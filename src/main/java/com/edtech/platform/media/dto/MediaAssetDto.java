package com.edtech.platform.media.dto;

import com.edtech.platform.media.domain.ProcessingStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class MediaAssetDto {
    private UUID id;
    private UUID lessonId;
    private String originalFileName;
    private String contentType;
    private Long fileSize;
    private Integer durationSeconds;
    private ProcessingStatus processingStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
