package com.edtech.platform.quiz.dto.instructor;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;
import java.util.List;
import java.time.LocalDateTime;

@Data
@Builder
public class InstructorQuizResponse {
    private UUID id;
    private UUID lessonId;
    private String title;
    private String description;
    private Integer passScore;
    private Integer attemptsAllowed;
    private Integer timeLimitSeconds;
    private List<InstructorQuestionResponse> questions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
