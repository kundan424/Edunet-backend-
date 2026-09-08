package com.edtech.platform.assignment.repository;

import com.edtech.platform.assignment.entity.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, UUID> {
    
    Optional<AssignmentSubmission> findByAssignmentIdAndUserId(UUID assignmentId, UUID userId);
    
    List<AssignmentSubmission> findByAssignmentIdOrderBySubmittedAtDesc(UUID assignmentId);

    @Query("SELECT s FROM AssignmentSubmission s JOIN FETCH s.user WHERE s.assignment.id = :assignmentId ORDER BY s.submittedAt DESC")
    List<AssignmentSubmission> findByAssignmentIdWithUser(@Param("assignmentId") UUID assignmentId);
}
