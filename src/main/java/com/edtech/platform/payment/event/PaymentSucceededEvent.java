package com.edtech.platform.payment.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class PaymentSucceededEvent {
    private final UUID userId;
    private final UUID courseId;
    private final UUID paymentId;
    private final String courseTitle;
}
