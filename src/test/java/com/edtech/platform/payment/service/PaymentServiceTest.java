package com.edtech.platform.payment.service;

import com.edtech.platform.common.exception.EdTechException;
import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.enums.PublishStatus;
import com.edtech.platform.course.repository.CourseRepository;
import com.edtech.platform.enrollment.repository.EnrollmentRepository;
import com.edtech.platform.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private UUID userId;
    private UUID courseId;
    private Course course;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        courseId = UUID.randomUUID();

        course = new Course();
        course.setId(courseId);
        course.setPublishStatus(PublishStatus.PUBLISHED);
        course.setPrice(new BigDecimal("10.00"));

        ReflectionTestUtils.setField(paymentService, "stripeSecretKey", "sk_test_123");
        ReflectionTestUtils.setField(paymentService, "frontendUrl", "http://localhost:3000");
    }

    @Test
    void createCheckoutSession_WhenCourseNotPublished_ShouldThrowException() {
        course.setPublishStatus(PublishStatus.DRAFT);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        assertThrows(EdTechException.class, () -> paymentService.createCheckoutSession(userId, courseId));
    }

    @Test
    void createCheckoutSession_WhenCourseIsFree_ShouldThrowException() {
        course.setPrice(BigDecimal.ZERO);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        assertThrows(EdTechException.class, () -> paymentService.createCheckoutSession(userId, courseId));
    }
}
