package com.edtech.platform.enrollment.dto;

import com.edtech.platform.course.enums.LessonType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class LessonLearningDTO {
    private UUID id;
    private String title;
    private String description;
    private LessonType lessonType;
    private Integer displayOrder;
}
