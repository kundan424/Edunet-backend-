package com.edtech.platform.assignment.service;

import com.edtech.platform.assignment.dto.instructor.AssignmentCreateRequest;
import com.edtech.platform.assignment.dto.shared.AssignmentResponse;
import com.edtech.platform.assignment.entity.Assignment;
import com.edtech.platform.assignment.repository.AssignmentRepository;
import com.edtech.platform.common.exception.ConflictException;
import com.edtech.platform.common.exception.ResourceNotFoundException;
import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.entity.Lesson;
import com.edtech.platform.course.enums.LessonType;
import com.edtech.platform.course.repository.CourseRepository;
import com.edtech.platform.course.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignmentAuthoringService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;

    private void validateInstructorOwnership(UUID instructorId, UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if (!course.getInstructorId().equals(instructorId)) {
            throw new AccessDeniedException("You do not own this course");
        }
    }

    private Lesson validateLesson(UUID courseId, UUID lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
        if (!lesson.getSection().getCourse().getId().equals(courseId)) {
            throw new ConflictException("Lesson does not belong to specified course");
        }
        if (lesson.getLessonType() != LessonType.ASSIGNMENT) {
            throw new ConflictException("Assignment can only be added to lessons of type ASSIGNMENT");
        }
        return lesson;
    }

    @Transactional
    public AssignmentResponse createAssignment(UUID instructorId, UUID courseId, UUID lessonId, AssignmentCreateRequest request) {
        validateInstructorOwnership(instructorId, courseId);
        Lesson lesson = validateLesson(courseId, lessonId);

        if (assignmentRepository.existsByLessonId(lessonId)) {
            throw new ConflictException("Assignment already exists for this lesson");
        }

        Assignment assignment = new Assignment();
        assignment.setLesson(lesson);
        assignment.setTitle(request.getTitle());
        assignment.setInstructions(request.getInstructions());
        assignment.setMaxScore(request.getMaxScore());
        assignment.setDueAt(request.getDueAt());

        assignment = assignmentRepository.save(assignment);
        return mapToResponse(assignment);
    }

    @Transactional
    public AssignmentResponse updateAssignment(UUID instructorId, UUID courseId, UUID lessonId, AssignmentCreateRequest request) {
        validateInstructorOwnership(instructorId, courseId);
        validateLesson(courseId, lessonId);

        Assignment assignment = assignmentRepository.findByLessonId(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        assignment.setTitle(request.getTitle());
        assignment.setInstructions(request.getInstructions());
        assignment.setMaxScore(request.getMaxScore());
        assignment.setDueAt(request.getDueAt());

        assignment = assignmentRepository.save(assignment);
        return mapToResponse(assignment);
    }

    @Transactional(readOnly = true)
    public AssignmentResponse getAssignment(UUID instructorId, UUID courseId, UUID lessonId) {
        validateInstructorOwnership(instructorId, courseId);
        validateLesson(courseId, lessonId);

        Assignment assignment = assignmentRepository.findByLessonId(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        return mapToResponse(assignment);
    }

    @Transactional
    public void deleteAssignment(UUID instructorId, UUID courseId, UUID lessonId) {
        validateInstructorOwnership(instructorId, courseId);
        validateLesson(courseId, lessonId);

        Assignment assignment = assignmentRepository.findByLessonId(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        assignmentRepository.delete(assignment);
    }

    private AssignmentResponse mapToResponse(Assignment assignment) {
        return AssignmentResponse.builder()
                .id(assignment.getId())
                .lessonId(assignment.getLesson().getId())
                .title(assignment.getTitle())
                .instructions(assignment.getInstructions())
                .maxScore(assignment.getMaxScore())
                .dueAt(assignment.getDueAt())
                .createdAt(assignment.getCreatedAt())
                .build();
    }
}
