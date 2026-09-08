package com.edtech.platform.quiz.dto.instructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuizCreateRequest {
    @NotBlank
    private String title;
    
    private String description;
    
    @NotNull
    @Min(1)
    private Integer passScore;
    
    @NotNull
    @Min(1)
    private Integer attemptsAllowed;
    
    private Integer timeLimitSeconds;
}
