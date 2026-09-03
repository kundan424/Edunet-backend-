package com.edtech.platform.user.controller;

import com.edtech.platform.auth.dto.LoginRequest;
import com.edtech.platform.auth.dto.RegisterRequest;
import com.edtech.platform.user.dto.InstructorProfileRequest;
import com.edtech.platform.user.entity.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class InstructorVerificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.edtech.platform.user.repository.UserRepository userRepository;

    @Autowired
    private com.edtech.platform.common.security.JwtUtils jwtUtils;

    @Test
    public void testInstructorVerificationFlow() throws Exception {
        // 1. Register Instructor
        RegisterRequest instructorReg = RegisterRequest.builder()
                .name("Test Instructor")
                .email("instructor@test.com")
                .password("password123")
                .role(Role.INSTRUCTOR)
                .build();
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(instructorReg)))
                .andExpect(status().isCreated());

        // Login Instructor
        LoginRequest loginInstructor = LoginRequest.builder()
                .email("instructor@test.com")
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
        
        MvcResult profileResult = mockMvc.perform(post("/api/v1/instructors/profile")
                .header("Authorization", "Bearer " + instructorToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.verificationStatus").value("UNVERIFIED"))
                .andReturn();

        String profileIdStr = objectMapper.readTree(profileResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        // 3. Request Verification
        mockMvc.perform(post("/api/v1/instructors/verification")
                .header("Authorization", "Bearer " + instructorToken))
                .andExpect(status().isOk());

        // Check Profile is PENDING
        mockMvc.perform(get("/api/v1/instructors/profile")
                .header("Authorization", "Bearer " + instructorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.verificationStatus").value("PENDING"));

        // 4. Register Admin via repository and log in
        com.edtech.platform.user.entity.User adminUser = com.edtech.platform.user.entity.User.builder()
                .name("Admin User")
                .email("admin@test.com")
                .passwordHash("$2a$10$abcdefghijklmnopqrstuv") // dummy hash
                .role(Role.ADMIN)
                .status(com.edtech.platform.user.entity.UserStatus.ACTIVE)
                .build();
        userRepository.save(adminUser);
        
        com.edtech.platform.common.security.UserDetailsImpl adminDetails = new com.edtech.platform.common.security.UserDetailsImpl(
            adminUser.getId(),
            adminUser.getEmail(),
            adminUser.getPasswordHash(),
            java.util.Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        org.springframework.security.core.Authentication auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(adminDetails, null, adminDetails.getAuthorities());
        String adminToken = jwtUtils.generateJwtToken(auth);

        // 5. Admin sees PENDING profiles
        mockMvc.perform(get("/api/v1/admin/instructors/pending")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(profileIdStr));

        // 6. Admin Approves Verification
        mockMvc.perform(post("/api/v1/admin/instructors/" + profileIdStr + "/verify")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

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
}
