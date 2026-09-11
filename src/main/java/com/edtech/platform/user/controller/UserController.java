package com.edtech.platform.user.controller;

import com.edtech.platform.common.response.ApiResponse;
import com.edtech.platform.common.security.UserDetailsImpl;
import com.edtech.platform.user.dto.UserResponse;
import com.edtech.platform.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.edtech.platform.user.dto.EmailPreferenceDTO;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ApiResponse.success(userService.getUserProfile(userDetails.getId()));
    }

    @GetMapping("/me/email-preferences")
    public ApiResponse<EmailPreferenceDTO> getEmailPreferences(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ApiResponse.success(userService.getEmailPreferences(userDetails.getId()));
    }

    @PutMapping("/me/email-preferences")
    public ApiResponse<EmailPreferenceDTO> updateEmailPreferences(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody EmailPreferenceDTO dto) {
        return ApiResponse.success(userService.updateEmailPreferences(userDetails.getId(), dto));
    }
}