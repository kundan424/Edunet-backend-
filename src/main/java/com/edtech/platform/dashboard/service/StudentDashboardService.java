package com.edtech.platform.dashboard.service;

import com.edtech.platform.dashboard.dto.StudentDashboardResponse;
import com.edtech.platform.dashboard.repository.StudentDashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentDashboardService {

    private final StudentDashboardRepository repository;

    @Transactional(readOnly = true)
    public StudentDashboardResponse getStudentDashboard(UUID studentId) {
        long activeCourses = repository.countEnrollmentsByStatus(studentId, "ACTIVE");
        long completedCourses = repository.countEnrollmentsByStatus(studentId, "COMPLETED");
        long totalEnrolled = repository.countEnrollmentsByStatus(studentId, null);
        
        double courseCompletionPct = activeCourses > 0 ? (completedCourses * 100.0) / activeCourses : 0.0;
        
        long totalLessonsInActive = repository.countTotalLessonsInActiveCourses(studentId);
        long completedLessons = repository.countCompletedLessons(studentId);
        
        double lessonCompletionPct = totalLessonsInActive > 0 ? (completedLessons * 100.0) / totalLessonsInActive : 0.0;
        
        return StudentDashboardResponse.builder()
                .totalEnrolledCourses(totalEnrolled)
                .activeCourses(activeCourses)
                .completedCourses(completedCourses)
                .overallCourseCompletionPercentage(courseCompletionPct)
                .totalLessonsCompleted(completedLessons)
                .totalLessonsInEnrolledCourses(totalLessonsInActive)
                .overallLessonCompletionPercentage(lessonCompletionPct)
                .quizzesAttempted(repository.countQuizzesAttempted(studentId))
                .quizzesPassed(repository.countQuizzesPassed(studentId))
                .averageQuizScore(repository.getAverageQuizScore(studentId))
                .assignmentsSubmitted(repository.countAssignmentsSubmitted(studentId))
                .assignmentsGraded(repository.countAssignmentsGraded(studentId))
                .averageAssignmentScore(repository.getAverageAssignmentScore(studentId))
                .unreadNotificationCount(repository.countUnreadNotifications(studentId))
                .continueLearning(repository.getContinueLearning(studentId, 5))
                .build();
    }
}
