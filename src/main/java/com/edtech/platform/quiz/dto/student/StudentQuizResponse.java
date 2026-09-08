package com.edtech.platform.quiz.dto.student;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;
import java.util.List;

@Data
@Builder
public class StudentQuizResponse {
    private UUID id;
    private String title;
    private String description;
    private Integer passScore;
    private Integer attemptsAllowed;
    private Integer timeLimitSeconds;
    private List<StudentQuestionResponse> questions;
}
