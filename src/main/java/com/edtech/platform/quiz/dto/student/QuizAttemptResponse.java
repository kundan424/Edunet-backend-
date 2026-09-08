package com.edtech.platform.quiz.dto.student;

import com.edtech.platform.quiz.enums.AttemptStatus;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Builder
public class QuizAttemptResponse {
    private UUID id;
    private UUID quizId;
    private Integer attemptNumber;
    private AttemptStatus status;
    private Integer score;
    private Double percentage;
    private Boolean passed;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
