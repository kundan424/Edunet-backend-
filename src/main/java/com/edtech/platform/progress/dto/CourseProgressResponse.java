package com.edtech.platform.progress.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CourseProgressResponse {
    private UUID courseId;
    private int totalLessons;
    private int completedLessons;
    private double completionPercentage;
    private UUID lastAccessedLessonId;
    private LocalDateTime lastAccessedAt;
    private List<LessonProgressResponse> lessonProgress;
}
