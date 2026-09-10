package com.edtech.platform.dashboard.service;

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
                
                .recentReviews(repository.getRecentCourseReviews(courseId, 5)) // Will fetch instructor's recent reviews, but we should probably filter by course. Let's fix that.
                .build();
    }
}
