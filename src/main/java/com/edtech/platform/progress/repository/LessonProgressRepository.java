package com.edtech.platform.progress.repository;

import com.edtech.platform.progress.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, UUID> {
    Optional<LessonProgress> findByUserIdAndLessonId(UUID userId, UUID lessonId);
    List<LessonProgress> findByUserIdAndCourseId(UUID userId, UUID courseId);
    int countByUserIdAndCourseIdAndStatus(UUID userId, UUID courseId, com.edtech.platform.progress.enums.ProgressStatus status);
}
