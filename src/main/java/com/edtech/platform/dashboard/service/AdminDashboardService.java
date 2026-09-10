package com.edtech.platform.dashboard.service;

import com.edtech.platform.dashboard.dto.AdminDashboardResponse;
import com.edtech.platform.dashboard.repository.AdminDashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final AdminDashboardRepository repository;

    @Transactional(readOnly = true)
    public AdminDashboardResponse getAdminDashboard() {
        return AdminDashboardResponse.builder()
                .users(AdminDashboardResponse.UserMetrics.builder()
                        .total(repository.countUsers(null, null))
                        .students(repository.countUsers("STUDENT", null))
                        .instructors(repository.countUsers("INSTRUCTOR", null))
                        .admins(repository.countUsers("ADMIN", null))
                        .active(repository.countUsers(null, "ACTIVE"))
                        .inactive(repository.countUsers(null, "INACTIVE"))
                        .suspended(repository.countUsers(null, "SUSPENDED"))
                        .build())
                .instructors(AdminDashboardResponse.InstructorMetrics.builder()
                        .unverified(repository.countInstructorProfiles("UNVERIFIED"))
                        .pending(repository.countInstructorProfiles("PENDING"))
                        .verified(repository.countInstructorProfiles("VERIFIED"))
                        .rejected(repository.countInstructorProfiles("REJECTED"))
                        .build())
                .courses(AdminDashboardResponse.CourseMetrics.builder()
                        .draft(repository.countCourses("DRAFT"))
                        .pendingApproval(repository.countCourses("PENDING_APPROVAL"))
                        .published(repository.countCourses("PUBLISHED"))
                        .archived(repository.countCourses("ARCHIVED"))
                        .total(repository.countCourses(null))
                        .build())
                .enrollments(AdminDashboardResponse.EnrollmentMetrics.builder()
                        .total(repository.countEnrollments(null))
                        .active(repository.countEnrollments("ACTIVE"))
                        .completed(repository.countEnrollments("COMPLETED"))
                        .cancelled(repository.countEnrollments("CANCELLED"))
                        .build())
                .payments(AdminDashboardResponse.PaymentMetrics.builder()
                        .totalSuccessful(repository.countPayments("SUCCEEDED"))
                        .totalAmount(repository.getTotalPaymentAmount("SUCCEEDED"))
                        .currency("USD")
                        .build())
                .reviews(AdminDashboardResponse.ReviewMetrics.builder()
                        .total(repository.countReviews())
                        .averageRating(repository.getAveragePlatformRating())
                        .build())
                .assignments(AdminDashboardResponse.AssignmentMetrics.builder()
                        .totalSubmissions(repository.countAssignmentSubmissions(null))
                        .gradedSubmissions(repository.countAssignmentSubmissions("GRADED"))
                        .build())
                .quizzes(AdminDashboardResponse.QuizMetrics.builder()
                        .totalAttempts(repository.countQuizAttempts(null))
                        .passedAttempts(repository.countQuizAttempts(true))
                        .build())
                .recentActivity(repository.getRecentActivity(10))
                .build();
    }
}
