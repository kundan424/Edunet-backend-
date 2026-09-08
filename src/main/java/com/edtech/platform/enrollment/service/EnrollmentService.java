package com.edtech.platform.enrollment.service;

import com.edtech.platform.common.exception.EdTechException;
import com.edtech.platform.common.exception.ErrorCode;
import com.edtech.platform.common.exception.ResourceNotFoundException;
import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.entity.Lesson;
import com.edtech.platform.course.entity.Section;
import com.edtech.platform.course.enums.PublishStatus;
import com.edtech.platform.course.repository.CourseRepository;
import com.edtech.platform.enrollment.dto.CourseLearningResponse;
import com.edtech.platform.enrollment.dto.EnrollmentResponseDTO;
import com.edtech.platform.enrollment.dto.LessonLearningDTO;
import com.edtech.platform.enrollment.dto.SectionLearningDTO;
import com.edtech.platform.enrollment.entity.Enrollment;
import com.edtech.platform.enrollment.repository.EnrollmentRepository;
import com.edtech.platform.user.entity.User;
import com.edtech.platform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Transactional
    public EnrollmentResponseDTO enroll(UUID userId, UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));

        if (course.getPublishStatus() != PublishStatus.PUBLISHED) {
            throw new EdTechException(ErrorCode.COURSE_NOT_PUBLISHED, "Course is not published");
        }

        if (course.getPrice() != null && course.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            throw new EdTechException(ErrorCode.VALIDATION_FAILED, "Paid enrollment not supported");
        }

        return createEnrollment(userId, course);
    }

    @Transactional
    public EnrollmentResponseDTO enrollPaidStudent(UUID userId, UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));

        if (course.getPublishStatus() != PublishStatus.PUBLISHED) {
            throw new EdTechException(ErrorCode.COURSE_NOT_PUBLISHED, "Course is not published");
        }

        return createEnrollment(userId, course);
    }

    private EnrollmentResponseDTO createEnrollment(UUID userId, Course course) {
        if (enrollmentRepository.existsByUserIdAndCourseId(userId, course.getId())) {
            throw new EdTechException(ErrorCode.ALREADY_ENROLLED, "You are already enrolled in this course");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        enrollment.setCourse(course);
        enrollment = enrollmentRepository.save(enrollment);

        courseRepository.incrementStudentCount(course.getId());

        return mapToDTO(enrollment);
    }

    @Transactional(readOnly = true)
    public Page<EnrollmentResponseDTO> getMyEnrollments(UUID userId, Pageable pageable) {
        return enrollmentRepository.findByUserId(userId, pageable)
                .map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public EnrollmentResponseDTO getEnrollmentById(UUID enrollmentId, UUID userId) {
        Enrollment enrollment = enrollmentRepository.findByIdAndUserId(enrollmentId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", enrollmentId));
        return mapToDTO(enrollment);
    }

    @Transactional(readOnly = true)
    public CourseLearningResponse getCourseLearningMaterial(UUID courseId, UUID userId) {
        if (!enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new EdTechException(ErrorCode.NOT_ENROLLED, "You are not enrolled in this course");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));

        return CourseLearningResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .sections(course.getSections().stream().map(this::mapSection).collect(Collectors.toList()))
                .build();
    }

    private EnrollmentResponseDTO mapToDTO(Enrollment enrollment) {
        return EnrollmentResponseDTO.builder()
                .id(enrollment.getId())
                .courseId(enrollment.getCourse().getId())
                .courseTitle(enrollment.getCourse().getTitle())
                .status(enrollment.getStatus())
                .enrolledAt(enrollment.getEnrolledAt())
                .build();
    }

    private SectionLearningDTO mapSection(Section section) {
        return SectionLearningDTO.builder()
                .id(section.getId())
                .title(section.getTitle())
                .displayOrder(section.getDisplayOrder())
                .lessons(section.getLessons().stream().map(this::mapLesson).collect(Collectors.toList()))
                .build();
    }

    private LessonLearningDTO mapLesson(Lesson lesson) {
        return LessonLearningDTO.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .description(lesson.getDescription())
                .lessonType(lesson.getLessonType())
                .displayOrder(lesson.getDisplayOrder())
                .build();
    }
}
