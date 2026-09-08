package com.edtech.platform.assignment.dto.shared;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AssignmentResponse {
    private UUID id;
    private UUID lessonId;
    private String title;
    private String instructions;
    private Integer maxScore;
    private LocalDateTime dueAt;
    private LocalDateTime createdAt;
}
