package com.edtech.platform.user.controller;

import com.edtech.platform.user.entity.User;
import com.edtech.platform.user.repository.UserRepository;
import com.edtech.platform.user.dto.EmailPreferenceDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@org.springframework.transaction.annotation.Transactional
public class EmailPreferencesIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setup() {
        
        testUser = new User();
        testUser.setName("Test");
        testUser.setEmail("test_ecb7e742@email.com");
        testUser.setPasswordHash("hash");
        testUser.setEmailNotificationsEnabled(true);
        testUser.setRole(com.edtech.platform.user.entity.Role.STUDENT);
        testUser = userRepository.save(testUser);
    }

    @Test
    void canGetPreferences() throws Exception {
        mockMvc.perform(get("/api/v1/users/me/email-preferences")
                .with(user(new com.edtech.platform.common.security.UserDetailsImpl(testUser.getId(), testUser.getEmail(), testUser.getPasswordHash(), java.util.Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + testUser.getRole().name()))))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.emailNotificationsEnabled").value(true));
    }

    @Test
    void canUpdatePreferences() throws Exception {
        EmailPreferenceDTO dto = new EmailPreferenceDTO(false);
        mockMvc.perform(put("/api/v1/users/me/email-preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .with(user(new com.edtech.platform.common.security.UserDetailsImpl(testUser.getId(), testUser.getEmail(), testUser.getPasswordHash(), java.util.Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + testUser.getRole().name()))))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.emailNotificationsEnabled").value(false));
    }

    @Test
    void unauthorizedUpdateIsRejected() throws Exception {
        EmailPreferenceDTO dto = new EmailPreferenceDTO(false);
        mockMvc.perform(put("/api/v1/users/me/email-preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized()); // Or 403 based on security config
    }
}
