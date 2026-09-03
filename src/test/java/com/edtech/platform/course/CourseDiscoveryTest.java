package com.edtech.platform.course;

import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.enums.CourseDifficulty;
import com.edtech.platform.course.enums.PublishStatus;
import com.edtech.platform.course.repository.CourseRepository;
import com.edtech.platform.user.entity.User;
import com.edtech.platform.user.entity.Role;
import com.edtech.platform.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CourseDiscoveryTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.edtech.platform.user.repository.InstructorProfileRepository instructorProfileRepository;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        instructorProfileRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void searchCourses_ShouldReturnPublishedCourses() throws Exception {
        User instructor = new User();
        instructor.setEmail("discovery_instructor@test.com");
        instructor.setPasswordHash("hash");
        instructor.setRole(Role.INSTRUCTOR);
        instructor.setStatus(com.edtech.platform.user.entity.UserStatus.ACTIVE);
        instructor.setName("John Doe");
        userRepository.save(instructor);

        Course publishedCourse = new Course();
        publishedCourse.setInstructorId(instructor.getId());
        publishedCourse.setTitle("Published Course");
        publishedCourse.setDescription("Desc");
        publishedCourse.setCategory("Java");
        publishedCourse.setDifficulty(CourseDifficulty.BEGINNER);
        publishedCourse.setPrice(new BigDecimal("10.0"));
        publishedCourse.setPublishStatus(PublishStatus.PUBLISHED);
        courseRepository.save(publishedCourse);

        Course draftCourse = new Course();
        draftCourse.setInstructorId(instructor.getId());
        draftCourse.setTitle("Draft Course");
        draftCourse.setDescription("Desc");
        draftCourse.setCategory("Java");
        draftCourse.setDifficulty(CourseDifficulty.BEGINNER);
        draftCourse.setPrice(new BigDecimal("10.0"));
        draftCourse.setPublishStatus(PublishStatus.DRAFT);
        courseRepository.save(draftCourse);

        mockMvc.perform(get("/api/v1/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value("Published Course"));
    }
}
