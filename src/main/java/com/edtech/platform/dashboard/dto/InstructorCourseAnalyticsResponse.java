package com.edtech.platform.dashboard.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class InstructorCourseAnalyticsResponse {
    private CourseSummaryDTO course;
    private long enrollmentCount;
    private long activeEnrollmentCount;
    private long completedEnrollmentCount;
    
    private double averageRating;
    private long reviewCount;
    
    private long totalLessons;
    private long totalQuizAttempts;
    private double averageQuizPercentage;
    
    private long totalAssignmentSubmissions;
    private long gradedSubmissions;
    private double averageAssignmentScore;
    
    private long successfulPaymentCount;
    private BigDecimal successfulRevenue;
    private String currency;
    
    private List<RecentReviewDTO> recentReviews;
}
