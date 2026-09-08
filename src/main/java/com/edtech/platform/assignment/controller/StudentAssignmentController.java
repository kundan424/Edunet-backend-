package com.edtech.platform.assignment.controller;

import com.edtech.platform.assignment.dto.shared.AssignmentResponse;
import com.edtech.platform.assignment.dto.student.AssignmentSubmissionRequest;
import com.edtech.platform.assignment.dto.student.StudentSubmissionResponse;
import com.edtech.platform.assignment.service.AssignmentSubmissionService;
import com.edtech.platform.common.response.ApiResponse;
import com.edtech.platform.common.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses/{courseId}/lessons/{lessonId}/assignment")
@RequiredArgsConstructor
public class StudentAssignmentController {

    private final AssignmentSubmissionService submissionService;

    @GetMapping
    public ResponseEntity<ApiResponse<AssignmentResponse>> getAssignment(
            @PathVariable UUID courseId,
            @PathVariable UUID lessonId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        AssignmentResponse response = submissionService.getAssignmentForStudent(userDetails.getId(), courseId, lessonId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/submissions")
    public ResponseEntity<ApiResponse<StudentSubmissionResponse>> submitAssignment(
            @PathVariable UUID courseId,
            @PathVariable UUID lessonId,
            @Valid @RequestBody AssignmentSubmissionRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        StudentSubmissionResponse response = submissionService.submitAssignment(userDetails.getId(), courseId, lessonId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/submissions/my")
    public ResponseEntity<ApiResponse<StudentSubmissionResponse>> getMySubmission(
            @PathVariable UUID courseId,
            @PathVariable UUID lessonId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        StudentSubmissionResponse response = submissionService.getMySubmission(userDetails.getId(), courseId, lessonId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
