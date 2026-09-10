package com.edtech.platform.dashboard.controller;

import com.edtech.platform.common.response.ApiResponse;
import com.edtech.platform.common.security.UserDetailsImpl;
import com.edtech.platform.dashboard.dto.AdminDashboardResponse;
import com.edtech.platform.dashboard.dto.InstructorCourseAnalyticsResponse;
import com.edtech.platform.dashboard.dto.InstructorDashboardResponse;
import com.edtech.platform.dashboard.dto.StudentDashboardResponse;
import com.edtech.platform.dashboard.service.AdminDashboardService;
import com.edtech.platform.dashboard.service.InstructorDashboardService;
import com.edtech.platform.dashboard.service.StudentDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final StudentDashboardService studentDashboardService;
    private final InstructorDashboardService instructorDashboardService;
    private final AdminDashboardService adminDashboardService;

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<StudentDashboardResponse>> getStudentDashboard(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(ApiResponse.success(studentDashboardService.getStudentDashboard(userDetails.getId())));
    }

    @GetMapping("/instructor")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse<InstructorDashboardResponse>> getInstructorDashboard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.success(instructorDashboardService.getInstructorDashboard(userDetails.getId(), from, to)));
    }

    @GetMapping("/instructor/courses/{courseId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse<InstructorCourseAnalyticsResponse>> getInstructorCourseAnalytics(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId) {
        return ResponseEntity.ok(ApiResponse.success(instructorDashboardService.getCourseAnalytics(userDetails.getId(), courseId)));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getAdminDashboard() {
        return ResponseEntity.ok(ApiResponse.success(adminDashboardService.getAdminDashboard()));
    }
}
