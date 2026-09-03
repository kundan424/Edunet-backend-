package com.edtech.platform.enrollment.dto;

import com.edtech.platform.enrollment.enums.EnrollmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class EnrollmentResponseDTO {
    private UUID id;
    private UUID courseId;
    private String courseTitle;
    private EnrollmentStatus status;
    private LocalDateTime enrolledAt;
}
