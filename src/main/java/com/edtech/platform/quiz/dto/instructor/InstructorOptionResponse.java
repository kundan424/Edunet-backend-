package com.edtech.platform.quiz.dto.instructor;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class InstructorOptionResponse {
    private UUID id;
    private String optionText;
    private Integer displayOrder;
    private Boolean isCorrect;
}
