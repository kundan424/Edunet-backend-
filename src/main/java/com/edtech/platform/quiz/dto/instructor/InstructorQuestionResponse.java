package com.edtech.platform.quiz.dto.instructor;

import com.edtech.platform.quiz.enums.QuestionType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;
import java.util.List;

@Data
@Builder
public class InstructorQuestionResponse {
    private UUID id;
    private String questionText;
    private QuestionType questionType;
    private Integer points;
    private Integer displayOrder;
    private List<InstructorOptionResponse> options;
}
