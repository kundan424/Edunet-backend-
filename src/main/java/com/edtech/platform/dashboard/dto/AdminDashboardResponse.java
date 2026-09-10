package com.edtech.platform.dashboard.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class AdminDashboardResponse {
    private UserMetrics users;
    private InstructorMetrics instructors;
    private CourseMetrics courses;
    private EnrollmentMetrics enrollments;
    private PaymentMetrics payments;
    private ReviewMetrics reviews;
    private AssignmentMetrics assignments;
    private QuizMetrics quizzes;
    private List<RecentActivityDTO> recentActivity;

    @Data @Builder public static class UserMetrics {
        private long total;
        private long students;
        private long instructors;
        private long admins;
        private long active;
        private long inactive;
        private long suspended;
    }
    
    @Data @Builder public static class InstructorMetrics {
        private long unverified;
        private long pending;
        private long verified;
        private long rejected;
    }

    @Data @Builder public static class CourseMetrics {
        private long draft;
        private long pendingApproval;
        private long published;
        private long archived;
        private long total;
    }

    @Data @Builder public static class EnrollmentMetrics {
        private long total;
        private long active;
        private long completed;
        private long cancelled;
    }

    @Data @Builder public static class PaymentMetrics {
        private long totalSuccessful;
        private BigDecimal totalAmount;
        private String currency;
    }

    @Data @Builder public static class ReviewMetrics {
        private long total;
        private double averageRating;
    }

    @Data @Builder public static class AssignmentMetrics {
        private long totalSubmissions;
        private long gradedSubmissions;
    }

    @Data @Builder public static class QuizMetrics {
        private long totalAttempts;
        private long passedAttempts;
    }
}
