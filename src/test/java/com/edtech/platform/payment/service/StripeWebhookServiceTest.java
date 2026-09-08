package com.edtech.platform.payment.service;

import com.edtech.platform.enrollment.service.EnrollmentService;
import com.edtech.platform.payment.entity.Payment;
import com.edtech.platform.payment.entity.PaymentStatus;
import com.edtech.platform.payment.repository.PaymentRepository;
import com.edtech.platform.payment.repository.StripeEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StripeWebhookServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private StripeEventRepository stripeEventRepository;

    @Mock
    private EnrollmentService enrollmentService;

    @InjectMocks
    private StripeWebhookService stripeWebhookService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(stripeWebhookService, "webhookSecret", "whsec_test_123");
    }

    @Test
    void processWebhook_WhenInvalidSignature_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                stripeWebhookService.processWebhook("payload", "invalid_sig"));
    }
}
