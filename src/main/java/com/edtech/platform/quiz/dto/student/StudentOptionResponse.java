package com.edtech.platform.quiz.dto.student;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class StudentOptionResponse {
    private UUID id;
    private String optionText;
    private Integer displayOrder;
}
