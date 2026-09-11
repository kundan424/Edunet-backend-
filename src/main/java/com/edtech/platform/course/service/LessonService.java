package com.edtech.platform.course.service;

import com.edtech.platform.course.dto.LessonRequest;
import com.edtech.platform.course.dto.LessonResponse;
import com.edtech.platform.course.entity.Lesson;
import com.edtech.platform.course.entity.Section;
import com.edtech.platform.common.exception.EdTechException;
import com.edtech.platform.common.exception.ErrorCode;
import com.edtech.platform.course.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.edtech.platform.course.dto.LessonReorderRequest;
import com.edtech.platform.course.entity.Course;
import java.util.Map;
import java.util.ArrayList;
import java.util.function.Function;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final SectionService sectionService;
    private final CourseService courseService;

    @Transactional
    public LessonResponse createLesson(UUID instructorId, UUID courseId, UUID sectionId, LessonRequest request) {
        courseService.getCourseAndVerifyOwnership(instructorId, courseId);
        Section section = sectionService.getSection(sectionId, courseId);
        Lesson lesson = new Lesson();
        lesson.setSection(section);
        section.getLessons().add(lesson);
        lesson.setTitle(request.getTitle());
        lesson.setDescription(request.getDescription());
        lesson.setLessonType(request.getLessonType());
        lesson.setDisplayOrder(request.getDisplayOrder());
        lesson.setDurationSeconds(request.getDurationSeconds());
        
        courseService.downgradeIfPublished(lesson.getSection().getCourse());
        lesson = lessonRepository.save(lesson);
        return mapToResponse(lesson);
    }

    @Transactional(readOnly = true)
    public List<LessonResponse> getLessonsBySection(UUID instructorId, UUID courseId, UUID sectionId) {
        courseService.getCourseAndVerifyOwnership(instructorId, courseId);
        sectionService.getSection(sectionId, courseId);
        return lessonRepository.findBySectionIdOrderByDisplayOrderAsc(sectionId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public LessonResponse updateLesson(UUID instructorId, UUID courseId, UUID sectionId, UUID lessonId, LessonRequest request) {
        courseService.getCourseAndVerifyOwnership(instructorId, courseId);
        sectionService.getSection(sectionId, courseId);
        Lesson lesson = getLesson(lessonId, sectionId);
        lesson.setTitle(request.getTitle());
        lesson.setDescription(request.getDescription());
        lesson.setLessonType(request.getLessonType());
        lesson.setDisplayOrder(request.getDisplayOrder());
        lesson.setDurationSeconds(request.getDurationSeconds());
        
        courseService.downgradeIfPublished(lesson.getSection().getCourse());
        lesson = lessonRepository.save(lesson);
        return mapToResponse(lesson);
    }

    @Transactional
    public void deleteLesson(UUID instructorId, UUID courseId, UUID sectionId, UUID lessonId) {
        courseService.getCourseAndVerifyOwnership(instructorId, courseId);
        sectionService.getSection(sectionId, courseId);
        Lesson lesson = getLesson(lessonId, sectionId);
        
        courseService.downgradeIfPublished(lesson.getSection().getCourse());
        lessonRepository.delete(lesson);
    }

    
    @Transactional
    public void reorderLessons(UUID instructorId, UUID courseId, UUID sectionId, LessonReorderRequest request) {
        courseService.getCourseAndVerifyOwnership(instructorId, courseId);
        Section section = sectionService.getSection(sectionId, courseId);
        
        List<Lesson> existingLessons = lessonRepository.findBySectionId(sectionId);
        
        if (existingLessons.size() != request.getOrderedLessonIds().size()) {
            throw new EdTechException(ErrorCode.VALIDATION_FAILED, "Provided lesson IDs do not match the existing lessons count");
        }
        
        Map<UUID, Lesson> lessonMap = existingLessons.stream()
                .collect(Collectors.toMap(Lesson::getId, Function.identity()));
                
        for (UUID id : request.getOrderedLessonIds()) {
            if (!lessonMap.containsKey(id)) {
                throw new EdTechException(ErrorCode.VALIDATION_FAILED, "Invalid lesson ID provided: " + id);
            }
        }
        
        int order = 1;
        for (UUID id : request.getOrderedLessonIds()) {
            Lesson lesson = lessonMap.get(id);
            lesson.setDisplayOrder(order++);
        }
        
        lessonRepository.saveAll(existingLessons);
        courseService.downgradeIfPublished(section.getCourse());
    }

    private Lesson getLesson(UUID lessonId, UUID sectionId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EdTechException(ErrorCode.RESOURCE_NOT_FOUND, "Lesson not found"));
        if (!lesson.getSection().getId().equals(sectionId)) {
            throw new EdTechException(ErrorCode.RESOURCE_NOT_FOUND, "Lesson does not belong to the specified section");
        }
        return lesson;
    }

    private LessonResponse mapToResponse(Lesson lesson) {
        LessonResponse response = new LessonResponse();
        response.setId(lesson.getId());
        response.setSectionId(lesson.getSection().getId());
        response.setTitle(lesson.getTitle());
        response.setDescription(lesson.getDescription());
        response.setLessonType(lesson.getLessonType());
        response.setDisplayOrder(lesson.getDisplayOrder());
        response.setDurationSeconds(lesson.getDurationSeconds());
        response.setCreatedAt(lesson.getCreatedAt());
        response.setUpdatedAt(lesson.getUpdatedAt());
        return response;
    }
}
