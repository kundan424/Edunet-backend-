package com.edtech.platform.assignment.service;

import com.edtech.platform.assignment.dto.instructor.GradeSubmissionRequest;
import com.edtech.platform.assignment.dto.instructor.InstructorSubmissionResponse;
import com.edtech.platform.assignment.entity.Assignment;
import com.edtech.platform.assignment.entity.AssignmentSubmission;
import com.edtech.platform.assignment.enums.SubmissionStatus;
import com.edtech.platform.assignment.repository.AssignmentRepository;
import com.edtech.platform.assignment.repository.AssignmentSubmissionRepository;
import com.edtech.platform.common.exception.ConflictException;
import com.edtech.platform.common.exception.ResourceNotFoundException;
import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.repository.CourseRepository;
import com.edtech.platform.user.entity.User;
import com.edtech.platform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationEventPublisher;

@Service
@RequiredArgsConstructor
public class AssignmentGradingService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    private void validateInstructorOwnership(UUID instructorId, UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if (!course.getInstructorId().equals(instructorId)) {
            throw new AccessDeniedException("You do not own this course");
        }
    }

    private Assignment validateAssignmentInCourse(UUID courseId, UUID assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        if (!assignment.getLesson().getSection().getCourse().getId().equals(courseId)) {
            throw new ConflictException("Assignment does not belong to the specified course");
        }
        return assignment;
    }

    @Transactional(readOnly = true)
    public List<InstructorSubmissionResponse> getSubmissions(UUID instructorId, UUID courseId, UUID assignmentId) {
        validateInstructorOwnership(instructorId, courseId);
        validateAssignmentInCourse(courseId, assignmentId);

        List<AssignmentSubmission> submissions = submissionRepository.findByAssignmentIdWithUser(assignmentId);
        return submissions.stream()
                .map(this::mapToInstructorResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InstructorSubmissionResponse getSubmission(UUID instructorId, UUID courseId, UUID assignmentId, UUID submissionId) {
        validateInstructorOwnership(instructorId, courseId);
        validateAssignmentInCourse(courseId, assignmentId);

        AssignmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        if (!submission.getAssignment().getId().equals(assignmentId)) {
            throw new ConflictException("Submission does not belong to the specified assignment");
        }

        return mapToInstructorResponse(submission);
    }

    @Transactional
    public InstructorSubmissionResponse gradeSubmission(UUID instructorId, UUID courseId, UUID assignmentId, UUID submissionId, GradeSubmissionRequest request) {
        validateInstructorOwnership(instructorId, courseId);
        Assignment assignment = validateAssignmentInCourse(courseId, assignmentId);

        AssignmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        if (!submission.getAssignment().getId().equals(assignmentId)) {
            throw new ConflictException("Submission does not belong to the specified assignment");
        }

        if (request.getScore() > assignment.getMaxScore()) {
            throw new com.edtech.platform.common.exception.EdTechException(
                    com.edtech.platform.common.exception.ErrorCode.VALIDATION_FAILED, 
                    "Score cannot be greater than the maximum score (" + assignment.getMaxScore() + ")");
        }

        submission.setScore(request.getScore());
        submission.setFeedback(request.getFeedback());
        submission.setStatus(SubmissionStatus.GRADED);
        
        User instructor = userRepository.getReferenceById(instructorId);
        submission.setGradedBy(instructor);
        submission.setGradedAt(LocalDateTime.now());

        submission = submissionRepository.save(submission);
        
        applicationEventPublisher.publishEvent(new com.edtech.platform.assignment.event.AssignmentGradedEvent(
                submission.getUser().getId(), submission.getId(), assignment.getTitle()));
                
        return mapToInstructorResponse(submission);
    }

    private InstructorSubmissionResponse mapToInstructorResponse(AssignmentSubmission submission) {
        return InstructorSubmissionResponse.builder()
                .id(submission.getId())
                .assignmentId(submission.getAssignment().getId())
                .studentId(submission.getUser().getId())
                .studentName(submission.getUser().getName())
                .submissionText(submission.getSubmissionText())
                .status(submission.getStatus())
                .score(submission.getScore())
                .feedback(submission.getFeedback())
                .gradedAt(submission.getGradedAt())
                .submittedAt(submission.getSubmittedAt())
                .build();
    }
}
