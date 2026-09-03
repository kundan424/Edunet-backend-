package com.edtech.platform.auth.service;

import com.edtech.platform.auth.dto.LoginRequest;
import com.edtech.platform.auth.dto.LoginResponse;
import com.edtech.platform.auth.dto.RegisterRequest;
import com.edtech.platform.common.exception.ConflictException;
import com.edtech.platform.common.exception.ErrorCode;
import com.edtech.platform.common.exception.UnauthorizedException;
import com.edtech.platform.common.exception.ValidationException;
import com.edtech.platform.common.security.JwtUtils;
import com.edtech.platform.user.dto.UserResponse;
import com.edtech.platform.user.entity.Role;
import com.edtech.platform.user.entity.User;
import com.edtech.platform.user.entity.UserStatus;
import com.edtech.platform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        Role role = request.getRole() != null ? request.getRole() : Role.STUDENT;
        if (role == Role.ADMIN) {
            throw new ValidationException("Cannot register as ADMIN");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);

        return UserResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .status(savedUser.getStatus())
                .build();
    }

    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            return LoginResponse.builder()
                    .token(jwt)
                    .build();
        } catch (AuthenticationException e) {
            log.warn("Invalid credentials for email: {}", request.getEmail());
            throw new UnauthorizedException(ErrorCode.INVALID_CREDENTIALS.getDefaultMessage());
        }
    }
}
