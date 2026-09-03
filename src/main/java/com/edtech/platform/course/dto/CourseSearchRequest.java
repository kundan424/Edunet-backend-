package com.edtech.platform.course.dto;

import com.edtech.platform.course.enums.CourseDifficulty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CourseSearchRequest {
    private String search;
    private String category;
    private CourseDifficulty difficulty;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal minRating;
}
