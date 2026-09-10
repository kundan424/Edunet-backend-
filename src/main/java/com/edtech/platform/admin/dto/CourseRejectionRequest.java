package com.edtech.platform.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CourseRejectionRequest {
    @NotBlank(message = "Rejection reason is required")
    @Size(max = 1000, message = "Reason must not exceed 1000 characters")
    private String reason;
}
