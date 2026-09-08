package com.edtech.platform.course;

import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.entity.Lesson;
import com.edtech.platform.course.entity.Section;
import com.edtech.platform.course.enums.CourseDifficulty;
import com.edtech.platform.course.enums.LessonType;
import com.edtech.platform.course.enums.PublishStatus;
import com.edtech.platform.course.repository.CourseRepository;
import com.edtech.platform.user.entity.Role;
import com.edtech.platform.user.entity.User;
import com.edtech.platform.user.entity.UserStatus;
import com.edtech.platform.user.repository.InstructorProfileRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private InstructorProfileRepository instructorProfileRepository;

    @BeforeEach
    void setUp() {
        // No manual cleanup needed — @Transactional rolls back after each test.
        // Explicit deleteAll() would fail if persistent smoke-test data has FK constraints.
    }

    @Test
    void searchCourses_ShouldReturnPublishedCourses() throws Exception {
        User instructor = User.builder()
                .email("discovery_instructor@test.com")
                .passwordHash("hash")
                .role(Role.INSTRUCTOR)
                .status(UserStatus.ACTIVE)
                .name("John Doe")
                .build();
        userRepository.save(instructor);

        Course publishedCourse = new Course();
        publishedCourse.setInstructorId(instructor.getId());
        publishedCourse.setTitle("Published Course");
        publishedCourse.setDescription("Desc");
        publishedCourse.setCategory("DiscoveryTestCategory");
        publishedCourse.setDifficulty(CourseDifficulty.BEGINNER);
        publishedCourse.setPrice(new BigDecimal("10.0"));
        publishedCourse.setPublishStatus(PublishStatus.PUBLISHED);
        courseRepository.save(publishedCourse);

        Course draftCourse = new Course();
        draftCourse.setInstructorId(instructor.getId());
        draftCourse.setTitle("Draft Course");
        draftCourse.setDescription("Desc");
        draftCourse.setCategory("DiscoveryTestCategory");
        draftCourse.setDifficulty(CourseDifficulty.BEGINNER);
        draftCourse.setPrice(new BigDecimal("10.0"));
        draftCourse.setPublishStatus(PublishStatus.DRAFT);
        courseRepository.save(draftCourse);

        // Filter by unique category to isolate this test's data from persistent smoke-test records
        mockMvc.perform(get("/api/v1/courses?category=DiscoveryTestCategory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value("Published Course"));
    }

    /**
     * REGRESSION TEST — Smoke test bug #2.
     * Proves that GET /api/v1/courses/{courseId} does NOT throw
     * MultipleBagFetchException when the course has sections with lessons.
     * Also verifies correct ordering and DTO mapping.
     */
    @Test
    void getCourseDetail_ShouldReturnSectionsAndLessons() throws Exception {
        User instructor = User.builder()
                .email("detail_instructor@test.com")
                .passwordHash("hash")
                .name("Detail Instructor")
                .role(Role.INSTRUCTOR)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(instructor);

        // Create an InstructorProfile so the service can resolve the instructor name
        com.edtech.platform.user.entity.InstructorProfile profile =
                com.edtech.platform.user.entity.InstructorProfile.builder()
                        .user(instructor)
                        .bio("Test bio")
                        .expertise("Java")
                        .verificationStatus(com.edtech.platform.user.entity.VerificationStatus.VERIFIED)
                        .build();
        instructorProfileRepository.save(profile);

        Course course = new Course();
        course.setTitle("Detailed Course");
        course.setDescription("A course with sections and lessons");
        course.setCategory("Programming");
        course.setDifficulty(CourseDifficulty.BEGINNER);
        course.setPrice(new BigDecimal("0.00"));
        course.setInstructorId(instructor.getId());
        course.setPublishStatus(PublishStatus.PUBLISHED);

        // Section 2 (display_order=2) added first to verify ordering
        Section section2 = new Section();
        section2.setTitle("Section 2");
        section2.setDisplayOrder(2);
        section2.setCourse(course);
        course.getSections().add(section2);

        // Section 1 (display_order=1) added second
        Section section1 = new Section();
        section1.setTitle("Section 1");
        section1.setDisplayOrder(1);
        section1.setCourse(course);
        course.getSections().add(section1);

        // Lesson B (display_order=2) added first to verify ordering
        Lesson lessonB = new Lesson();
        lessonB.setTitle("Lesson B");
        lessonB.setLessonType(LessonType.TEXT);
        lessonB.setDisplayOrder(2);
        lessonB.setSection(section1);
        section1.getLessons().add(lessonB);

        // Lesson A (display_order=1) added second
        Lesson lessonA = new Lesson();
        lessonA.setTitle("Lesson A");
        lessonA.setLessonType(LessonType.TEXT);
        lessonA.setDisplayOrder(1);
        lessonA.setSection(section1);
        section1.getLessons().add(lessonA);

        // Single lesson in section 2
        Lesson lessonC = new Lesson();
        lessonC.setTitle("Lesson C");
        lessonC.setLessonType(LessonType.VIDEO);
        lessonC.setDisplayOrder(1);
        lessonC.setSection(section2);
        section2.getLessons().add(lessonC);

        courseRepository.save(course);

        mockMvc.perform(get("/api/v1/courses/" + course.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Detailed Course")))
                .andExpect(jsonPath("$.instructorName", is("Detail Instructor")))
                // Two sections
                .andExpect(jsonPath("$.sections", hasSize(2)))
                // Sections sorted by displayOrder
                .andExpect(jsonPath("$.sections[0].title", is("Section 1")))
                .andExpect(jsonPath("$.sections[0].displayOrder", is(1)))
                .andExpect(jsonPath("$.sections[1].title", is("Section 2")))
                .andExpect(jsonPath("$.sections[1].displayOrder", is(2)))
                // Lessons in section 1 sorted by displayOrder
                .andExpect(jsonPath("$.sections[0].lessons", hasSize(2)))
                .andExpect(jsonPath("$.sections[0].lessons[0].title", is("Lesson A")))
                .andExpect(jsonPath("$.sections[0].lessons[0].displayOrder", is(1)))
                .andExpect(jsonPath("$.sections[0].lessons[1].title", is("Lesson B")))
                .andExpect(jsonPath("$.sections[0].lessons[1].displayOrder", is(2)))
                // Lesson in section 2
                .andExpect(jsonPath("$.sections[1].lessons", hasSize(1)))
                .andExpect(jsonPath("$.sections[1].lessons[0].title", is("Lesson C")));
    }

    /**
     * Verifies that a non-existent course ID returns 404.
     */
    @Test
    void getCourseDetail_ShouldReturn404_WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/courses/" + java.util.UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    /**
     * Verifies that a DRAFT course is not accessible via public detail endpoint.
     */
    @Test
    void getCourseDetail_ShouldReturn404_WhenDraft() throws Exception {
        User instructor = User.builder()
                .email("draft_detail_instructor@test.com")
                .passwordHash("hash")
                .name("Draft Instructor")
                .role(Role.INSTRUCTOR)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(instructor);

        Course draftCourse = new Course();
        draftCourse.setTitle("Draft Course");
        draftCourse.setDescription("Should not be visible");
        draftCourse.setCategory("Programming");
        draftCourse.setDifficulty(CourseDifficulty.BEGINNER);
        draftCourse.setPrice(new BigDecimal("0.00"));
        draftCourse.setInstructorId(instructor.getId());
        draftCourse.setPublishStatus(PublishStatus.DRAFT);
        courseRepository.save(draftCourse);

        mockMvc.perform(get("/api/v1/courses/" + draftCourse.getId()))
                .andExpect(status().isNotFound());
    }
}
