package com.edtech.platform.course.dto;

import com.edtech.platform.course.enums.LessonType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicLessonResponse {
    private UUID id;
    private String title;
    private String description;
    private LessonType lessonType;
    private Integer displayOrder;
    private Integer durationSeconds;
}
