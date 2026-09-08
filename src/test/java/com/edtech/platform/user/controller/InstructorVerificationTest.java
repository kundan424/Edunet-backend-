package com.edtech.platform.user.controller;

import com.edtech.platform.auth.dto.LoginRequest;
import com.edtech.platform.auth.dto.RegisterRequest;
import com.edtech.platform.common.security.UserDetailsImpl;
import com.edtech.platform.user.dto.InstructorProfileRequest;
import com.edtech.platform.user.entity.InstructorProfile;
import com.edtech.platform.user.entity.Role;
import com.edtech.platform.user.entity.User;
import com.edtech.platform.user.entity.UserStatus;
import com.edtech.platform.user.entity.VerificationStatus;
import com.edtech.platform.user.repository.InstructorProfileRepository;
import com.edtech.platform.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class InstructorVerificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InstructorProfileRepository instructorProfileRepository;

    @Autowired
    private com.edtech.platform.common.security.JwtUtils jwtUtils;

    /**
     * Full end-to-end verification flow using real HTTP registration/login.
     * The admin verify endpoint uses the instructor's USER ID (not profile ID).
     */
    @Test
    public void testInstructorVerificationFlow() throws Exception {
        // 1. Register Instructor
        RegisterRequest instructorReg = RegisterRequest.builder()
                .name("Test Instructor")
                .email("instructor_flow_test@test.com")
                .password("password123")
                .role(Role.INSTRUCTOR)
                .build();
        MvcResult regResult = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(instructorReg)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract the user ID from registration response
        String instructorUserId = objectMapper.readTree(regResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        // Login Instructor
        LoginRequest loginInstructor = LoginRequest.builder()
                .email("instructor_flow_test@test.com")
                .password("password123")
                .build();
        MvcResult instructorLoginResult = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginInstructor)))
                .andExpect(status().isOk())
                .andReturn();
        String instructorToken = objectMapper.readTree(instructorLoginResult.getResponse().getContentAsString())
                .get("data").get("token").asText();

        // 2. Create Profile
        InstructorProfileRequest profileReq = InstructorProfileRequest.builder()
                .bio("I am a Java expert")
                .expertise("Java, Spring Boot")
                .build();

        mockMvc.perform(post("/api/v1/instructors/profile")
                .header("Authorization", "Bearer " + instructorToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.verificationStatus").value("UNVERIFIED"));

        // 3. Request Verification
        mockMvc.perform(post("/api/v1/instructors/verification")
                .header("Authorization", "Bearer " + instructorToken))
                .andExpect(status().isOk());

        // Check Profile is PENDING
        mockMvc.perform(get("/api/v1/instructors/profile")
                .header("Authorization", "Bearer " + instructorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.verificationStatus").value("PENDING"));

        // 4. Register Admin via repository and generate JWT
        User adminUser = User.builder()
                .name("Admin User")
                .email("admin_flow_test@test.com")
                .passwordHash("$2a$10$abcdefghijklmnopqrstuv") // dummy hash
                .role(Role.ADMIN)
                .status(UserStatus.ACTIVE)
                .build();
        adminUser = userRepository.saveAndFlush(adminUser);

        UserDetailsImpl adminDetails = UserDetailsImpl.build(adminUser);
        org.springframework.security.core.Authentication auth =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        adminDetails, null, adminDetails.getAuthorities());
        String adminToken = jwtUtils.generateJwtToken(auth);

        // 5. Admin sees PENDING profiles
        mockMvc.perform(get("/api/v1/admin/instructors/pending")
                .with(user(adminDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].userId").value(instructorUserId));

        // 6. Admin Approves Verification using the USER ID
        mockMvc.perform(post("/api/v1/admin/instructors/" + instructorUserId + "/verify")
                .with(user(adminDetails)))
                .andExpect(status().isOk());

        System.out.println("USER EXISTS? " + userRepository.findByEmail("instructor_flow_test@test.com").isPresent());

        // 7. Check Profile is VERIFIED
        mockMvc.perform(get("/api/v1/instructors/profile")
                .header("Authorization", "Bearer " + instructorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.verificationStatus").value("VERIFIED"));
    }

    @Test
    public void testSecurityNegativeCases() throws Exception {
        // Register Student
        RegisterRequest studentReg = RegisterRequest.builder()
                .name("Test Student")
                .email("student_neg@test.com")
                .password("password123")
                .role(Role.STUDENT)
                .build();
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentReg)))
                .andExpect(status().isCreated());

        // Login Student
        LoginRequest loginStudent = LoginRequest.builder()
                .email("student_neg@test.com")
                .password("password123")
                .build();
        MvcResult studentLoginResult = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginStudent)))
                .andExpect(status().isOk())
                .andReturn();
        String studentToken = objectMapper.readTree(studentLoginResult.getResponse().getContentAsString())
                .get("data").get("token").asText();

        // Student receives 403 on instructor endpoints
        InstructorProfileRequest profileReq = InstructorProfileRequest.builder()
                .bio("I am a Java expert")
                .expertise("Java, Spring Boot")
                .build();

        mockMvc.perform(post("/api/v1/instructors/profile")
                .header("Authorization", "Bearer " + studentToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileReq)))
                .andExpect(status().isForbidden());

        // Register & Login Instructor for admin negative test
        RegisterRequest instructorReg = RegisterRequest.builder()
                .name("Test Instructor")
                .email("instructor_neg@test.com")
                .password("password123")
                .role(Role.INSTRUCTOR)
                .build();
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(instructorReg)))
                .andExpect(status().isCreated());

        LoginRequest loginInstructor = LoginRequest.builder()
                .email("instructor_neg@test.com")
                .password("password123")
                .build();
        MvcResult instructorLoginResult = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginInstructor)))
                .andExpect(status().isOk())
                .andReturn();
        String instructorToken = objectMapper.readTree(instructorLoginResult.getResponse().getContentAsString())
                .get("data").get("token").asText();

        // Instructor receives 403 on admin endpoints
        mockMvc.perform(get("/api/v1/admin/instructors/pending")
                .header("Authorization", "Bearer " + instructorToken))
                .andExpect(status().isForbidden());
    }

    /**
     * REGRESSION TEST — Smoke test bug #1.
     * Proves that approveVerification looks up by user_id, not profile PK.
     */
    @Test
    void testApproveVerificationUsesUserId() throws Exception {
        User instructor = User.builder()
                .name("Regression Inst")
                .email("regression_approve@test.com")
                .passwordHash("hash")
                .role(Role.INSTRUCTOR)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(instructor);

        InstructorProfile profile = InstructorProfile.builder()
                .user(instructor)
                .bio("Bio")
                .expertise("Exp")
                .verificationStatus(VerificationStatus.PENDING)
                .build();
        instructorProfileRepository.save(profile);

        User admin = User.builder()
                .name("Regression Admin")
                .email("regression_admin@test.com")
                .passwordHash("hash")
                .role(Role.ADMIN)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(admin);

        UserDetailsImpl adminDetails = UserDetailsImpl.build(admin);

        // Verify using instructor's USER ID (not profile ID)
        mockMvc.perform(post("/api/v1/admin/instructors/" + instructor.getId() + "/verify")
                .with(user(adminDetails)))
                .andExpect(status().isOk());

        InstructorProfile updated = instructorProfileRepository.findByUserId(instructor.getId()).get();
        assertEquals(VerificationStatus.VERIFIED, updated.getVerificationStatus());
    }

    /**
     * REGRESSION TEST — Proves that reject also uses user_id consistently.
     */
    @Test
    void testRejectVerificationUsesUserId() throws Exception {
        User instructor = User.builder()
                .name("Reject Inst")
                .email("regression_reject@test.com")
                .passwordHash("hash")
                .role(Role.INSTRUCTOR)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(instructor);

        InstructorProfile profile = InstructorProfile.builder()
                .user(instructor)
                .bio("Bio")
                .expertise("Exp")
                .verificationStatus(VerificationStatus.PENDING)
                .build();
        instructorProfileRepository.save(profile);

        User admin = User.builder()
                .name("Reject Admin")
                .email("regression_reject_admin@test.com")
                .passwordHash("hash")
                .role(Role.ADMIN)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(admin);

        UserDetailsImpl adminDetails = UserDetailsImpl.build(admin);

        // Reject using instructor's USER ID
        mockMvc.perform(post("/api/v1/admin/instructors/" + instructor.getId() + "/reject")
                .with(user(adminDetails)))
                .andExpect(status().isOk());

        InstructorProfile updated = instructorProfileRepository.findByUserId(instructor.getId()).get();
        assertEquals(VerificationStatus.REJECTED, updated.getVerificationStatus());
    }

    /**
     * REGRESSION TEST — Invalid (non-existent) user ID returns 404.
     */
    @Test
    void testVerifyInvalidUserIdReturns404() throws Exception {
        User admin = User.builder()
                .name("Admin 404")
                .email("admin_404@test.com")
                .passwordHash("hash")
                .role(Role.ADMIN)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(admin);

        UserDetailsImpl adminDetails = UserDetailsImpl.build(admin);

        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/admin/instructors/" + nonExistentId + "/verify")
                .with(user(adminDetails)))
                .andExpect(status().isNotFound());
    }
}
