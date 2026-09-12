package com.edtech.platform.email.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
@Slf4j
@ConditionalOnProperty(name = "edtech.email.provider", havingValue = "resend")
public class ResendEmailServiceImpl implements EmailService {

    @Value("${edtech.email.resend-api-key}")
    private String apiKey;

    @Value("${edtech.email.from}")
    private String fromEmail;

    private final HttpClient httpClient;
    private final Gson gson;

    public ResendEmailServiceImpl() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
    }

    @Override
    public void sendEmail(String recipient, String subject, String body) {
        try {
            JsonObject payload = new JsonObject();
            payload.addProperty("from", fromEmail);
            payload.add("to", gson.toJsonTree(new String[]{recipient}));
            payload.addProperty("subject", subject);
            payload.addProperty("text", body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(payload)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("Successfully sent email via Resend to {}", recipient);
            } else {
                log.error("Failed to send email via Resend. Status: {}, Response: {}", response.statusCode(), response.body());
            }
        } catch (Exception e) {
            log.error("Exception while sending email via Resend", e);
        }
    }
}
