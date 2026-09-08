package com.edtech.platform.progress.dto;

import com.edtech.platform.progress.enums.ProgressStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class LessonProgressResponse {
    private UUID lessonId;
    private ProgressStatus status;
    private Integer lastPositionSeconds;
    private Integer maxPositionSeconds;
    private LocalDateTime completedAt;
}
