package com.edtech.platform.email.service;

public interface EmailService {
    void sendEmail(String recipient, String subject, String body);
}
