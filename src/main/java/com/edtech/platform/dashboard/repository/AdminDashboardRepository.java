package com.edtech.platform.dashboard.repository;

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
        String sql = "SELECT COUNT(*) FROM quiz_attempts" + (passed != null ? " WHERE passed = ?" : "");
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
