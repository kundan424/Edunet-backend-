package com.edtech.platform.course.repository;

import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.enums.PublishStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID>, JpaSpecificationExecutor<Course> {
    List<Course> findByInstructorId(UUID instructorId);

    @EntityGraph(attributePaths = {"sections"})
    Optional<Course> findByIdAndPublishStatus(UUID id, PublishStatus status);

    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true, flushAutomatically = true)
    @org.springframework.data.jpa.repository.Query("UPDATE Course c SET c.studentCount = c.studentCount + 1 WHERE c.id = :courseId")
    void incrementStudentCount(@org.springframework.data.repository.query.Param("courseId") UUID courseId);
}
