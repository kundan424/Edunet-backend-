package com.edtech.platform.assignment.service;

import com.edtech.platform.assignment.dto.shared.AssignmentResponse;
import com.edtech.platform.assignment.dto.student.AssignmentSubmissionRequest;
import com.edtech.platform.assignment.dto.student.StudentSubmissionResponse;
import com.edtech.platform.assignment.entity.Assignment;
import com.edtech.platform.assignment.entity.AssignmentSubmission;
import com.edtech.platform.assignment.enums.SubmissionStatus;
import com.edtech.platform.assignment.repository.AssignmentRepository;
import com.edtech.platform.assignment.repository.AssignmentSubmissionRepository;
import com.edtech.platform.common.exception.ConflictException;
import com.edtech.platform.common.exception.ResourceNotFoundException;
import com.edtech.platform.course.entity.Lesson;
import com.edtech.platform.course.repository.LessonRepository;
import com.edtech.platform.enrollment.repository.EnrollmentRepository;
import com.edtech.platform.progress.service.ProgressService;
import com.edtech.platform.user.entity.User;
import com.edtech.platform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignmentSubmissionService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final ProgressService progressService;

    private void validateEnrollment(UUID userId, UUID courseId) {
        if (!enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new AccessDeniedException("User is not enrolled in this course");
        }
    }

    private Assignment validateAssignmentAndLesson(UUID courseId, UUID lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
        if (!lesson.getSection().getCourse().getId().equals(courseId)) {
            throw new ConflictException("Lesson does not belong to specified course");
        }

        return assignmentRepository.findByLessonId(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found for this lesson"));
    }

    @Transactional(readOnly = true)
    public AssignmentResponse getAssignmentForStudent(UUID userId, UUID courseId, UUID lessonId) {
        validateEnrollment(userId, courseId);
        Assignment assignment = validateAssignmentAndLesson(courseId, lessonId);
        
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

    @Transactional(readOnly = true)
    public StudentSubmissionResponse getMySubmission(UUID userId, UUID courseId, UUID lessonId) {
        validateEnrollment(userId, courseId);
        Assignment assignment = validateAssignmentAndLesson(courseId, lessonId);

        AssignmentSubmission submission = submissionRepository.findByAssignmentIdAndUserId(assignment.getId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        return mapToStudentResponse(submission);
    }

    @Transactional
    public StudentSubmissionResponse submitAssignment(UUID userId, UUID courseId, UUID lessonId, AssignmentSubmissionRequest request) {
        validateEnrollment(userId, courseId);
        Assignment assignment = validateAssignmentAndLesson(courseId, lessonId);

        if (assignment.getDueAt() != null && LocalDateTime.now().isAfter(assignment.getDueAt())) {
            throw new ConflictException("Assignment due date has passed. Late submissions are not allowed.");
        }

        AssignmentSubmission submission = submissionRepository.findByAssignmentIdAndUserId(assignment.getId(), userId)
                .orElseGet(() -> {
                    AssignmentSubmission newSub = new AssignmentSubmission();
                    newSub.setAssignment(assignment);
                    User user = userRepository.getReferenceById(userId);
                    newSub.setUser(user);
                    return newSub;
                });

        if (submission.getStatus() == SubmissionStatus.GRADED) {
            throw new ConflictException("Cannot resubmit an assignment that has already been graded.");
        }

        submission.setSubmissionText(request.getSubmissionText());
        submission.setStatus(SubmissionStatus.SUBMITTED);
        submission.setSubmittedAt(LocalDateTime.now());
        
        submission = submissionRepository.save(submission);

        progressService.completeLesson(userId, courseId, lessonId); // Reuse logic that marks lesson complete

        return mapToStudentResponse(submission);
    }

    private StudentSubmissionResponse mapToStudentResponse(AssignmentSubmission submission) {
        return StudentSubmissionResponse.builder()
                .id(submission.getId())
                .assignmentId(submission.getAssignment().getId())
                .submissionText(submission.getSubmissionText())
                .status(submission.getStatus())
                .score(submission.getScore())
                .feedback(submission.getFeedback())
                .gradedAt(submission.getGradedAt())
                .submittedAt(submission.getSubmittedAt())
                .build();
    }
}
