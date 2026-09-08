package com.edtech.platform.payment.service;

import com.edtech.platform.common.exception.EdTechException;
import com.edtech.platform.common.exception.ErrorCode;
import com.edtech.platform.common.exception.ResourceNotFoundException;
import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.enums.PublishStatus;
import com.edtech.platform.course.repository.CourseRepository;
import com.edtech.platform.enrollment.repository.EnrollmentRepository;
import com.edtech.platform.payment.dto.CheckoutResponseDTO;
import com.edtech.platform.payment.dto.PaymentResponseDTO;
import com.edtech.platform.payment.entity.Payment;
import com.edtech.platform.payment.entity.PaymentStatus;
import com.edtech.platform.payment.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Value("${edtech.stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${edtech.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Transactional
    public CheckoutResponseDTO createCheckoutSession(UUID userId, UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));

        if (course.getPublishStatus() != PublishStatus.PUBLISHED) {
            throw new EdTechException(ErrorCode.COURSE_NOT_PUBLISHED, "Course is not published");
        }

        if (course.getPrice() == null || course.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new EdTechException(ErrorCode.VALIDATION_FAILED, "Free courses do not require payment");
        }

        if (enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new EdTechException(ErrorCode.ALREADY_ENROLLED, "You are already enrolled in this course");
        }

        long amountInCents = course.getPrice().multiply(new BigDecimal("100")).longValueExact();

        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(frontendUrl + "/courses/" + courseId + "/payment/success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(frontendUrl + "/courses/" + courseId + "/payment/cancel")
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("usd")
                                    .setUnitAmount(amountInCents)
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName(course.getTitle())
                                            .build())
                                    .build())
                            .build())
                    .putMetadata("userId", userId.toString())
                    .putMetadata("courseId", courseId.toString())
                    .build();

            Session session = Session.create(params);

            Payment payment = new Payment();
            payment.setUserId(userId);
            payment.setCourseId(courseId);
            payment.setProvider("STRIPE");
            payment.setCheckoutSessionId(session.getId());
            payment.setAmount(course.getPrice());
            payment.setCurrency("USD");
            payment.setStatus(PaymentStatus.CREATED);

            paymentRepository.save(payment);

            return new CheckoutResponseDTO(session.getUrl());

        } catch (StripeException e) {
            log.error("Stripe error while creating checkout session", e);
            throw new EdTechException(ErrorCode.INTERNAL_SERVER_ERROR, "Payment service unavailable");
        }
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getMyPayments(UUID userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PaymentResponseDTO getPayment(UUID paymentId, UUID userId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));
                
        if (!payment.getUserId().equals(userId)) {
            throw new EdTechException(ErrorCode.FORBIDDEN, "You don't have access to this payment");
        }

        return mapToDTO(payment);
    }

    private PaymentResponseDTO mapToDTO(Payment payment) {
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .userId(payment.getUserId())
                .courseId(payment.getCourseId())
                .provider(payment.getProvider())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .paidAt(payment.getPaidAt())
                .build();
    }
}
