package com.edtech.platform.course.service;

import com.edtech.platform.course.dto.SectionRequest;
import com.edtech.platform.course.dto.SectionResponse;
import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.entity.Section;
import com.edtech.platform.common.exception.EdTechException;
import com.edtech.platform.common.exception.ErrorCode;
import com.edtech.platform.course.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.edtech.platform.course.dto.SectionReorderRequest;
import java.util.Map;
import java.util.ArrayList;
import java.util.function.Function;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SectionService {
    private final SectionRepository sectionRepository;
    private final CourseService courseService;

    @Transactional
    public SectionResponse createSection(UUID instructorId, UUID courseId, SectionRequest request) {
        Course course = courseService.getCourseAndVerifyOwnership(instructorId, courseId);
        Section section = new Section();
        section.setCourse(course);
        course.getSections().add(section);
        section.setTitle(request.getTitle());
        section.setDescription(request.getDescription());
        section.setDisplayOrder(request.getDisplayOrder());
        
        courseService.downgradeIfPublished(course);
        section = sectionRepository.save(section);
        return mapToResponse(section);
    }

    @Transactional(readOnly = true)
    public List<SectionResponse> getSectionsByCourse(UUID instructorId, UUID courseId) {
        courseService.getCourseAndVerifyOwnership(instructorId, courseId);
        return sectionRepository.findByCourseIdOrderByDisplayOrderAsc(courseId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SectionResponse updateSection(UUID instructorId, UUID courseId, UUID sectionId, SectionRequest request) {
        Course course = courseService.getCourseAndVerifyOwnership(instructorId, courseId);
        Section section = getSection(sectionId, courseId);
        section.setTitle(request.getTitle());
        section.setDescription(request.getDescription());
        section.setDisplayOrder(request.getDisplayOrder());
        
        courseService.downgradeIfPublished(course);
        section = sectionRepository.save(section);
        return mapToResponse(section);
    }

    @Transactional
    public void deleteSection(UUID instructorId, UUID courseId, UUID sectionId) {
        Course course = courseService.getCourseAndVerifyOwnership(instructorId, courseId);
        Section section = getSection(sectionId, courseId);
        
        courseService.downgradeIfPublished(course);
        sectionRepository.delete(section);
    }

    
    @Transactional
    public void reorderSections(UUID instructorId, UUID courseId, SectionReorderRequest request) {
        Course course = courseService.getCourseAndVerifyOwnership(instructorId, courseId);
        List<Section> existingSections = sectionRepository.findByCourseId(courseId);
        
        if (existingSections.size() != request.getOrderedSectionIds().size()) {
            throw new EdTechException(ErrorCode.VALIDATION_FAILED, "Provided section IDs do not match the existing sections count");
        }
        
        Map<UUID, Section> sectionMap = existingSections.stream()
                .collect(Collectors.toMap(Section::getId, Function.identity()));
                
        for (UUID id : request.getOrderedSectionIds()) {
            if (!sectionMap.containsKey(id)) {
                throw new EdTechException(ErrorCode.VALIDATION_FAILED, "Invalid section ID provided: " + id);
            }
        }
        
        int order = 1;
        for (UUID id : request.getOrderedSectionIds()) {
            Section section = sectionMap.get(id);
            section.setDisplayOrder(order++);
        }
        
        sectionRepository.saveAll(existingSections);
        courseService.downgradeIfPublished(course);
    }

    public Section getSection(UUID sectionId, UUID courseId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new EdTechException(ErrorCode.RESOURCE_NOT_FOUND, "Section not found"));
        if (!section.getCourse().getId().equals(courseId)) {
            throw new EdTechException(ErrorCode.RESOURCE_NOT_FOUND, "Section does not belong to the specified course");
        }
        return section;
    }

    private SectionResponse mapToResponse(Section section) {
        SectionResponse response = new SectionResponse();
        response.setId(section.getId());
        response.setCourseId(section.getCourse().getId());
        response.setTitle(section.getTitle());
        response.setDescription(section.getDescription());
        response.setDisplayOrder(section.getDisplayOrder());
        response.setCreatedAt(section.getCreatedAt());
        response.setUpdatedAt(section.getUpdatedAt());
        return response;
    }
}
