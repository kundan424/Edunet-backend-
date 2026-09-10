package com.edtech.platform.dashboard.repository;

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
    
        public List<RecentReviewDTO> getRecentCourseReviews(UUID courseId, int limit) {
        String sql = "SELECT r.course_id, c.title as course_title, u.name as reviewer_name, r.rating, r.comment, r.created_at " +
                     "FROM course_reviews r JOIN courses c ON r.course_id = c.id JOIN users u ON r.user_id = u.id " +
                     "WHERE c.id = ? ORDER BY r.created_at DESC LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> RecentReviewDTO.builder()
                .courseId(UUID.fromString(rs.getString("course_id")))
                .courseTitle(rs.getString("course_title"))
                .reviewerName(rs.getString("reviewer_name"))
                .rating(rs.getInt("rating"))
                .comment(rs.getString("comment"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build(), courseId, limit);
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
        String sql = "SELECT AVG(qa.percentage) FROM quiz_attempts qa JOIN quizzes q ON qa.quiz_id = q.id JOIN lessons l ON q.lesson_id = l.id JOIN sections s ON l.section_id = s.id WHERE s.course_id = ? AND qa.status = 'COMPLETED'";
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
        String sql = "SELECT AVG(sub.score) FROM assignment_submissions sub JOIN assignments a ON sub.assignment_id = a.id JOIN lessons l ON a.lesson_id = l.id JOIN sections s ON l.section_id = s.id WHERE s.course_id = ? AND sub.status = 'GRADED'";
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
