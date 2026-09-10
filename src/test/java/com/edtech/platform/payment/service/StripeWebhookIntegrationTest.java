package com.edtech.platform.payment.service;

import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.enums.PublishStatus;
import com.edtech.platform.course.repository.CourseRepository;
import com.edtech.platform.enrollment.repository.EnrollmentRepository;
import com.edtech.platform.payment.entity.Payment;
import com.edtech.platform.payment.entity.PaymentStatus;
import com.edtech.platform.payment.repository.PaymentRepository;
import com.edtech.platform.payment.repository.StripeEventRepository;
import com.edtech.platform.user.entity.Role;
import com.edtech.platform.user.entity.User;
import com.edtech.platform.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class StripeWebhookIntegrationTest {

    @Autowired
    private StripeWebhookService stripeWebhookService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StripeEventRepository stripeEventRepository;

    private User student;
    private Course course;
    private Payment payment;

    @Autowired
    private com.edtech.platform.user.repository.InstructorProfileRepository instructorProfileRepository;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
        enrollmentRepository.deleteAll();
        courseRepository.deleteAll();
        instructorProfileRepository.deleteAll();
        userRepository.deleteAll();
        stripeEventRepository.deleteAll();

        student = new User();
        student.setEmail("webhookstudent@test.com");
        student.setName("Student");
        student.setPasswordHash("hash");
        student.setRole(Role.STUDENT);
        student.setStatus(com.edtech.platform.user.entity.UserStatus.ACTIVE);
        student = userRepository.save(student);

        User instructor = new User();
        instructor.setEmail("instructor_" + UUID.randomUUID() + "@test.com");
        instructor.setName("Instructor");
        instructor.setPasswordHash("hash");
        instructor.setRole(Role.INSTRUCTOR);
        instructor.setStatus(com.edtech.platform.user.entity.UserStatus.ACTIVE);
        instructor = userRepository.save(instructor);

        course = new Course();
        course.setTitle("Paid Course");
        course.setPrice(new BigDecimal("29.99"));
        course.setPublishStatus(PublishStatus.PUBLISHED);
        course.setInstructorId(instructor.getId());
        course.setStudentCount(0);
        course.setReviewCount(0);
        course = courseRepository.save(course);

        payment = new Payment();
        payment.setUserId(student.getId());
        payment.setCourseId(course.getId());
        payment.setAmount(new BigDecimal("29.99"));
        payment.setCurrency("USD");
        payment.setStatus(PaymentStatus.CREATED);
        payment.setProvider("STRIPE");
        payment.setCheckoutSessionId("cs_test_123");
        payment = paymentRepository.save(payment);
    }

    private String generateWebhookSignature(String payload, String secret) throws Exception {
        long timestamp = System.currentTimeMillis() / 1000L;
        String payloadToSign = timestamp + "." + payload;
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
        mac.init(new javax.crypto.spec.SecretKeySpec(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] hash = mac.doFinal(payloadToSign.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return "t=" + timestamp + ",v1=" + hexString.toString();
    }

    @Test
    void testSuccessfulPaymentAndDuplicateWebhook() throws Exception {
        String secret = "whsec_test_123";
        ReflectionTestUtils.setField(stripeWebhookService, "webhookSecret", secret);

        String payload = "{\n" +
                "  \"id\": \"evt_test_123\",\n" +
                "  \"object\": \"event\",\n" +
                "  \"api_version\": \"2023-10-16\",\n" +
                "  \"type\": \"checkout.session.completed\",\n" +
                "  \"data\": {\n" +
                "    \"object\": {\n" +
                "      \"id\": \"cs_test_123\",\n" +
                "      \"object\": \"checkout.session\",\n" +
                "      \"payment_intent\": \"pi_test_123\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        String signature = generateWebhookSignature(payload, secret);

        stripeWebhookService.processWebhook(payload, signature);

        Payment updatedPayment = paymentRepository.findById(payment.getId()).get();
        assertEquals(PaymentStatus.SUCCEEDED, updatedPayment.getStatus());

        var enrollments = enrollmentRepository.findAll();
        assertEquals(1, enrollments.size());
        assertEquals(student.getId(), enrollments.get(0).getUser().getId());
        assertEquals(course.getId(), enrollments.get(0).getCourse().getId());

        Course updatedCourse = courseRepository.findById(course.getId()).get();
        assertEquals(1, updatedCourse.getStudentCount());

        stripeWebhookService.processWebhook(payload, signature);

        assertEquals(1, enrollmentRepository.findAll().size());
        assertEquals(1, courseRepository.findById(course.getId()).get().getStudentCount());
    }

    @Test
    void testFailedEventDoesNotCreateEnrollment() throws Exception {
        String secret = "whsec_test_123";
        ReflectionTestUtils.setField(stripeWebhookService, "webhookSecret", secret);

        String payload = "{\n" +
                "  \"id\": \"evt_test_failed\",\n" +
                "  \"type\": \"payment_intent.payment_failed\",\n" +
                "  \"data\": {\n" +
                "    \"object\": {\n" +
                "      \"id\": \"pi_test_123\",\n" +
                "      \"object\": \"payment_intent\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        String signature = generateWebhookSignature(payload, secret);

        stripeWebhookService.processWebhook(payload, signature);

        assertEquals(0, enrollmentRepository.findAll().size());
        assertEquals(0, courseRepository.findById(course.getId()).get().getStudentCount());
    }
}
