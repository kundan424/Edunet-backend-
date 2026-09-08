package com.edtech.platform.quiz.dto.instructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OptionCreateRequest {
    @NotBlank
    private String optionText;
    
    @NotNull
    private Integer displayOrder;
    
    @NotNull
    private Boolean isCorrect;
}
