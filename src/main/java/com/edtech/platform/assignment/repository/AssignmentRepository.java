package com.edtech.platform.assignment.repository;

import com.edtech.platform.assignment.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {
    Optional<Assignment> findByLessonId(UUID lessonId);
    boolean existsByLessonId(UUID lessonId);
}
