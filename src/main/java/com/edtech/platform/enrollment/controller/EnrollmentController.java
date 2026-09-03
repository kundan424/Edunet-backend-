package com.edtech.platform.enrollment.controller;

import com.edtech.platform.common.security.UserDetailsImpl;
import com.edtech.platform.enrollment.dto.CourseLearningResponse;
import com.edtech.platform.enrollment.dto.EnrollmentResponseDTO;
import com.edtech.platform.enrollment.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/courses/{courseId}/enroll")
    public ResponseEntity<EnrollmentResponseDTO> enroll(
            @PathVariable UUID courseId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(enrollmentService.enroll(userDetails.getId(), courseId));
    }

    @GetMapping("/enrollments")
    public ResponseEntity<Page<EnrollmentResponseDTO>> getMyEnrollments(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Pageable pageable) {
        return ResponseEntity.ok(enrollmentService.getMyEnrollments(userDetails.getId(), pageable));
    }

    @GetMapping("/enrollments/{enrollmentId}")
    public ResponseEntity<EnrollmentResponseDTO> getEnrollmentById(
            @PathVariable UUID enrollmentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentById(enrollmentId, userDetails.getId()));
    }

    @GetMapping("/courses/{courseId}/learn")
    public ResponseEntity<CourseLearningResponse> getCourseLearningMaterial(
            @PathVariable UUID courseId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(enrollmentService.getCourseLearningMaterial(courseId, userDetails.getId()));
    }
}
