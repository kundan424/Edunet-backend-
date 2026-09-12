package com.edtech.platform.email.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Service
@ConditionalOnProperty(name = "edtech.email.provider", havingValue = "mock", matchIfMissing = true)
@Slf4j
public class MockEmailService implements EmailService {

    @Override
    public void sendEmail(String recipient, String subject, String body) {
        log.info("========== MOCK EMAIL SENT ==========");
        log.info("To: {}", recipient);
        log.info("Subject: {}", subject);
        log.info("Body:\n{}", body);
        log.info("=====================================");
    }
}
