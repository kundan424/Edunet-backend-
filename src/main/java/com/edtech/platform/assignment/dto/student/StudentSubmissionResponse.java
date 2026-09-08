package com.edtech.platform.assignment.dto.student;

import com.edtech.platform.assignment.enums.SubmissionStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class StudentSubmissionResponse {
    private UUID id;
    private UUID assignmentId;
    private String submissionText;
    private SubmissionStatus status;
    private Integer score;
    private String feedback;
    private LocalDateTime gradedAt;
    private LocalDateTime submittedAt;
}
