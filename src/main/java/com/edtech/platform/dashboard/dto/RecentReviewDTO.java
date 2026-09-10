package com.edtech.platform.dashboard.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Builder
public class RecentReviewDTO {
    private UUID courseId;
    private String courseTitle;
    private String reviewerName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
