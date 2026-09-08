package com.edtech.platform.progress;

import com.edtech.platform.auth.dto.LoginRequest;
import com.edtech.platform.auth.dto.RegisterRequest;
import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.entity.Lesson;
import com.edtech.platform.course.entity.Section;
import com.edtech.platform.course.enums.CourseDifficulty;
import com.edtech.platform.course.enums.LessonType;
import com.edtech.platform.course.enums.PublishStatus;
import com.edtech.platform.course.repository.CourseRepository;
import com.edtech.platform.enrollment.entity.Enrollment;
import com.edtech.platform.enrollment.repository.EnrollmentRepository;
import com.edtech.platform.progress.dto.ProgressUpdateRequest;
import com.edtech.platform.progress.repository.LessonProgressRepository;
import com.edtech.platform.user.entity.Role;
import com.edtech.platform.user.entity.User;
import com.edtech.platform.user.entity.UserStatus;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ProgressIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private LessonProgressRepository lessonProgressRepository;

    private User student;
    private String studentToken;
    private Course course;
    private Lesson videoLesson;
    private Lesson textLesson;

    @BeforeEach
    void setUp() throws Exception {
        // Register Student
        student = User.builder()
                .email("student_prog@test.com")
                .passwordHash("hash") // We will use auth API to get token, so we need a real registered user or bypass
                .name("Student")
                .role(Role.STUDENT)
                .status(UserStatus.ACTIVE)
                .build();
        // Just create real token via register API
    }
    
    private String getStudentToken() throws Exception {
        RegisterRequest reg = RegisterRequest.builder()
                .name("Student Prog")
                .email("student_prog_api" + System.currentTimeMillis() + "@test.com")
                .password("password123")
                .role(Role.STUDENT)
                .build();
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated());

        LoginRequest login = LoginRequest.builder()
                .email(reg.getEmail())
                .password("password123")
                .build();
        MvcResult res = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andReturn();
        
        String email = reg.getEmail();
        student = userRepository.findByEmail(email).get();

        return objectMapper.readTree(res.getResponse().getContentAsString())
                .get("data").get("token").asText();
    }

    private void setupCourseData() {
        User instructor = User.builder()
                .email("inst_prog" + System.currentTimeMillis() + "@test.com")
                .passwordHash("hash")
                .name("Inst")
                .role(Role.INSTRUCTOR)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(instructor);

        course = new Course();
        course.setTitle("Progress Course");
        course.setInstructorId(instructor.getId());
        course.setPublishStatus(PublishStatus.PUBLISHED);
        course.setPrice(BigDecimal.ZERO);
        course.setCategory("Test");
        course.setDifficulty(CourseDifficulty.BEGINNER);

        Section section = new Section();
        section.setTitle("Section 1");
        section.setDisplayOrder(1);
        section.setCourse(course);
        course.getSections().add(section);

        videoLesson = new Lesson();
        videoLesson.setTitle("Video");
        videoLesson.setLessonType(LessonType.VIDEO);
        videoLesson.setDisplayOrder(1);
        videoLesson.setDurationSeconds(100);
        videoLesson.setSection(section);
        section.getLessons().add(videoLesson);

        textLesson = new Lesson();
        textLesson.setTitle("Text");
        textLesson.setLessonType(LessonType.TEXT);
        textLesson.setDisplayOrder(2);
        textLesson.setSection(section);
        section.getLessons().add(textLesson);

        courseRepository.save(course);
    }

    @Test
    void testProgressFlow() throws Exception {
        String token = getStudentToken();
        setupCourseData();

        // Enroll
        Enrollment e = new Enrollment();
        e.setUser(student);
        e.setCourse(course);
        enrollmentRepository.save(e);

        ProgressUpdateRequest updateReq = new ProgressUpdateRequest();
        updateReq.setPositionSeconds(50); // 50 / 100 = 50%

        // 1. Update progress (Video)
        mockMvc.perform(post("/api/v1/courses/" + course.getId() + "/lessons/" + videoLesson.getId() + "/progress")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk());

        // 2. Read course progress
        mockMvc.perform(get("/api/v1/courses/" + course.getId() + "/progress")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.completedLessons", is(0)))
                .andExpect(jsonPath("$.data.completionPercentage", is(0.0)))
                .andExpect(jsonPath("$.data.lessonProgress[0].status", is("IN_PROGRESS")));

        // 3. Complete Video
        updateReq.setPositionSeconds(95);
        mockMvc.perform(post("/api/v1/courses/" + course.getId() + "/lessons/" + videoLesson.getId() + "/progress")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk());

        // 4. Complete Text
        ProgressUpdateRequest textReq = new ProgressUpdateRequest();
        mockMvc.perform(post("/api/v1/courses/" + course.getId() + "/lessons/" + textLesson.getId() + "/progress")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(textReq)))
                .andExpect(status().isOk());

        // 5. Read overall progress
        mockMvc.perform(get("/api/v1/courses/" + course.getId() + "/progress")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalLessons", is(2)))
                .andExpect(jsonPath("$.data.completedLessons", is(2)))
                .andExpect(jsonPath("$.data.completionPercentage", is(100.0)));
                
        // 6. Rewind Video
        updateReq.setPositionSeconds(20);
        mockMvc.perform(post("/api/v1/courses/" + course.getId() + "/lessons/" + videoLesson.getId() + "/progress")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk());
                
        // 7. Verify rewind changed lastPosition but preserved maxPosition
        mockMvc.perform(get("/api/v1/courses/" + course.getId() + "/progress")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                // Assuming video is first in the list
                .andExpect(jsonPath("$.data.lessonProgress[0].lastPositionSeconds", is(20)))
                .andExpect(jsonPath("$.data.lessonProgress[0].maxPositionSeconds", is(95)));
    }

    @Test
    void testProgressFailsIfNotEnrolled() throws Exception {
        String token = getStudentToken();
        setupCourseData();

        ProgressUpdateRequest updateReq = new ProgressUpdateRequest();
        updateReq.setPositionSeconds(50);

        mockMvc.perform(post("/api/v1/courses/" + course.getId() + "/lessons/" + videoLesson.getId() + "/progress")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isForbidden());
    }
}
