package com.edtech.platform.user.dto;

import com.edtech.platform.user.entity.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructorProfileResponse {
    private UUID id;
    private UUID userId;
    private String name;
    private String bio;
    private String expertise;
    private VerificationStatus verificationStatus;
}
