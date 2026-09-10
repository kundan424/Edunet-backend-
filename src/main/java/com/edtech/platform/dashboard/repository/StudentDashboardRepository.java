package com.edtech.platform.dashboard.repository;

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
        String sql = "SELECT COUNT(*) FROM quiz_attempts WHERE user_id = ? AND passed = true";
        return jdbcTemplate.queryForObject(sql, Long.class, studentId);
    }

    public double getAverageQuizScore(UUID studentId) {
        String sql = "SELECT AVG(percentage) FROM quiz_attempts WHERE user_id = ? AND status = 'COMPLETED'";
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
        String sql = "SELECT AVG(score) FROM assignment_submissions WHERE user_id = ? AND status = 'GRADED'";
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
