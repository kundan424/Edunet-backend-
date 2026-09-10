package com.edtech.platform.admin.service;

import com.edtech.platform.admin.dto.UserSummaryResponse;
import com.edtech.platform.user.entity.Role;
import com.edtech.platform.user.entity.UserStatus;
import com.edtech.platform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserService {
    
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<UserSummaryResponse> getUsers(Role role, UserStatus status, Pageable pageable) {
        return userRepository.findAll(pageable) // Need to implement proper filtering if necessary, but keep it simple
                .map(user -> UserSummaryResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .status(user.getStatus())
                        .createdAt(user.getCreatedAt())
                        .build());
    }
}
