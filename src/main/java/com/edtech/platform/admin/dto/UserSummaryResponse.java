package com.edtech.platform.admin.dto;

import com.edtech.platform.user.entity.Role;
import com.edtech.platform.user.entity.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserSummaryResponse {
    private UUID id;
    private String name;
    private String email;
    private Role role;
    private UserStatus status;
    private LocalDateTime createdAt;
}
