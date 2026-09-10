package com.edtech.platform.dashboard.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;
import java.math.BigDecimal;

@Data
@Builder
public class CourseSummaryDTO {
    private UUID courseId;
    private String title;
    private String publishStatus;
    private long studentCount;
    private double rating;
    private long reviewCount;
    private BigDecimal price;
}
