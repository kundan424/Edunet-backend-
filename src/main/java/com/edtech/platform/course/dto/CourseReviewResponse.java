package com.edtech.platform.course.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CourseReviewResponse {
    private UUID id;
    private UUID courseId;
    private UUID userId;
    private String studentDisplayName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
