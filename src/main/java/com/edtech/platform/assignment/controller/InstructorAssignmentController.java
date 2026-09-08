package com.edtech.platform.assignment.controller;

import com.edtech.platform.assignment.dto.instructor.AssignmentCreateRequest;
import com.edtech.platform.assignment.dto.shared.AssignmentResponse;
import com.edtech.platform.assignment.service.AssignmentAuthoringService;
import com.edtech.platform.common.response.ApiResponse;
import com.edtech.platform.common.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/instructors/courses/{courseId}/lessons/{lessonId}/assignment")
@RequiredArgsConstructor
public class InstructorAssignmentController {

    private final AssignmentAuthoringService assignmentAuthoringService;

    @PostMapping
    public ResponseEntity<ApiResponse<AssignmentResponse>> createAssignment(
            @PathVariable UUID courseId,
            @PathVariable UUID lessonId,
            @Valid @RequestBody AssignmentCreateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        AssignmentResponse response = assignmentAuthoringService.createAssignment(userDetails.getId(), courseId, lessonId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<AssignmentResponse>> updateAssignment(
            @PathVariable UUID courseId,
            @PathVariable UUID lessonId,
            @Valid @RequestBody AssignmentCreateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        AssignmentResponse response = assignmentAuthoringService.updateAssignment(userDetails.getId(), courseId, lessonId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<AssignmentResponse>> getAssignment(
            @PathVariable UUID courseId,
            @PathVariable UUID lessonId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        AssignmentResponse response = assignmentAuthoringService.getAssignment(userDetails.getId(), courseId, lessonId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteAssignment(
            @PathVariable UUID courseId,
            @PathVariable UUID lessonId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        assignmentAuthoringService.deleteAssignment(userDetails.getId(), courseId, lessonId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(null));
    }
}
