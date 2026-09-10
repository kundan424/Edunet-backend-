import os
base = 'src/main/java/com/edtech/platform/dashboard/repository'

student_repo = '''package com.edtech.platform.dashboard.repository;

import com.edtech.platform.dashboard.dto.ContinueLearningDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
@RequiredArgsConstructor
public class StudentDashboardRepository {

    private final JdbcTemplate jdbcTemplate;

    public long countEnrollmentsByStatus(UUID studentId, String status) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE user_id = ? " + (status != null ? "AND status = ?" : "");
        if (status != null) {
            return jdbcTemplate.queryForObject(sql, Long.class, studentId, status);
        }
        return jdbcTemplate.queryForObject(sql, Long.class, studentId);
    }

    public long countCompletedLessons(UUID studentId) {
        String sql = "SELECT COUNT(*) FROM lesson_progress WHERE user_id = ? AND status = 'COMPLETED'";
        return jdbcTemplate.queryForObject(sql, Long.class, studentId);
    }

    public long countTotalLessonsInActiveCourses(UUID studentId) {
        String sql = "SELECT COUNT(l.id) FROM lessons l " +
                     "JOIN sections s ON l.section_id = s.id " +
                     "JOIN enrollments e ON s.course_id = e.course_id " +
                     "WHERE e.user_id = ? AND e.status = 'ACTIVE'";
        return jdbcTemplate.queryForObject(sql, Long.class, studentId);
    }

    public long countQuizzesAttempted(UUID studentId) {
        String sql = "SELECT COUNT(*) FROM quiz_attempts WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, studentId);
    }

    public long countQuizzesPassed(UUID studentId) {
        String sql = "SELECT COUNT(*) FROM quiz_attempts WHERE user_id = ? AND is_passed = true";
        return jdbcTemplate.queryForObject(sql, Long.class, studentId);
    }

    public double getAverageQuizScore(UUID studentId) {
        String sql = "SELECT AVG(score) FROM quiz_attempts WHERE user_id = ? AND is_completed = true";
        Double avg = jdbcTemplate.queryForObject(sql, Double.class, studentId);
        return avg != null ? avg : 0.0;
    }

    public long countAssignmentsSubmitted(UUID studentId) {
        String sql = "SELECT COUNT(*) FROM assignment_submissions WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, studentId);
    }

    public long countAssignmentsGraded(UUID studentId) {
        String sql = "SELECT COUNT(*) FROM assignment_submissions WHERE user_id = ? AND status = 'GRADED'";
        return jdbcTemplate.queryForObject(sql, Long.class, studentId);
    }

    public double getAverageAssignmentScore(UUID studentId) {
        String sql = "SELECT AVG(grade) FROM assignment_submissions WHERE user_id = ? AND status = 'GRADED'";
        Double avg = jdbcTemplate.queryForObject(sql, Double.class, studentId);
        return avg != null ? avg : 0.0;
    }

    public long countUnreadNotifications(UUID studentId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = false";
        return jdbcTemplate.queryForObject(sql, Long.class, studentId);
    }

    public List<ContinueLearningDTO> getContinueLearning(UUID studentId, int limit) {
        String sql = "SELECT c.id as course_id, c.title as course_title, l.id as lesson_id, l.title as lesson_title, lp.last_position_seconds, lp.updated_at, " +
                     "(SELECT COUNT(lp2.id) FROM lesson_progress lp2 WHERE lp2.course_id = c.id AND lp2.user_id = ? AND lp2.status = 'COMPLETED') as completed_lessons, " +
                     "(SELECT COUNT(l2.id) FROM lessons l2 JOIN sections s2 ON l2.section_id = s2.id WHERE s2.course_id = c.id) as total_lessons " +
                     "FROM lesson_progress lp " +
                     "JOIN courses c ON lp.course_id = c.id " +
                     "JOIN lessons l ON lp.lesson_id = l.id " +
                     "JOIN enrollments e ON c.id = e.course_id " +
                     "WHERE lp.user_id = ? AND e.user_id = ? AND e.status = 'ACTIVE' " +
                     "AND lp.id IN (SELECT id FROM (SELECT id, ROW_NUMBER() OVER(PARTITION BY course_id ORDER BY updated_at DESC) as rn FROM lesson_progress WHERE user_id = ?) tmp WHERE rn = 1) " +
                     "ORDER BY lp.updated_at DESC LIMIT ?";
        return jdbcTemplate.query(sql, this::mapContinueLearning, studentId, studentId, studentId, studentId, limit);
    }

    private ContinueLearningDTO mapContinueLearning(ResultSet rs, int rowNum) throws SQLException {
        long completed = rs.getLong("completed_lessons");
        long total = rs.getLong("total_lessons");
        double pct = total > 0 ? (completed * 100.0) / total : 0.0;
        
        return ContinueLearningDTO.builder()
                .courseId(UUID.fromString(rs.getString("course_id")))
                .courseTitle(rs.getString("course_title"))
                .progressPercentage(pct)
                .lastLessonId(UUID.fromString(rs.getString("lesson_id")))
                .lastLessonTitle(rs.getString("lesson_title"))
                .lastPositionSeconds(rs.getObject("last_position_seconds") != null ? rs.getInt("last_position_seconds") : 0)
                .updatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null)
                .build();
    }
}
'''
with open(f'{base}/StudentDashboardRepository.java', 'w') as f: f.write(student_repo)

instructor_repo = '''package com.edtech.platform.dashboard.repository;

import com.edtech.platform.dashboard.dto.CourseSummaryDTO;
import com.edtech.platform.dashboard.dto.RecentActivityDTO;
import com.edtech.platform.dashboard.dto.RecentReviewDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.LocalDate;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
@RequiredArgsConstructor
public class InstructorDashboardRepository {

    private final JdbcTemplate jdbcTemplate;

    public long countCoursesByStatus(UUID instructorId, String status) {
        String sql = "SELECT COUNT(*) FROM courses WHERE instructor_id = ? " + (status != null ? "AND publish_status = ?" : "");
        if (status != null) {
            return jdbcTemplate.queryForObject(sql, Long.class, instructorId, status);
        }
        return jdbcTemplate.queryForObject(sql, Long.class, instructorId);
    }
    
    public long countEnrolledStudents(UUID instructorId, LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(DISTINCT e.user_id) FROM enrollments e JOIN courses c ON e.course_id = c.id WHERE c.instructor_id = ?");
        List<Object> args = new ArrayList<>();
        args.add(instructorId);
        if (from != null) { sql.append(" AND e.enrolled_at >= ?"); args.add(from.atStartOfDay()); }
        if (to != null) { sql.append(" AND e.enrolled_at <= ?"); args.add(to.plusDays(1).atStartOfDay()); }
        return jdbcTemplate.queryForObject(sql.toString(), Long.class, args.toArray());
    }

    public long countCourseReviews(UUID instructorId, LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM course_reviews r JOIN courses c ON r.course_id = c.id WHERE c.instructor_id = ?");
        List<Object> args = new ArrayList<>();
        args.add(instructorId);
        if (from != null) { sql.append(" AND r.created_at >= ?"); args.add(from.atStartOfDay()); }
        if (to != null) { sql.append(" AND r.created_at <= ?"); args.add(to.plusDays(1).atStartOfDay()); }
        return jdbcTemplate.queryForObject(sql.toString(), Long.class, args.toArray());
    }

    public double getAverageRating(UUID instructorId) {
        String sql = "SELECT AVG(r.rating) FROM course_reviews r JOIN courses c ON r.course_id = c.id WHERE c.instructor_id = ?";
        Double avg = jdbcTemplate.queryForObject(sql, Double.class, instructorId);
        return avg != null ? avg : 0.0;
    }

    public long countLessons(UUID instructorId) {
        String sql = "SELECT COUNT(l.id) FROM lessons l JOIN sections s ON l.section_id = s.id JOIN courses c ON s.course_id = c.id WHERE c.instructor_id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, instructorId);
    }

    public long countQuizzes(UUID instructorId) {
        String sql = "SELECT COUNT(q.id) FROM quizzes q JOIN lessons l ON q.lesson_id = l.id JOIN sections s ON l.section_id = s.id JOIN courses c ON s.course_id = c.id WHERE c.instructor_id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, instructorId);
    }

    public long countAssignments(UUID instructorId) {
        String sql = "SELECT COUNT(a.id) FROM assignments a JOIN lessons l ON a.lesson_id = l.id JOIN sections s ON l.section_id = s.id JOIN courses c ON s.course_id = c.id WHERE c.instructor_id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, instructorId);
    }

    public long countAssignmentSubmissions(UUID instructorId, String status) {
        String sql = "SELECT COUNT(sub.id) FROM assignment_submissions sub JOIN assignments a ON sub.assignment_id = a.id JOIN lessons l ON a.lesson_id = l.id JOIN sections s ON l.section_id = s.id JOIN courses c ON s.course_id = c.id WHERE c.instructor_id = ?" + (status != null ? " AND sub.status = ?" : "");
        if (status != null) {
            return jdbcTemplate.queryForObject(sql, Long.class, instructorId, status);
        }
        return jdbcTemplate.queryForObject(sql, Long.class, instructorId);
    }

    public BigDecimal getTotalRevenue(UUID instructorId, LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder("SELECT SUM(p.amount) FROM payments p JOIN courses c ON p.course_id = c.id WHERE c.instructor_id = ? AND p.status = 'SUCCEEDED'");
        List<Object> args = new ArrayList<>();
        args.add(instructorId);
        if (from != null) { sql.append(" AND p.paid_at >= ?"); args.add(from.atStartOfDay()); }
        if (to != null) { sql.append(" AND p.paid_at <= ?"); args.add(to.plusDays(1).atStartOfDay()); }
        BigDecimal total = jdbcTemplate.queryForObject(sql.toString(), BigDecimal.class, args.toArray());
        return total != null ? total : BigDecimal.ZERO;
    }

    public List<CourseSummaryDTO> getRecentCourses(UUID instructorId, int limit) {
        String sql = "SELECT id, title, publish_status, student_count, rating, review_count, price FROM courses WHERE instructor_id = ? ORDER BY created_at DESC LIMIT ?";
        return jdbcTemplate.query(sql, this::mapCourseSummary, instructorId, limit);
    }

    public List<RecentActivityDTO> getRecentEnrollments(UUID instructorId, LocalDate from, LocalDate to, int limit) {
        StringBuilder sql = new StringBuilder("SELECT e.id, 'ENROLLMENT' as type, e.enrolled_at, u.name as user_name, c.title as course_title " +
                     "FROM enrollments e JOIN courses c ON e.course_id = c.id JOIN users u ON e.user_id = u.id " +
                     "WHERE c.instructor_id = ?");
        List<Object> args = new ArrayList<>();
        args.add(instructorId);
        if (from != null) { sql.append(" AND e.enrolled_at >= ?"); args.add(from.atStartOfDay()); }
        if (to != null) { sql.append(" AND e.enrolled_at <= ?"); args.add(to.plusDays(1).atStartOfDay()); }
        sql.append(" ORDER BY e.enrolled_at DESC LIMIT ?");
        args.add(limit);
        
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> RecentActivityDTO.builder()
                .type(rs.getString("type"))
                .referenceId(UUID.fromString(rs.getString("id")))
                .description(rs.getString("user_name") + " enrolled in " + rs.getString("course_title"))
                .createdAt(rs.getTimestamp("enrolled_at").toLocalDateTime())
                .build(), args.toArray());
    }

    public List<RecentReviewDTO> getRecentReviews(UUID instructorId, LocalDate from, LocalDate to, int limit) {
        StringBuilder sql = new StringBuilder("SELECT r.course_id, c.title as course_title, u.name as reviewer_name, r.rating, r.comment, r.created_at " +
                     "FROM course_reviews r JOIN courses c ON r.course_id = c.id JOIN users u ON r.user_id = u.id " +
                     "WHERE c.instructor_id = ?");
        List<Object> args = new ArrayList<>();
        args.add(instructorId);
        if (from != null) { sql.append(" AND r.created_at >= ?"); args.add(from.atStartOfDay()); }
        if (to != null) { sql.append(" AND r.created_at <= ?"); args.add(to.plusDays(1).atStartOfDay()); }
        sql.append(" ORDER BY r.created_at DESC LIMIT ?");
        args.add(limit);

        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> RecentReviewDTO.builder()
                .courseId(UUID.fromString(rs.getString("course_id")))
                .courseTitle(rs.getString("course_title"))
                .reviewerName(rs.getString("reviewer_name"))
                .rating(rs.getInt("rating"))
                .comment(rs.getString("comment"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build(), args.toArray());
    }

    private CourseSummaryDTO mapCourseSummary(ResultSet rs, int rowNum) throws SQLException {
        return CourseSummaryDTO.builder()
                .courseId(UUID.fromString(rs.getString("id")))
                .title(rs.getString("title"))
                .publishStatus(rs.getString("publish_status"))
                .studentCount(rs.getLong("student_count"))
                .rating(rs.getDouble("rating"))
                .reviewCount(rs.getLong("review_count"))
                .price(rs.getBigDecimal("price"))
                .build();
    }
    
    // Course-specific analytics queries
    public CourseSummaryDTO getCourseSummary(UUID courseId) {
        String sql = "SELECT id, title, publish_status, student_count, rating, review_count, price FROM courses WHERE id = ?";
        List<CourseSummaryDTO> results = jdbcTemplate.query(sql, this::mapCourseSummary, courseId);
        return results.isEmpty() ? null : results.get(0);
    }
    
    public long countCourseEnrollments(UUID courseId, String status) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE course_id = ?" + (status != null ? " AND status = ?" : "");
        if (status != null) {
            return jdbcTemplate.queryForObject(sql, Long.class, courseId, status);
        }
        return jdbcTemplate.queryForObject(sql, Long.class, courseId);
    }
    
    public long countCourseLessons(UUID courseId) {
        String sql = "SELECT COUNT(l.id) FROM lessons l JOIN sections s ON l.section_id = s.id WHERE s.course_id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, courseId);
    }
    
    public long countCourseQuizAttempts(UUID courseId) {
        String sql = "SELECT COUNT(qa.id) FROM quiz_attempts qa JOIN quizzes q ON qa.quiz_id = q.id JOIN lessons l ON q.lesson_id = l.id JOIN sections s ON l.section_id = s.id WHERE s.course_id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, courseId);
    }
    
    public double getAverageCourseQuizScore(UUID courseId) {
        String sql = "SELECT AVG(qa.score) FROM quiz_attempts qa JOIN quizzes q ON qa.quiz_id = q.id JOIN lessons l ON q.lesson_id = l.id JOIN sections s ON l.section_id = s.id WHERE s.course_id = ? AND qa.is_completed = true";
        Double avg = jdbcTemplate.queryForObject(sql, Double.class, courseId);
        return avg != null ? avg : 0.0;
    }
    
    public long countCourseAssignmentSubmissions(UUID courseId, String status) {
        String sql = "SELECT COUNT(sub.id) FROM assignment_submissions sub JOIN assignments a ON sub.assignment_id = a.id JOIN lessons l ON a.lesson_id = l.id JOIN sections s ON l.section_id = s.id WHERE s.course_id = ?" + (status != null ? " AND sub.status = ?" : "");
        if (status != null) {
            return jdbcTemplate.queryForObject(sql, Long.class, courseId, status);
        }
        return jdbcTemplate.queryForObject(sql, Long.class, courseId);
    }
    
    public double getAverageCourseAssignmentScore(UUID courseId) {
        String sql = "SELECT AVG(sub.grade) FROM assignment_submissions sub JOIN assignments a ON sub.assignment_id = a.id JOIN lessons l ON a.lesson_id = l.id JOIN sections s ON l.section_id = s.id WHERE s.course_id = ? AND sub.status = 'GRADED'";
        Double avg = jdbcTemplate.queryForObject(sql, Double.class, courseId);
        return avg != null ? avg : 0.0;
    }
    
    public long countCourseSuccessfulPayments(UUID courseId) {
        String sql = "SELECT COUNT(*) FROM payments WHERE course_id = ? AND status = 'SUCCEEDED'";
        return jdbcTemplate.queryForObject(sql, Long.class, courseId);
    }
    
    public BigDecimal getCourseSuccessfulRevenue(UUID courseId) {
        String sql = "SELECT SUM(amount) FROM payments WHERE course_id = ? AND status = 'SUCCEEDED'";
        BigDecimal total = jdbcTemplate.queryForObject(sql, BigDecimal.class, courseId);
        return total != null ? total : BigDecimal.ZERO;
    }
}
'''
with open(f'{base}/InstructorDashboardRepository.java', 'w') as f: f.write(instructor_repo)

admin_repo = '''package com.edtech.platform.dashboard.repository;

import com.edtech.platform.dashboard.dto.RecentActivityDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
@RequiredArgsConstructor
public class AdminDashboardRepository {

    private final JdbcTemplate jdbcTemplate;

    public long countUsers(String role, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM users WHERE 1=1");
        if (role != null) sql.append(" AND role = '").append(role).append("'");
        if (status != null) sql.append(" AND status = '").append(status).append("'");
        return jdbcTemplate.queryForObject(sql.toString(), Long.class);
    }

    public long countInstructorProfiles(String status) {
        String sql = "SELECT COUNT(*) FROM instructor_profiles" + (status != null ? " WHERE verification_status = ?" : "");
        if (status != null) {
            return jdbcTemplate.queryForObject(sql, Long.class, status);
        }
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public long countCourses(String status) {
        String sql = "SELECT COUNT(*) FROM courses" + (status != null ? " WHERE publish_status = ?" : "");
        if (status != null) {
            return jdbcTemplate.queryForObject(sql, Long.class, status);
        }
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public long countEnrollments(String status) {
        String sql = "SELECT COUNT(*) FROM enrollments" + (status != null ? " WHERE status = ?" : "");
        if (status != null) {
            return jdbcTemplate.queryForObject(sql, Long.class, status);
        }
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public long countPayments(String status) {
        String sql = "SELECT COUNT(*) FROM payments" + (status != null ? " WHERE status = ?" : "");
        if (status != null) {
            return jdbcTemplate.queryForObject(sql, Long.class, status);
        }
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public BigDecimal getTotalPaymentAmount(String status) {
        String sql = "SELECT SUM(amount) FROM payments" + (status != null ? " WHERE status = ?" : "");
        BigDecimal total;
        if (status != null) {
            total = jdbcTemplate.queryForObject(sql, BigDecimal.class, status);
        } else {
            total = jdbcTemplate.queryForObject(sql, BigDecimal.class);
        }
        return total != null ? total : BigDecimal.ZERO;
    }

    public long countReviews() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM course_reviews", Long.class);
    }

    public double getAveragePlatformRating() {
        Double avg = jdbcTemplate.queryForObject("SELECT AVG(rating) FROM course_reviews", Double.class);
        return avg != null ? avg : 0.0;
    }

    public long countAssignmentSubmissions(String status) {
        String sql = "SELECT COUNT(*) FROM assignment_submissions" + (status != null ? " WHERE status = ?" : "");
        if (status != null) {
            return jdbcTemplate.queryForObject(sql, Long.class, status);
        }
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public long countQuizAttempts(Boolean passed) {
        String sql = "SELECT COUNT(*) FROM quiz_attempts" + (passed != null ? " WHERE is_passed = ?" : "");
        if (passed != null) {
            return jdbcTemplate.queryForObject(sql, Long.class, passed);
        }
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public List<RecentActivityDTO> getRecentActivity(int limit) {
        // Union of recent users, recent enrollments, and recent payments
        String sql = "(SELECT id as ref_id, 'USER_REGISTRATION' as type, 'User registered: ' || name as description, created_at FROM users ORDER BY created_at DESC LIMIT ?) " +
                     "UNION ALL " +
                     "(SELECT e.id as ref_id, 'ENROLLMENT' as type, 'User enrolled in course', e.enrolled_at as created_at FROM enrollments e ORDER BY enrolled_at DESC LIMIT ?) " +
                     "UNION ALL " +
                     "(SELECT p.id as ref_id, 'PAYMENT' as type, 'Payment ' || p.status, p.created_at FROM payments p ORDER BY created_at DESC LIMIT ?) " +
                     "ORDER BY created_at DESC LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> RecentActivityDTO.builder()
                .type(rs.getString("type"))
                .referenceId(UUID.fromString(rs.getString("ref_id")))
                .description(rs.getString("description"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build(), limit, limit, limit, limit);
    }
}
'''
with open(f'{base}/AdminDashboardRepository.java', 'w') as f: f.write(admin_repo)
