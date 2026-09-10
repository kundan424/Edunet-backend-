package com.edtech.platform.dashboard.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Builder
public class ContinueLearningDTO {
    private UUID courseId;
    private String courseTitle;
    private double progressPercentage;
    private UUID lastLessonId;
    private String lastLessonTitle;
    private Integer lastPositionSeconds;
    private LocalDateTime updatedAt;
}
