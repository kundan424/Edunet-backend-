package com.edtech.platform.course.repository;

import com.edtech.platform.course.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    List<Lesson> findBySectionIdOrderByDisplayOrderAsc(UUID sectionId);
    int countBySectionCourseId(UUID courseId);
}
