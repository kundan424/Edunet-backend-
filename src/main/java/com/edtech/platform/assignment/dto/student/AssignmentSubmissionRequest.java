package com.edtech.platform.assignment.dto.student;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssignmentSubmissionRequest {
    @NotBlank
    private String submissionText;
}
