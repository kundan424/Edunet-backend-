package com.edtech.platform.course.service;

import com.edtech.platform.common.exception.EdTechException;
import com.edtech.platform.common.exception.ErrorCode;
import com.edtech.platform.course.dto.CourseReviewRequest;
import com.edtech.platform.course.dto.CourseReviewResponse;
import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.entity.CourseReview;
import com.edtech.platform.course.repository.CourseRepository;
import com.edtech.platform.course.repository.CourseReviewRepository;
import com.edtech.platform.enrollment.enums.EnrollmentStatus;
import com.edtech.platform.enrollment.repository.EnrollmentRepository;
import com.edtech.platform.user.entity.User;
import com.edtech.platform.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseReviewServiceTest {

    @Mock
    private CourseReviewRepository courseReviewRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CourseReviewService courseReviewService;

    private UUID userId;
    private UUID courseId;
    private User user;
    private Course course;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        courseId = UUID.randomUUID();

        user = new User();
        user.setId(userId);
        user.setName("Test Student");

        course = new Course();
        course.setId(courseId);
        course.setReviewCount(0);
    }

    @Test
    void testCreateReview_Success() {
        CourseReviewRequest request = new CourseReviewRequest();
        request.setRating(4);
        request.setComment("Great!");

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByUserIdAndCourseIdAndStatus(userId, courseId, EnrollmentStatus.ACTIVE))
                .thenReturn(true);
        when(courseReviewRepository.existsByUserIdAndCourseId(userId, courseId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        CourseReview savedReview = new CourseReview();
        savedReview.setId(UUID.randomUUID());
        savedReview.setCourse(course);
        savedReview.setUser(user);
        savedReview.setRating(4);
        savedReview.setComment("Great!");
        when(courseReviewRepository.save(any(CourseReview.class))).thenReturn(savedReview);

        CourseReviewResponse response = courseReviewService.createReview(userId, courseId, request);

        assertNotNull(response);
        assertEquals(4, response.getRating());
        assertEquals(1, course.getReviewCount());
        assertEquals(BigDecimal.valueOf(4.0).setScale(1), course.getRating());
        verify(courseRepository).save(course);
    }

    @Test
    void testCreateReview_NotEnrolled_ThrowsException() {
        CourseReviewRequest request = new CourseReviewRequest();
        request.setRating(4);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByUserIdAndCourseIdAndStatus(userId, courseId, EnrollmentStatus.ACTIVE))
                .thenReturn(false);

        EdTechException ex = assertThrows(EdTechException.class, () ->
                courseReviewService.createReview(userId, courseId, request));
        assertEquals(ErrorCode.FORBIDDEN, ex.getErrorCode());
    }

    @Test
    void testCreateReview_DuplicateReview_ThrowsException() {
        CourseReviewRequest request = new CourseReviewRequest();
        request.setRating(4);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByUserIdAndCourseIdAndStatus(userId, courseId, EnrollmentStatus.ACTIVE))
                .thenReturn(true);
        when(courseReviewRepository.existsByUserIdAndCourseId(userId, courseId)).thenReturn(true);

        EdTechException ex = assertThrows(EdTechException.class, () ->
                courseReviewService.createReview(userId, courseId, request));
        assertEquals(ErrorCode.VALIDATION_FAILED, ex.getErrorCode());
    }
}
