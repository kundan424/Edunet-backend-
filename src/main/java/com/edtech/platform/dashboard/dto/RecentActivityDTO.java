package com.edtech.platform.dashboard.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Builder
public class RecentActivityDTO {
    private String type;
    private UUID referenceId;
    private String description;
    private LocalDateTime createdAt;
}
