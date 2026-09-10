package com.edtech.platform.quiz.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class QuizCompletedEvent {
    private final UUID studentId;
    private final UUID attemptId;
    private final String quizTitle;
    private final double score;
}
