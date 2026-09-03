package com.edtech.platform.auth.controller;

import com.edtech.platform.auth.dto.LoginRequest;
import com.edtech.platform.auth.dto.LoginResponse;
import com.edtech.platform.auth.dto.RegisterRequest;
import com.edtech.platform.auth.service.AuthService;
import com.edtech.platform.common.response.ApiResponse;
import com.edtech.platform.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request), "User registered successfully");
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request), "Login successful");
    }
}
