package com.edtech.platform.progress.controller;

import com.edtech.platform.common.response.ApiResponse;
import com.edtech.platform.common.security.UserDetailsImpl;
import com.edtech.platform.progress.dto.CourseProgressResponse;
import com.edtech.platform.progress.dto.ProgressUpdateRequest;
import com.edtech.platform.progress.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @PostMapping("/{courseId}/lessons/{lessonId}/progress")
    public ResponseEntity<ApiResponse<Void>> updateProgress(
            @PathVariable UUID courseId,
            @PathVariable UUID lessonId,
            @RequestBody(required = false) ProgressUpdateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        if (request == null) {
            request = new ProgressUpdateRequest();
        }
        
        progressService.updateProgress(userDetails.getId(), courseId, lessonId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{courseId}/progress")
    public ResponseEntity<ApiResponse<CourseProgressResponse>> getCourseProgress(
            @PathVariable UUID courseId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        CourseProgressResponse response = progressService.getCourseProgress(userDetails.getId(), courseId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
