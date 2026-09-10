package com.edtech.platform.dashboard.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Builder
public class InstructorDashboardResponse {
    private long totalCoursesOwned;
    private long draftCourses;
    private long pendingApprovalCourses;
    private long publishedCourses;
    private long archivedCourses;
    
    private long totalEnrolledStudents;
    private long totalCourseReviews;
    private double averageRating;
    
    private long totalLessons;
    private long totalQuizzes;
    private long totalAssignments;
    
    private long totalAssignmentSubmissions;
    private long totalGradedAssignments;
    
    private BigDecimal totalRevenue;
    private String currency;
    
    private List<CourseSummaryDTO> recentCourses;
    private List<RecentActivityDTO> recentEnrollments;
    private List<RecentReviewDTO> recentReviews;
}
