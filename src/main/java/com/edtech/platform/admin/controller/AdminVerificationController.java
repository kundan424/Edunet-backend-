package com.edtech.platform.admin.controller;

import com.edtech.platform.common.response.ApiResponse;
import com.edtech.platform.user.dto.InstructorProfileResponse;
import com.edtech.platform.user.service.InstructorProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/instructors")
@RequiredArgsConstructor
public class AdminVerificationController {

    private final InstructorProfileService instructorProfileService;

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<InstructorProfileResponse>>> getPendingProfiles() {
        List<InstructorProfileResponse> profiles = instructorProfileService.getPendingProfiles();
        return ResponseEntity.ok(ApiResponse.success(profiles));
    }

    @PostMapping("/{instructorId}/verify")
    public ResponseEntity<ApiResponse<Void>> verifyInstructor(@PathVariable UUID instructorId) {
        instructorProfileService.approveVerification(instructorId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{instructorId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectInstructor(@PathVariable UUID instructorId) {
        instructorProfileService.rejectVerification(instructorId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
