package com.edtech.platform.course.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CoursePublishedEvent {
    private final UUID courseId;
    private final UUID instructorId;
    private final String courseTitle;
}
