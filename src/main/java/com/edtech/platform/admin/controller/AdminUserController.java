package com.edtech.platform.admin.controller;

import com.edtech.platform.admin.dto.UserSummaryResponse;
import com.edtech.platform.admin.service.AdminUserService;
import com.edtech.platform.common.response.ApiResponse;
import com.edtech.platform.user.entity.Role;
import com.edtech.platform.user.entity.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserSummaryResponse>>> getUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) UserStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(adminUserService.getUsers(role, status, pageable)));
    }
}
