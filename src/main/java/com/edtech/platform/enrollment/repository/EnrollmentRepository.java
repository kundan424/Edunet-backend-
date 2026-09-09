package com.edtech.platform.enrollment.repository;

import com.edtech.platform.enrollment.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    boolean existsByUserIdAndCourseId(UUID userId, UUID courseId);
    boolean existsByUserIdAndCourseIdAndStatus(UUID userId, UUID courseId, com.edtech.platform.enrollment.enums.EnrollmentStatus status);
    Optional<Enrollment> findByUserIdAndCourseId(UUID userId, UUID courseId);

    @Query(value = "SELECT e FROM Enrollment e JOIN FETCH e.course WHERE e.user.id = :userId",
           countQuery = "SELECT count(e) FROM Enrollment e WHERE e.user.id = :userId")
    Page<Enrollment> findByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT e FROM Enrollment e JOIN FETCH e.course WHERE e.id = :id AND e.user.id = :userId")
    Optional<Enrollment> findByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);
}
