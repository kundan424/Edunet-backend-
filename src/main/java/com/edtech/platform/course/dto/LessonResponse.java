package com.edtech.platform.course.dto;

import com.edtech.platform.course.enums.LessonType;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LessonResponse {
    private UUID id;
    private UUID sectionId;
    private String title;
    private String description;
    private LessonType lessonType;
    private Integer displayOrder;
    private Integer durationSeconds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
