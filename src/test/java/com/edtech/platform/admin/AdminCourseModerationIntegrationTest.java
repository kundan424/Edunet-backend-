package com.edtech.platform.admin;

import com.edtech.platform.admin.dto.CourseRejectionRequest;
import com.edtech.platform.common.security.UserDetailsImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.edtech.platform.common.security.JwtUtils;
import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.enums.PublishStatus;
import com.edtech.platform.course.repository.CourseRepository;
import com.edtech.platform.user.entity.Role;
import com.edtech.platform.user.entity.UserStatus;
import com.edtech.platform.user.entity.User;
import com.edtech.platform.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminCourseModerationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private Course pendingCourse;
    private User adminUser;
    private User instructor;
    private User student;
    private String adminToken;
    private String instructorToken;
    private String studentToken;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setEmail("admin@test.com");
        adminUser.setName("Admin User");
        adminUser.setPasswordHash("hash");
        adminUser.setRole(Role.ADMIN);
        adminUser.setStatus(UserStatus.ACTIVE);
        userRepository.save(adminUser);
        adminToken = jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(UserDetailsImpl.build(adminUser), null, UserDetailsImpl.build(adminUser).getAuthorities()));

        instructor = new User();
        instructor.setEmail("instructor@test.com");
        instructor.setName("Instructor");
        instructor.setPasswordHash("hash");
        instructor.setRole(Role.INSTRUCTOR);
        instructor.setStatus(UserStatus.ACTIVE);
        userRepository.save(instructor);
        instructorToken = jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(UserDetailsImpl.build(instructor), null, UserDetailsImpl.build(instructor).getAuthorities()));

        student = new User();
        student.setEmail("student@test.com");
        student.setName("Student");
        student.setPasswordHash("hash");
        student.setRole(Role.STUDENT);
        student.setStatus(UserStatus.ACTIVE);
        userRepository.save(student);
        studentToken = jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(UserDetailsImpl.build(student), null, UserDetailsImpl.build(student).getAuthorities()));

        pendingCourse = new Course();
        pendingCourse.setTitle("Pending Course");
        pendingCourse.setInstructorId(instructor.getId());
        pendingCourse.setPrice(BigDecimal.TEN);
        pendingCourse.setPublishStatus(PublishStatus.PENDING_APPROVAL);
        courseRepository.save(pendingCourse);
    }

    @Test
    void testGetPendingCoursesAsAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/admin/courses/pending")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].title").value("Pending Course"));
    }

    @Test
    void testGetPendingCoursesAsStudent_Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/admin/courses/pending")
                .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testApproveCourseAsAdmin() throws Exception {
        mockMvc.perform(post("/api/v1/admin/courses/" + pendingCourse.getId() + "/approve")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        Course approved = courseRepository.findById(pendingCourse.getId()).get();
        org.junit.jupiter.api.Assertions.assertEquals(PublishStatus.PUBLISHED, approved.getPublishStatus());
        org.junit.jupiter.api.Assertions.assertEquals(adminUser.getId(), approved.getReviewedBy());
    }

    @Test
    void testRejectCourseAsAdmin() throws Exception {
        CourseRejectionRequest request = new CourseRejectionRequest();
        request.setReason("Needs more content");

        mockMvc.perform(post("/api/v1/admin/courses/" + pendingCourse.getId() + "/reject")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Course rejected = courseRepository.findById(pendingCourse.getId()).get();
        org.junit.jupiter.api.Assertions.assertEquals(PublishStatus.DRAFT, rejected.getPublishStatus());
        org.junit.jupiter.api.Assertions.assertEquals("Needs more content", rejected.getRejectionReason());
        org.junit.jupiter.api.Assertions.assertEquals(adminUser.getId(), rejected.getReviewedBy());
    }
}
