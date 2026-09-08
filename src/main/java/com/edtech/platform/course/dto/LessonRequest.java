package com.edtech.platform.course.dto;

import com.edtech.platform.course.enums.LessonType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LessonRequest {
    @NotBlank
    private String title;
    private String description;
    @NotNull
    private LessonType lessonType;
    @NotNull
    private Integer displayOrder;
    private Integer durationSeconds;
}
