package com.edtech.platform.payment.service;

import com.edtech.platform.enrollment.service.EnrollmentService;
import com.edtech.platform.payment.entity.Payment;
import com.edtech.platform.payment.entity.PaymentStatus;
import com.edtech.platform.payment.entity.StripeEvent;
import com.edtech.platform.payment.repository.PaymentRepository;
import com.edtech.platform.payment.repository.StripeEventRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookService {

    private final PaymentRepository paymentRepository;
    private final StripeEventRepository stripeEventRepository;
    private final EnrollmentService enrollmentService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Value("${edtech.stripe.webhook-secret}")
    private String webhookSecret;

    @Transactional
    public void processWebhook(String payload, String signature) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, signature, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.error("Invalid Stripe signature");
            throw new IllegalArgumentException("Invalid signature", e);
        } catch (Exception e) {
            log.error("Error parsing Stripe event");
            throw new IllegalArgumentException("Invalid payload", e);
        }

        String eventId = event.getId();

        // Idempotency check
        if (stripeEventRepository.existsById(eventId)) {
            log.info("Event {} already processed, skipping", eventId);
            return;
        }

        StripeEvent stripeEventEntity = new StripeEvent();
        stripeEventEntity.setEventId(eventId);
        stripeEventEntity.setType(event.getType());
        stripeEventEntity.setCreatedAt(LocalDateTime.now());
        stripeEventRepository.save(stripeEventEntity);

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session != null) {
                handleCheckoutSessionCompleted(session);
            } else {
                log.error("Deserialization failed for checkout.session.completed");
            }
        }
        // Additional event types like payment_intent.succeeded can be handled here
    }

    private void handleCheckoutSessionCompleted(Session session) {
        String checkoutSessionId = session.getId();
        Optional<Payment> optionalPayment = paymentRepository.findByCheckoutSessionId(checkoutSessionId);

        if (optionalPayment.isEmpty()) {
            log.error("Payment not found for checkout session: {}", checkoutSessionId);
            return;
        }

        Payment payment = optionalPayment.get();
        if (payment.getStatus() == PaymentStatus.SUCCEEDED) {
            log.info("Payment {} already marked as SUCCEEDED", payment.getId());
            return;
        }

        payment.setStatus(PaymentStatus.SUCCEEDED);
        payment.setPaymentIntentId(session.getPaymentIntent());
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);

        try {
            enrollmentService.enrollPaidStudent(payment.getUserId(), payment.getCourseId());
            log.info("Successfully enrolled user {} in course {}", payment.getUserId(), payment.getCourseId());
            
            applicationEventPublisher.publishEvent(new com.edtech.platform.payment.event.PaymentSucceededEvent(
                    payment.getUserId(), payment.getCourseId(), payment.getId(), "Course"));
        } catch (com.edtech.platform.common.exception.EdTechException e) {
            if (e.getErrorCode() == com.edtech.platform.common.exception.ErrorCode.ALREADY_ENROLLED) {
                log.info("User {} is already enrolled in course {}. Payment marked as SUCCEEDED.", payment.getUserId(), payment.getCourseId());
            } else {
                log.error("EdTechException enrolling user {} in course {} after successful payment: {}", payment.getUserId(), payment.getCourseId(), e.getMessage());
                throw e; // Rethrow to trigger rollback
            }
        } catch (Exception e) {
            log.error("Unexpected error enrolling user {} in course {} after successful payment", payment.getUserId(), payment.getCourseId(), e);
            throw new RuntimeException("Failed to create enrollment, triggering rollback", e);
        }
    }
}
