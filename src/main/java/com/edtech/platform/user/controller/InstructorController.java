package com.edtech.platform.user.controller;

import com.edtech.platform.common.response.ApiResponse;
import com.edtech.platform.common.security.UserDetailsImpl;
import com.edtech.platform.user.dto.InstructorProfileRequest;
import com.edtech.platform.user.dto.InstructorProfileResponse;
import com.edtech.platform.user.service.InstructorProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/instructors")
@RequiredArgsConstructor
public class InstructorController {

    private final InstructorProfileService instructorProfileService;

    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<InstructorProfileResponse>> createOrUpdateProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody InstructorProfileRequest request) {
        InstructorProfileResponse response = instructorProfileService.createOrUpdateProfile(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<InstructorProfileResponse>> getProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        InstructorProfileResponse response = instructorProfileService.getProfile(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/verification")
    public ResponseEntity<ApiResponse<Void>> requestVerification(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        instructorProfileService.requestVerification(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
