package com.edtech.platform.quiz.dto.instructor;

import com.edtech.platform.quiz.enums.QuestionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class QuestionCreateRequest {
    @NotBlank
    private String questionText;
    
    @NotNull
    private QuestionType questionType;
    
    @NotNull
    @Min(1)
    private Integer points;
    
    @NotNull
    @Min(1)
    private Integer displayOrder;
    
    private List<OptionCreateRequest> options;
}
