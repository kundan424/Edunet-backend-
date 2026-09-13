package com.edtech.platform.admin.service;

import com.edtech.platform.admin.dto.CourseRejectionRequest;
import com.edtech.platform.common.exception.EdTechException;
import com.edtech.platform.common.exception.ErrorCode;
import com.edtech.platform.course.dto.CourseSummaryResponse;
import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.enums.PublishStatus;
import com.edtech.platform.course.event.CoursePublishedEvent;
import com.edtech.platform.course.event.CourseRejectedEvent;
import com.edtech.platform.course.repository.CourseRepository;
import com.edtech.platform.user.repository.UserRepository;
import com.edtech.platform.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.edtech.platform.course.service.CourseService;
import com.edtech.platform.course.dto.CourseCurriculumResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCourseModerationService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CourseService courseService;

    @Transactional(readOnly = true)
    public Page<CourseSummaryResponse> getPendingCourses(Pageable pageable) {
        Page<Course> courses = courseRepository.findByPublishStatus(PublishStatus.PENDING_APPROVAL, pageable);
        Map<UUID, String> instructorNames = userRepository.findAllById(
                        courses.stream().map(Course::getInstructorId).collect(Collectors.toSet()))
                .stream()
                .collect(Collectors.toMap(User::getId, User::getName));

        return courses.map(course -> CourseSummaryResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .category(course.getCategory())
                .difficulty(course.getDifficulty())
                .price(course.getPrice())
                .thumbnailUrl(course.getThumbnailUrl())
                .rating(course.getRating())
                .studentCount(course.getStudentCount())
                .reviewCount(course.getReviewCount())
                .instructorId(course.getInstructorId())
                .instructorName(instructorNames.getOrDefault(course.getInstructorId(), "Unknown Instructor"))
                .build());
    }

    @Transactional
    public void approveCourse(UUID courseId, UUID adminId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EdTechException(ErrorCode.COURSE_NOT_FOUND));

        if (course.getPublishStatus() != PublishStatus.PENDING_APPROVAL) {
            throw new EdTechException(ErrorCode.VALIDATION_FAILED, "Only PENDING_APPROVAL courses can be approved");
        }

        course.setPublishStatus(PublishStatus.PUBLISHED);
        course.setReviewedBy(adminId);
        course.setReviewedAt(LocalDateTime.now());
        course.setRejectionReason(null);
        courseRepository.save(course);

        eventPublisher.publishEvent(new CoursePublishedEvent(course.getId(), course.getInstructorId(), course.getTitle()));
    }

    @Transactional
    public void rejectCourse(UUID courseId, UUID adminId, CourseRejectionRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EdTechException(ErrorCode.COURSE_NOT_FOUND));

        if (course.getPublishStatus() != PublishStatus.PENDING_APPROVAL) {
            throw new EdTechException(ErrorCode.VALIDATION_FAILED, "Only PENDING_APPROVAL courses can be rejected");
        }

        course.setPublishStatus(PublishStatus.DRAFT);
        course.setReviewedBy(adminId);
        course.setReviewedAt(LocalDateTime.now());
        course.setRejectionReason(request.getReason());
        courseRepository.save(course);

        eventPublisher.publishEvent(new CourseRejectedEvent(course.getId(), course.getInstructorId(), course.getTitle(), request.getReason()));
    }

    @Transactional(readOnly = true)
    public CourseCurriculumResponse getCourseCurriculumForModeration(UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EdTechException(ErrorCode.COURSE_NOT_FOUND));

        if (course.getPublishStatus() != PublishStatus.PENDING_APPROVAL) {
            throw new EdTechException(ErrorCode.VALIDATION_FAILED, "Only PENDING_APPROVAL courses can be viewed for moderation");
        }

        return courseService.buildCourseCurriculumResponse(course);
    }
}
