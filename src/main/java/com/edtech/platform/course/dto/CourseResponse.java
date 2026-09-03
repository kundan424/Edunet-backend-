package com.edtech.platform.course.dto;

import com.edtech.platform.course.enums.CourseDifficulty;
import com.edtech.platform.course.enums.PublishStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CourseResponse {
    private UUID id;
    private UUID instructorId;
    private String title;
    private String description;
    private String category;
    private CourseDifficulty difficulty;
    private BigDecimal price;
    private String thumbnailUrl;
    private PublishStatus publishStatus;
    private BigDecimal rating;
    private Integer studentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
