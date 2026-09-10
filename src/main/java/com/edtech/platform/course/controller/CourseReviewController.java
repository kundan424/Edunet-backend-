package com.edtech.platform.course.controller;

import com.edtech.platform.common.response.ApiResponse;
import com.edtech.platform.common.security.UserDetailsImpl;
import com.edtech.platform.course.dto.CourseReviewRequest;
import com.edtech.platform.course.dto.CourseReviewResponse;
import com.edtech.platform.course.service.CourseReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses/{courseId}/reviews")
@RequiredArgsConstructor
public class CourseReviewController {

    private final CourseReviewService courseReviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<CourseReviewResponse>> createReview(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @Valid @RequestBody CourseReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(courseReviewService.createReview(userDetails.getId(), courseId, request)));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<CourseReviewResponse>> updateReview(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @PathVariable UUID reviewId,
            @Valid @RequestBody CourseReviewRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                courseReviewService.updateReview(userDetails.getId(), courseId, reviewId, request)));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @PathVariable UUID reviewId) {
        courseReviewService.deleteReview(userDetails.getId(), courseId, reviewId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping
    public ResponseEntity<Page<CourseReviewResponse>> getCourseReviews(
            @PathVariable UUID courseId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(courseReviewService.getCourseReviews(courseId, pageable));
    }
}
