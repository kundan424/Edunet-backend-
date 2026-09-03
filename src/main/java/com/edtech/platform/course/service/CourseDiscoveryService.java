package com.edtech.platform.course.service;

import com.edtech.platform.common.exception.ResourceNotFoundException;
import com.edtech.platform.course.dto.*;
import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.enums.PublishStatus;
import com.edtech.platform.course.repository.CourseRepository;
import com.edtech.platform.course.repository.CourseSpecification;
import com.edtech.platform.user.entity.InstructorProfile;
import com.edtech.platform.user.repository.InstructorProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseDiscoveryService {

    private final CourseRepository courseRepository;
    private final InstructorProfileRepository instructorProfileRepository;

    @Transactional(readOnly = true)
    public Page<CourseSummaryResponse> searchCourses(CourseSearchRequest request, Pageable pageable) {
        Page<Course> courses = courseRepository.findAll(CourseSpecification.getCoursesByFilter(request), pageable);

        List<UUID> instructorIds = courses.getContent().stream()
                .map(Course::getInstructorId)
                .distinct()
                .toList();

        Map<UUID, String> instructorNames = instructorProfileRepository.findByUserIdInWithUser(instructorIds)
                .stream()
                .collect(Collectors.toMap(
                        ip -> ip.getUser().getId(),
                        ip -> ip.getUser().getName()
                ));

        return courses.map(course -> CourseSummaryResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .category(course.getCategory())
                .difficulty(course.getDifficulty())
                .price(course.getPrice())
                .thumbnailUrl(course.getThumbnailUrl())
                .rating(course.getRating())
                .studentCount(course.getStudentCount())
                .instructorId(course.getInstructorId())
                .instructorName(instructorNames.getOrDefault(course.getInstructorId(), "Unknown Instructor"))
                .build());
    }

    @Transactional(readOnly = true)
    public CourseDetailResponse getCourseDetail(UUID courseId) {
        Course course = courseRepository.findByIdAndPublishStatus(courseId, PublishStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found or not published"));

        InstructorProfile instructorProfile = instructorProfileRepository.findByUserIdInWithUser(List.of(course.getInstructorId()))
                .stream().findFirst().orElse(null);

        String instructorName = "Unknown Instructor";
        String instructorBio = null;
        List<String> instructorExpertise = null;

        if (instructorProfile != null) {
            instructorName = instructorProfile.getUser().getName();
            instructorBio = instructorProfile.getBio();
            if (instructorProfile.getExpertise() != null && !instructorProfile.getExpertise().isBlank()) {
                instructorExpertise = java.util.Arrays.asList(instructorProfile.getExpertise().split("\\s*,\\s*"));
            } else {
                instructorExpertise = java.util.Collections.emptyList();
            }
        }

        List<PublicSectionResponse> sectionResponses = course.getSections().stream()
                .sorted(Comparator.comparing(section -> section.getDisplayOrder()))
                .map(section -> PublicSectionResponse.builder()
                        .id(section.getId())
                        .title(section.getTitle())
                        .description(section.getDescription())
                        .displayOrder(section.getDisplayOrder())
                        .lessons(section.getLessons().stream()
                                .sorted(Comparator.comparing(lesson -> lesson.getDisplayOrder()))
                                .map(lesson -> PublicLessonResponse.builder()
                                        .id(lesson.getId())
                                        .title(lesson.getTitle())
                                        .description(lesson.getDescription())
                                        .lessonType(lesson.getLessonType())
                                        .displayOrder(lesson.getDisplayOrder())
                                        .build())
                                .toList())
                        .build())
                .toList();

        return CourseDetailResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .category(course.getCategory())
                .difficulty(course.getDifficulty())
                .price(course.getPrice())
                .thumbnailUrl(course.getThumbnailUrl())
                .rating(course.getRating())
                .studentCount(course.getStudentCount())
                .instructorId(course.getInstructorId())
                .instructorName(instructorName)
                .instructorBio(instructorBio)
                .instructorExpertise(instructorExpertise)
                .sections(sectionResponses)
                .build();
    }
}
