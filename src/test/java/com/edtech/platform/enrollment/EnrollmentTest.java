package com.edtech.platform.enrollment;

import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.enums.CourseDifficulty;
import com.edtech.platform.course.enums.PublishStatus;
import com.edtech.platform.course.repository.CourseRepository;
import com.edtech.platform.enrollment.entity.Enrollment;
import com.edtech.platform.enrollment.repository.EnrollmentRepository;
import com.edtech.platform.user.entity.Role;
import com.edtech.platform.user.entity.User;
import com.edtech.platform.user.entity.UserStatus;
import com.edtech.platform.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class EnrollmentTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private User student;
    private User instructor;
    private com.edtech.platform.common.security.UserDetailsImpl studentDetails;

    @BeforeEach
    void setUp() {
        enrollmentRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();

        student = new User();
        student.setEmail("student@test.com");
        student.setPasswordHash("hash");
        student.setName("Student Name");
        student.setRole(Role.STUDENT);
        student.setStatus(UserStatus.ACTIVE);
        student = userRepository.save(student);

        instructor = new User();
        instructor.setEmail("instructor@test.com");
        instructor.setPasswordHash("hash");
        instructor.setName("Instructor Name");
        instructor.setRole(Role.INSTRUCTOR);
        instructor.setStatus(UserStatus.ACTIVE);
        instructor = userRepository.save(instructor);

        studentDetails = com.edtech.platform.common.security.UserDetailsImpl.build(student);
    }

    @Test
    void enroll_ShouldReturn201_WhenValid() throws Exception {
        Course course = createCourse(BigDecimal.ZERO, PublishStatus.PUBLISHED);

        mockMvc.perform(post("/api/v1/courses/" + course.getId() + "/enroll")
                .with(user(studentDetails)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.courseId").value(course.getId().toString()))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        Course updatedCourse = courseRepository.findById(course.getId()).get();
        assert updatedCourse.getStudentCount() == 1;
    }

    @Test
    void enroll_ShouldReturn409_WhenAlreadyEnrolled() throws Exception {
        Course course = createCourse(BigDecimal.ZERO, PublishStatus.PUBLISHED);

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        enrollment.setUser(student);
        enrollmentRepository.save(enrollment);

        mockMvc.perform(post("/api/v1/courses/" + course.getId() + "/enroll")
                .with(user(studentDetails)))
                .andExpect(status().isConflict());
    }

    @Test
    void enroll_ShouldReturn401_WhenUnauthenticated() throws Exception {
        Course course = createCourse(BigDecimal.ZERO, PublishStatus.PUBLISHED);

        mockMvc.perform(post("/api/v1/courses/" + course.getId() + "/enroll"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void enroll_ShouldReturn400_WhenPaidCourse() throws Exception {
        Course course = createCourse(new BigDecimal("10.00"), PublishStatus.PUBLISHED);

        mockMvc.perform(post("/api/v1/courses/" + course.getId() + "/enroll")
                .with(user(studentDetails)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void enroll_ShouldReturn403_WhenNotPublished() throws Exception {
        Course course = createCourse(BigDecimal.ZERO, PublishStatus.DRAFT);

        mockMvc.perform(post("/api/v1/courses/" + course.getId() + "/enroll")
                .with(user(studentDetails)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getCourseLearningMaterial_ShouldReturn200_WhenEnrolled() throws Exception {
        Course course = createCourse(BigDecimal.ZERO, PublishStatus.PUBLISHED);

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        enrollment.setUser(student);
        enrollmentRepository.save(enrollment);

        mockMvc.perform(get("/api/v1/courses/" + course.getId() + "/learn")
                .with(user(studentDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(course.getId().toString()))
                .andExpect(jsonPath("$.title").value(course.getTitle()));
    }

    @Test
    void getCourseLearningMaterial_ShouldReturn403_WhenNotEnrolled() throws Exception {
        Course course = createCourse(BigDecimal.ZERO, PublishStatus.PUBLISHED);

        mockMvc.perform(get("/api/v1/courses/" + course.getId() + "/learn")
                .with(user(studentDetails)))
                .andExpect(status().isForbidden());
    }

    private Course createCourse(BigDecimal price, PublishStatus publishStatus) {
        Course course = new Course();
        course.setInstructorId(instructor.getId());
        course.setTitle("Test Course");
        course.setDescription("Desc");
        course.setCategory("Java");
        course.setDifficulty(CourseDifficulty.BEGINNER);
        course.setPrice(price);
        course.setPublishStatus(publishStatus);
        return courseRepository.save(course);
    }
}
