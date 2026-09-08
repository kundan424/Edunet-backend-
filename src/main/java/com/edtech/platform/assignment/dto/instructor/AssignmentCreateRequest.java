package com.edtech.platform.assignment.dto.instructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AssignmentCreateRequest {
    @NotBlank
    private String title;
    
    @NotBlank
    private String instructions;
    
    @NotNull
    @Min(1)
    private Integer maxScore;
    
    private LocalDateTime dueAt;
}
