package com.edtech.platform.quiz.dto.student;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;
import java.util.List;

@Data
public class QuizSubmitRequest {
    @NotNull
    private List<AnswerSubmission> answers;

    @Data
    public static class AnswerSubmission {
        @NotNull
        private UUID questionId;
        
        @NotNull
        private List<UUID> selectedOptionIds;
    }
}
