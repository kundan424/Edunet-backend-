package com.edtech.platform.payment.controller;

import com.edtech.platform.payment.dto.CheckoutResponseDTO;
import com.edtech.platform.payment.dto.PaymentResponseDTO;
import com.edtech.platform.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StudentPaymentController {

    private final PaymentService paymentService;

    @PostMapping("/courses/{courseId}/checkout")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<CheckoutResponseDTO> createCheckoutSession(
            @PathVariable UUID courseId,
            @AuthenticationPrincipal com.edtech.platform.common.security.UserDetailsImpl userDetails) {
        UUID userId = userDetails.getId();
        return ResponseEntity.ok(paymentService.createCheckoutSession(userId, courseId));
    }

    @GetMapping("/payments")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<PaymentResponseDTO>> getMyPayments(@AuthenticationPrincipal com.edtech.platform.common.security.UserDetailsImpl userDetails) {
        UUID userId = userDetails.getId();
        return ResponseEntity.ok(paymentService.getMyPayments(userId));
    }

    @GetMapping("/payments/{paymentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PaymentResponseDTO> getPayment(
            @PathVariable UUID paymentId,
            @AuthenticationPrincipal com.edtech.platform.common.security.UserDetailsImpl userDetails) {
        UUID userId = userDetails.getId();
        return ResponseEntity.ok(paymentService.getPayment(paymentId, userId));
    }
}
