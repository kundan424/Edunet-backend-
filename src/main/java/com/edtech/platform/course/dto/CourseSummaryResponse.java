package com.edtech.platform.course.dto;

import com.edtech.platform.course.enums.CourseDifficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSummaryResponse {
    private UUID id;
    private String title;
    private String description;
    private String category;
    private CourseDifficulty difficulty;
    private BigDecimal price;
    private String thumbnailUrl;
    private BigDecimal rating;
    private Integer studentCount;
    private UUID instructorId;
    private String instructorName;
}
