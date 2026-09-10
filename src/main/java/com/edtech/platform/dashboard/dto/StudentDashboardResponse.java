package com.edtech.platform.dashboard.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StudentDashboardResponse {
    private long totalEnrolledCourses;
    private long activeCourses;
    private long completedCourses;
    private double overallCourseCompletionPercentage;
    
    private long totalLessonsCompleted;
    private long totalLessonsInEnrolledCourses;
    private double overallLessonCompletionPercentage;
    
    private long quizzesAttempted;
    private long quizzesPassed;
    private double averageQuizScore;
    
    private long assignmentsSubmitted;
    private long assignmentsGraded;
    private double averageAssignmentScore;
    
    private long unreadNotificationCount;
    
    private List<ContinueLearningDTO> continueLearning;
}
