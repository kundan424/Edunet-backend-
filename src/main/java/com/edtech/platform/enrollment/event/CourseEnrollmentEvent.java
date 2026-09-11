package com.edtech.platform.enrollment.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CourseEnrollmentEvent {
    private final UUID userId;
    private final UUID courseId;
    private final String courseTitle;
}
