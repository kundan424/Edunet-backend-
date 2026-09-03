package com.edtech.platform.course.dto;

import com.edtech.platform.course.enums.CourseDifficulty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CourseUpdateRequest {
    @NotBlank
    private String title;
    private String description;
    private String category;
    private CourseDifficulty difficulty;
    @DecimalMin("0.0")
    private BigDecimal price;
    private String thumbnailUrl;
}
