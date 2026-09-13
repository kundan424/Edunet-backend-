package com.edtech.platform.admin.controller;

import com.edtech.platform.admin.dto.CourseRejectionRequest;
import com.edtech.platform.admin.service.AdminCourseModerationService;
import com.edtech.platform.common.response.ApiResponse;
import com.edtech.platform.course.dto.CourseSummaryResponse;
import com.edtech.platform.common.security.UserDetailsImpl;
import com.edtech.platform.course.dto.CourseCurriculumResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/courses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCourseModerationController {

    private final AdminCourseModerationService adminCourseModerationService;

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<Page<CourseSummaryResponse>>> getPendingCourses(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(adminCourseModerationService.getPendingCourses(pageable)));
    }

    @PostMapping("/{courseId}/approve")
    public ResponseEntity<ApiResponse<Void>> approveCourse(
            @PathVariable UUID courseId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        adminCourseModerationService.approveCourse(courseId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{courseId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectCourse(
            @PathVariable UUID courseId,
            @Valid @RequestBody CourseRejectionRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        adminCourseModerationService.rejectCourse(courseId, userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{courseId}/curriculum")
    public ResponseEntity<ApiResponse<CourseCurriculumResponse>> getCourseCurriculum(
            @PathVariable UUID courseId) {
        return ResponseEntity.ok(ApiResponse.success(adminCourseModerationService.getCourseCurriculumForModeration(courseId)));
    }
}
