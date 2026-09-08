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
        lesson = lessonRepository.save(lesson);
        return mapToResponse(lesson);
    }

    @Transactional
    public void deleteLesson(UUID instructorId, UUID courseId, UUID sectionId, UUID lessonId) {
        courseService.getCourseAndVerifyOwnership(instructorId, courseId);
        sectionService.getSection(sectionId, courseId);
        Lesson lesson = getLesson(lessonId, sectionId);
        lessonRepository.delete(lesson);
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
