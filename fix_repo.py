import os
path = 'src/main/java/com/edtech/platform/dashboard/repository/InstructorDashboardRepository.java'
with open(path, 'r') as f: c = f.read()

new_method = '''    public List<RecentReviewDTO> getRecentCourseReviews(UUID courseId, int limit) {
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
'''
c = c.replace("public long countCourseLessons", new_method + "\n    public long countCourseLessons")
with open(path, 'w') as f: f.write(c)

path2 = 'src/main/java/com/edtech/platform/dashboard/service/InstructorDashboardService.java'
with open(path2, 'r') as f: c2 = f.read()
c2 = c2.replace("repository.getRecentReviews(instructorId, null, null, 5)", "repository.getRecentCourseReviews(courseId, 5)")
with open(path2, 'w') as f: f.write(c2)
