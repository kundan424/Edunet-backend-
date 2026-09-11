package com.edtech.platform.course.controller;

import com.edtech.platform.common.security.UserDetailsImpl;
import com.edtech.platform.course.dto.CourseCreateRequest;
import com.edtech.platform.course.dto.CourseResponse;
import com.edtech.platform.course.dto.CourseUpdateRequest;
import com.edtech.platform.course.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.edtech.platform.course.dto.CourseCurriculumResponse;

import com.edtech.platform.common.response.ApiResponse;

import java.util.List;
import java.util.UUID;

@RestController
    @RequestMapping("/api/v1/instructors/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody CourseCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(courseService.createCourse(userDetails.getId(), request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getCourses(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(ApiResponse.success(courseService.getCoursesByInstructor(userDetails.getId())));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourse(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId) {
        return ResponseEntity.ok(ApiResponse.success(courseService.getCourseById(userDetails.getId(), courseId)));
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @Valid @RequestBody CourseUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(courseService.updateCourse(userDetails.getId(), courseId, request)));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId) {
        courseService.deleteCourse(userDetails.getId(), courseId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(null));
    }

    @GetMapping("/{courseId}/curriculum")
    public ResponseEntity<ApiResponse<CourseCurriculumResponse>> getCourseCurriculum(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId) {
        return ResponseEntity.ok(ApiResponse.success(courseService.getCourseCurriculum(userDetails.getId(), courseId)));
    }

    @PutMapping("/{courseId}/archive")
    public ResponseEntity<ApiResponse<Void>> archiveCourse(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId) {
        courseService.archiveCourse(userDetails.getId(), courseId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/{courseId}/unarchive")
    public ResponseEntity<ApiResponse<Void>> unarchiveCourse(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId) {
        courseService.unarchiveCourse(userDetails.getId(), courseId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{courseId}/submit")
    public ResponseEntity<ApiResponse<Void>> submitForApproval(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId) {
        courseService.submitForApproval(userDetails.getId(), courseId);
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }
}
