package com.edtech.platform.common.health;

import com.edtech.platform.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Simple health check endpoint.
 * The Spring Actuator health endpoint is also available at /actuator/health.
 * This endpoint provides a simpler application-level check.
 */
@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthController {

    @GetMapping
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> data = Map.of(
                "status", "UP",
                "service", "edtech-platform",
                "timestamp", Instant.now().toString()
        );
        return ApiResponse.success(data);
    }
}