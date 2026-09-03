package com.edtech.platform.user.service;

import com.edtech.platform.common.exception.InstructorProfileNotFoundException;
import com.edtech.platform.common.exception.InvalidVerificationStateException;
import com.edtech.platform.common.exception.ResourceNotFoundException;
import com.edtech.platform.common.exception.ErrorCode;
import com.edtech.platform.user.dto.InstructorProfileRequest;
import com.edtech.platform.user.dto.InstructorProfileResponse;
import com.edtech.platform.user.entity.InstructorProfile;
import com.edtech.platform.user.entity.User;
import com.edtech.platform.user.entity.VerificationStatus;
import com.edtech.platform.user.repository.InstructorProfileRepository;
import com.edtech.platform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstructorProfileService {

    private final InstructorProfileRepository instructorProfileRepository;
    private final UserRepository userRepository;

    @Transactional
    public InstructorProfileResponse createOrUpdateProfile(UUID userId, InstructorProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        InstructorProfile profile = instructorProfileRepository.findByUserId(userId).orElse(null);

        if (profile == null) {
            profile = InstructorProfile.builder()
                    .user(user)
                    .bio(request.getBio())
                    .expertise(request.getExpertise())
                    .verificationStatus(VerificationStatus.UNVERIFIED)
                    .build();
        } else {
            profile.setBio(request.getBio());
            profile.setExpertise(request.getExpertise());
        }

        profile = instructorProfileRepository.save(profile);
        return mapToResponse(profile);
    }

    @Transactional(readOnly = true)
    public InstructorProfileResponse getProfile(UUID userId) {
        InstructorProfile profile = instructorProfileRepository.findByUserId(userId)
                .orElseThrow(InstructorProfileNotFoundException::new);
        return mapToResponse(profile);
    }

    @Transactional
    public void requestVerification(UUID userId) {
        InstructorProfile profile = instructorProfileRepository.findByUserId(userId)
                .orElseThrow(InstructorProfileNotFoundException::new);

        if (profile.getVerificationStatus() == VerificationStatus.PENDING || profile.getVerificationStatus() == VerificationStatus.VERIFIED) {
            throw new InvalidVerificationStateException("Cannot request verification. Current status is " + profile.getVerificationStatus());
        }

        profile.setVerificationStatus(VerificationStatus.PENDING);
        instructorProfileRepository.save(profile);
    }

    @Transactional
    public void approveVerification(UUID instructorId) {
        InstructorProfile profile = instructorProfileRepository.findById(instructorId)
                .orElseThrow(InstructorProfileNotFoundException::new);

        if (profile.getVerificationStatus() != VerificationStatus.PENDING) {
            throw new InvalidVerificationStateException("Cannot approve. Current status is not PENDING.");
        }

        profile.setVerificationStatus(VerificationStatus.VERIFIED);
        instructorProfileRepository.save(profile);
    }

    @Transactional
    public void rejectVerification(UUID instructorId) {
        InstructorProfile profile = instructorProfileRepository.findById(instructorId)
                .orElseThrow(InstructorProfileNotFoundException::new);

        if (profile.getVerificationStatus() != VerificationStatus.PENDING) {
            throw new InvalidVerificationStateException("Cannot reject. Current status is not PENDING.");
        }

        profile.setVerificationStatus(VerificationStatus.REJECTED);
        instructorProfileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public List<InstructorProfileResponse> getPendingProfiles() {
        return instructorProfileRepository.findAllByVerificationStatus(VerificationStatus.PENDING)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private InstructorProfileResponse mapToResponse(InstructorProfile profile) {
        return InstructorProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .name(profile.getUser().getName())
                .bio(profile.getBio())
                .expertise(profile.getExpertise())
                .verificationStatus(profile.getVerificationStatus())
                .build();
    }
}
