package com.edtech.platform.assignment.controller;

import com.edtech.platform.assignment.dto.instructor.GradeSubmissionRequest;
import com.edtech.platform.assignment.dto.instructor.InstructorSubmissionResponse;
import com.edtech.platform.assignment.service.AssignmentGradingService;
import com.edtech.platform.common.response.ApiResponse;
import com.edtech.platform.common.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/instructors/courses/{courseId}/assignments/{assignmentId}/submissions")
@RequiredArgsConstructor
public class InstructorAssignmentGradingController {

    private final AssignmentGradingService gradingService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<InstructorSubmissionResponse>>> getSubmissions(
            @PathVariable UUID courseId,
            @PathVariable UUID assignmentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        List<InstructorSubmissionResponse> response = gradingService.getSubmissions(userDetails.getId(), courseId, assignmentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{submissionId}")
    public ResponseEntity<ApiResponse<InstructorSubmissionResponse>> getSubmission(
            @PathVariable UUID courseId,
            @PathVariable UUID assignmentId,
            @PathVariable UUID submissionId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        InstructorSubmissionResponse response = gradingService.getSubmission(userDetails.getId(), courseId, assignmentId, submissionId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{submissionId}/grade")
    public ResponseEntity<ApiResponse<InstructorSubmissionResponse>> gradeSubmission(
            @PathVariable UUID courseId,
            @PathVariable UUID assignmentId,
            @PathVariable UUID submissionId,
            @Valid @RequestBody GradeSubmissionRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        InstructorSubmissionResponse response = gradingService.gradeSubmission(userDetails.getId(), courseId, assignmentId, submissionId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
