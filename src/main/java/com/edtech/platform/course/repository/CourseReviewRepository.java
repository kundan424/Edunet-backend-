package com.edtech.platform.course.repository;

import com.edtech.platform.course.entity.CourseReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, UUID> {
    
    @Query("SELECT cr FROM CourseReview cr JOIN FETCH cr.user WHERE cr.course.id = :courseId")
    Page<CourseReview> findByCourseIdWithUser(@Param("courseId") UUID courseId, Pageable pageable);
    
    Optional<CourseReview> findByUserIdAndCourseId(UUID userId, UUID courseId);
    
    boolean existsByUserIdAndCourseId(UUID userId, UUID courseId);
}
