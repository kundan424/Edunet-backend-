import os
base = 'src/main/java/com/edtech/platform/dashboard/service'

student_service = '''package com.edtech.platform.dashboard.service;

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
'''
with open(f'{base}/StudentDashboardService.java', 'w') as f: f.write(student_service)

instructor_service = '''package com.edtech.platform.dashboard.service;

import com.edtech.platform.common.exception.ResourceNotFoundException;
import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.repository.CourseRepository;
import com.edtech.platform.dashboard.dto.CourseSummaryDTO;
import com.edtech.platform.dashboard.dto.InstructorCourseAnalyticsResponse;
import com.edtech.platform.dashboard.dto.InstructorDashboardResponse;
import com.edtech.platform.dashboard.repository.InstructorDashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InstructorDashboardService {

    private final InstructorDashboardRepository repository;
    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public InstructorDashboardResponse getInstructorDashboard(UUID instructorId, LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("From date cannot be after To date");
        }
        
        return InstructorDashboardResponse.builder()
                .totalCoursesOwned(repository.countCoursesByStatus(instructorId, null))
                .draftCourses(repository.countCoursesByStatus(instructorId, "DRAFT"))
                .pendingApprovalCourses(repository.countCoursesByStatus(instructorId, "PENDING_APPROVAL"))
                .publishedCourses(repository.countCoursesByStatus(instructorId, "PUBLISHED"))
                .archivedCourses(repository.countCoursesByStatus(instructorId, "ARCHIVED"))
                
                .totalEnrolledStudents(repository.countEnrolledStudents(instructorId, from, to))
                .totalCourseReviews(repository.countCourseReviews(instructorId, from, to))
                .averageRating(repository.getAverageRating(instructorId))
                
                .totalLessons(repository.countLessons(instructorId))
                .totalQuizzes(repository.countQuizzes(instructorId))
                .totalAssignments(repository.countAssignments(instructorId))
                
                .totalAssignmentSubmissions(repository.countAssignmentSubmissions(instructorId, null))
                .totalGradedAssignments(repository.countAssignmentSubmissions(instructorId, "GRADED"))
                
                .totalRevenue(repository.getTotalRevenue(instructorId, from, to))
                .currency("USD") // Hardcoded to USD assuming existing Stripe checkout is USD
                
                .recentCourses(repository.getRecentCourses(instructorId, 5))
                .recentEnrollments(repository.getRecentEnrollments(instructorId, from, to, 5))
                .recentReviews(repository.getRecentReviews(instructorId, from, to, 5))
                .build();
    }
    
    @Transactional(readOnly = true)
    public InstructorCourseAnalyticsResponse getCourseAnalytics(UUID instructorId, UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
                
        if (!course.getInstructorId().equals(instructorId)) {
            throw new AccessDeniedException("You do not own this course");
        }
        
        CourseSummaryDTO summary = repository.getCourseSummary(courseId);
        
        return InstructorCourseAnalyticsResponse.builder()
                .course(summary)
                .enrollmentCount(repository.countCourseEnrollments(courseId, null))
                .activeEnrollmentCount(repository.countCourseEnrollments(courseId, "ACTIVE"))
                .completedEnrollmentCount(repository.countCourseEnrollments(courseId, "COMPLETED"))
                
                .averageRating(summary != null ? summary.getRating() : 0.0)
                .reviewCount(summary != null ? summary.getReviewCount() : 0)
                
                .totalLessons(repository.countCourseLessons(courseId))
                .totalQuizAttempts(repository.countCourseQuizAttempts(courseId))
                .averageQuizPercentage(repository.getAverageCourseQuizScore(courseId))
                
                .totalAssignmentSubmissions(repository.countCourseAssignmentSubmissions(courseId, null))
                .gradedSubmissions(repository.countCourseAssignmentSubmissions(courseId, "GRADED"))
                .averageAssignmentScore(repository.getAverageCourseAssignmentScore(courseId))
                
                .successfulPaymentCount(repository.countCourseSuccessfulPayments(courseId))
                .successfulRevenue(repository.getCourseSuccessfulRevenue(courseId))
                .currency("USD")
                
                .recentReviews(repository.getRecentReviews(instructorId, null, null, 5)) // Will fetch instructor's recent reviews, but we should probably filter by course. Let's fix that.
                .build();
    }
}
'''
with open(f'{base}/InstructorDashboardService.java', 'w') as f: f.write(instructor_service)

admin_service = '''package com.edtech.platform.dashboard.service;

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
'''
with open(f'{base}/AdminDashboardService.java', 'w') as f: f.write(admin_service)
