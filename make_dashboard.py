import os

base = 'src/main/java/com/edtech/platform/dashboard'
os.makedirs(f'{base}/dto', exist_ok=True)
os.makedirs(f'{base}/controller', exist_ok=True)
os.makedirs(f'{base}/service', exist_ok=True)
os.makedirs(f'{base}/repository', exist_ok=True)

# Generate StudentDashboardResponse
student_dto = '''package com.edtech.platform.dashboard.dto;

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
'''
with open(f'{base}/dto/StudentDashboardResponse.java', 'w') as f: f.write(student_dto)

# Generate ContinueLearningDTO
cl_dto = '''package com.edtech.platform.dashboard.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Builder
public class ContinueLearningDTO {
    private UUID courseId;
    private String courseTitle;
    private double progressPercentage;
    private UUID lastLessonId;
    private String lastLessonTitle;
    private Integer lastPositionSeconds;
    private LocalDateTime updatedAt;
}
'''
with open(f'{base}/dto/ContinueLearningDTO.java', 'w') as f: f.write(cl_dto)

# Generate InstructorDashboardResponse
instructor_dto = '''package com.edtech.platform.dashboard.dto;

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
'''
with open(f'{base}/dto/InstructorDashboardResponse.java', 'w') as f: f.write(instructor_dto)

course_summary = '''package com.edtech.platform.dashboard.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;
import java.math.BigDecimal;

@Data
@Builder
public class CourseSummaryDTO {
    private UUID courseId;
    private String title;
    private String publishStatus;
    private long studentCount;
    private double rating;
    private long reviewCount;
    private BigDecimal price;
}
'''
with open(f'{base}/dto/CourseSummaryDTO.java', 'w') as f: f.write(course_summary)

recent_activity = '''package com.edtech.platform.dashboard.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Builder
public class RecentActivityDTO {
    private String type;
    private UUID referenceId;
    private String description;
    private LocalDateTime createdAt;
}
'''
with open(f'{base}/dto/RecentActivityDTO.java', 'w') as f: f.write(recent_activity)

recent_review = '''package com.edtech.platform.dashboard.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Builder
public class RecentReviewDTO {
    private UUID courseId;
    private String courseTitle;
    private String reviewerName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
'''
with open(f'{base}/dto/RecentReviewDTO.java', 'w') as f: f.write(recent_review)

admin_dto = '''package com.edtech.platform.dashboard.dto;

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
'''
with open(f'{base}/dto/AdminDashboardResponse.java', 'w') as f: f.write(admin_dto)

instructor_course_analytics = '''package com.edtech.platform.dashboard.dto;

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
'''
with open(f'{base}/dto/InstructorCourseAnalyticsResponse.java', 'w') as f: f.write(instructor_course_analytics)

