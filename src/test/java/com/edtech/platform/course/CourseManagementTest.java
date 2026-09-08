package com.edtech.platform.course;

import com.edtech.platform.course.dto.CourseCreateRequest;
import com.edtech.platform.course.dto.LessonRequest;
import com.edtech.platform.course.dto.SectionRequest;
import com.edtech.platform.course.enums.CourseDifficulty;
import com.edtech.platform.course.enums.LessonType;
import com.edtech.platform.course.repository.CourseRepository;
import com.edtech.platform.user.entity.InstructorProfile;
import com.edtech.platform.user.entity.User;
import com.edtech.platform.user.entity.Role;
import com.edtech.platform.user.entity.UserStatus;
import com.edtech.platform.user.entity.VerificationStatus;
import com.edtech.platform.user.repository.InstructorProfileRepository;
import com.edtech.platform.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CourseManagementTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InstructorProfileRepository instructorProfileRepository;

    @Autowired
    private CourseRepository courseRepository;

    private User instructor;
    private User otherInstructor;
    private com.edtech.platform.common.security.UserDetailsImpl userDetails;
    private com.edtech.platform.common.security.UserDetailsImpl otherUserDetails;

    @BeforeEach
    void setUp() {
        instructor = new User();
        instructor.setEmail("instructor@test.com");
        instructor.setPasswordHash("hash");
        instructor.setRole(Role.INSTRUCTOR);
        instructor.setName("John Doe");
        instructor.setStatus(UserStatus.ACTIVE);
        instructor = userRepository.save(instructor);

        InstructorProfile profile = new InstructorProfile();
        profile.setUser(instructor);
        profile.setBio("Expert");
        profile.setExpertise("Java");
        profile.setVerificationStatus(VerificationStatus.VERIFIED);
        instructorProfileRepository.save(profile);

        userDetails = com.edtech.platform.common.security.UserDetailsImpl.build(instructor);

        otherInstructor = new User();
        otherInstructor.setEmail("other@test.com");
        otherInstructor.setPasswordHash("hash");
        otherInstructor.setRole(Role.INSTRUCTOR);
        otherInstructor.setName("Jane Doe");
        otherInstructor.setStatus(UserStatus.ACTIVE);
        otherInstructor = userRepository.save(otherInstructor);

        otherUserDetails = com.edtech.platform.common.security.UserDetailsImpl.build(otherInstructor);
    }

    @Test
    void testCourseCreationAndPublicationFlow() throws Exception {
        // 1. Create Course
        CourseCreateRequest courseReq = new CourseCreateRequest();
        courseReq.setTitle("Spring Boot Mastery");
        courseReq.setDescription("Learn Spring Boot");
        courseReq.setDifficulty(CourseDifficulty.INTERMEDIATE);
        courseReq.setPrice(new BigDecimal("99.99"));

        MvcResult courseResult = mockMvc.perform(post("/api/v1/instructors/courses")
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").exists())
                .andReturn();

        String courseResponseStr = courseResult.getResponse().getContentAsString();
        String courseId = objectMapper.readTree(courseResponseStr).get("data").get("id").asText();

        // 2. Create Section
        SectionRequest sectionReq = new SectionRequest();
        sectionReq.setTitle("Getting Started");
        sectionReq.setDisplayOrder(1);

        MvcResult sectionResult = mockMvc.perform(post("/api/v1/instructors/courses/" + courseId + "/sections")
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sectionReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").exists())
                .andReturn();

        String sectionResponseStr = sectionResult.getResponse().getContentAsString();
        String sectionId = objectMapper.readTree(sectionResponseStr).get("data").get("id").asText();

        // 3. Create Lesson
        LessonRequest lessonReq = new LessonRequest();
        lessonReq.setTitle("Introduction");
        lessonReq.setLessonType(LessonType.VIDEO);
        lessonReq.setDisplayOrder(1);

        mockMvc.perform(post("/api/v1/instructors/courses/" + courseId + "/sections/" + sectionId + "/lessons")
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lessonReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").exists());

        // 4. Submit for Approval
        mockMvc.perform(post("/api/v1/instructors/courses/" + courseId + "/submit")
                        .with(user(userDetails)))
                .andExpect(status().isOk());

        // Verify status
        mockMvc.perform(get("/api/v1/instructors/courses/" + courseId)
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.publishStatus").value("PENDING_APPROVAL"));
    }

    @Test
    void testUnauthorizedModification() throws Exception {
        CourseCreateRequest courseReq = new CourseCreateRequest();
        courseReq.setTitle("Spring Boot Mastery");
        courseReq.setDescription("Learn Spring Boot");
        courseReq.setDifficulty(CourseDifficulty.INTERMEDIATE);
        courseReq.setPrice(new BigDecimal("99.99"));

        MvcResult courseResult = mockMvc.perform(post("/api/v1/instructors/courses")
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String courseResponseStr = courseResult.getResponse().getContentAsString();
        String courseId = objectMapper.readTree(courseResponseStr).get("data").get("id").asText();

        // Another instructor tries to access it
        mockMvc.perform(get("/api/v1/instructors/courses/" + courseId)
                        .with(user(otherUserDetails)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCannotPublishWithoutLessons() throws Exception {
        CourseCreateRequest courseReq = new CourseCreateRequest();
        courseReq.setTitle("Empty Course");
        courseReq.setDifficulty(CourseDifficulty.BEGINNER);
        courseReq.setPrice(new BigDecimal("0.00"));

        MvcResult courseResult = mockMvc.perform(post("/api/v1/instructors/courses")
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String courseResponseStr = courseResult.getResponse().getContentAsString();
        String courseId = objectMapper.readTree(courseResponseStr).get("data").get("id").asText();

        // Submit for Approval without sections/lessons
        mockMvc.perform(post("/api/v1/instructors/courses/" + courseId + "/submit")
                        .with(user(userDetails)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUnverifiedInstructorCannotPublish() throws Exception {
        // Change status to PENDING
        InstructorProfile profile = instructorProfileRepository.findByUserId(instructor.getId()).get();
        profile.setVerificationStatus(VerificationStatus.PENDING);
        instructorProfileRepository.save(profile);

        CourseCreateRequest courseReq = new CourseCreateRequest();
        courseReq.setTitle("Draft");
        courseReq.setDifficulty(CourseDifficulty.BEGINNER);
        courseReq.setPrice(new BigDecimal("0.00"));

        MvcResult courseResult = mockMvc.perform(post("/api/v1/instructors/courses")
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String courseId = objectMapper.readTree(courseResult.getResponse().getContentAsString()).get("data").get("id").asText();

        mockMvc.perform(post("/api/v1/instructors/courses/" + courseId + "/submit")
                        .with(user(userDetails)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testMissingInstructorProfileCannotPublish() throws Exception {
        // Delete the profile completely to simulate skipping onboarding
        instructorProfileRepository.deleteAll();

        CourseCreateRequest courseReq = new CourseCreateRequest();
        courseReq.setTitle("Draft No Profile");
        courseReq.setDifficulty(CourseDifficulty.BEGINNER);
        courseReq.setPrice(new BigDecimal("0.00"));

        MvcResult courseResult = mockMvc.perform(post("/api/v1/instructors/courses")
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String courseId = objectMapper.readTree(courseResult.getResponse().getContentAsString()).get("data").get("id").asText();

        mockMvc.perform(post("/api/v1/instructors/courses/" + courseId + "/submit")
                        .with(user(userDetails)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.message").value("Instructor onboarding/profile creation is required before submitting a course"));
    }
}
