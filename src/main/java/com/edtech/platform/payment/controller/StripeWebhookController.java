package com.edtech.platform.payment.controller;

import com.edtech.platform.payment.service.StripeWebhookService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments/stripe")
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookController {

    private final StripeWebhookService stripeWebhookService;

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            HttpServletRequest request) {
        
        String sigHeader = request.getHeader("Stripe-Signature");
        if (sigHeader == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            stripeWebhookService.processWebhook(payload, sigHeader);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Webhook processing failed", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Webhook processing error", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
