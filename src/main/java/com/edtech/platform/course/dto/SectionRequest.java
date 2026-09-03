package com.edtech.platform.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SectionRequest {
    @NotBlank
    private String title;
    private String description;
    @NotNull
    private Integer displayOrder;
}
