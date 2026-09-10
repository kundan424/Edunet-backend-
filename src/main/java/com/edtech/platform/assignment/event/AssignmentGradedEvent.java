package com.edtech.platform.assignment.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class AssignmentGradedEvent {
    private final UUID studentId;
    private final UUID submissionId;
    private final String assignmentTitle;
}
