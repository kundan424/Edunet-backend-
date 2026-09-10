package com.edtech.platform.dashboard;

import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.enums.PublishStatus;
import com.edtech.platform.course.repository.CourseRepository;
import com.edtech.platform.enrollment.entity.Enrollment;
import com.edtech.platform.enrollment.enums.EnrollmentStatus;
import com.edtech.platform.enrollment.repository.EnrollmentRepository;
import com.edtech.platform.payment.entity.Payment;
import com.edtech.platform.payment.entity.PaymentStatus;
import com.edtech.platform.payment.repository.PaymentRepository;
import com.edtech.platform.notification.entity.Notification;import com.edtech.platform.notification.enums.NotificationType;
import com.edtech.platform.notification.repository.NotificationRepository;
import com.edtech.platform.user.entity.Role;
import com.edtech.platform.user.entity.User;
import com.edtech.platform.user.entity.UserStatus;
import com.edtech.platform.user.repository.UserRepository;
import com.edtech.platform.common.security.JwtUtils;
import com.edtech.platform.common.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.AnyOf.anyOf;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class DashboardIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private JwtUtils jwtUtils;

    private User student1;
    private User student2;
    private User instructor1;
    private User instructor2;
    private User admin;
    private Course course1;
    private String student1Token;
    private String student2Token;
    private String instructor1Token;
    private String instructor2Token;
    private String adminToken;

    @BeforeEach
    void setUp() {
        // Setup Admin
        admin = new User();
        admin.setEmail("admin-dash@test.com");
        admin.setPasswordHash("hash");
        admin.setName("Admin");
        admin.setRole(Role.ADMIN);
        admin.setStatus(UserStatus.ACTIVE);
        admin = userRepository.saveAndFlush(admin);
        adminToken = jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(UserDetailsImpl.build(admin), null, UserDetailsImpl.build(admin).getAuthorities()));

        // Setup Instructor 1
        instructor1 = new User();
        instructor1.setEmail("inst1-dash@test.com");
        instructor1.setPasswordHash("hash");
        instructor1.setName("Inst 1");
        instructor1.setRole(Role.INSTRUCTOR);
        instructor1.setStatus(UserStatus.ACTIVE);
        instructor1 = userRepository.saveAndFlush(instructor1);
        instructor1Token = jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(UserDetailsImpl.build(instructor1), null, UserDetailsImpl.build(instructor1).getAuthorities()));

        // Setup Instructor 2 (for isolation testing)
        instructor2 = new User();
        instructor2.setEmail("inst2-dash@test.com");
        instructor2.setPasswordHash("hash");
        instructor2.setName("Inst 2");
        instructor2.setRole(Role.INSTRUCTOR);
        instructor2.setStatus(UserStatus.ACTIVE);
        instructor2 = userRepository.saveAndFlush(instructor2);
        instructor2Token = jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(UserDetailsImpl.build(instructor2), null, UserDetailsImpl.build(instructor2).getAuthorities()));

        // Setup Student 1
        student1 = new User();
        student1.setEmail("stud1-dash@test.com");
        student1.setPasswordHash("hash");
        student1.setName("Stud 1");
        student1.setRole(Role.STUDENT);
        student1.setStatus(UserStatus.ACTIVE);
        student1 = userRepository.saveAndFlush(student1);
        student1Token = jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(UserDetailsImpl.build(student1), null, UserDetailsImpl.build(student1).getAuthorities()));

        // Setup Student 2 (for isolation testing)
        student2 = new User();
        student2.setEmail("stud2-dash@test.com");
        student2.setPasswordHash("hash");
        student2.setName("Stud 2");
        student2.setRole(Role.STUDENT);
        student2.setStatus(UserStatus.ACTIVE);
        student2 = userRepository.saveAndFlush(student2);
        student2Token = jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(UserDetailsImpl.build(student2), null, UserDetailsImpl.build(student2).getAuthorities()));

        // Setup Course for Instructor 1
        course1 = new Course();
        course1.setInstructorId(instructor1.getId());
        course1.setTitle("Test Course");
        course1.setPublishStatus(PublishStatus.PUBLISHED);
        course1.setPrice(BigDecimal.valueOf(100.00));
        course1 = courseRepository.saveAndFlush(course1);

        // Setup Enrollment for Student 1 in Course 1
        Enrollment e1 = new Enrollment();
        e1.setCourse(course1);
        e1.setUser(student1);
        e1.setStatus(EnrollmentStatus.ACTIVE);
        enrollmentRepository.saveAndFlush(e1);

        // Setup successful payment for Course 1 by Student 1
        Payment p1 = new Payment();
        p1.setUserId(student1.getId());
        p1.setCourseId(course1.getId());
        p1.setAmount(BigDecimal.valueOf(100.00));        p1.setProvider("STRIPE");
        p1.setCurrency("usd");
        p1.setStatus(PaymentStatus.SUCCEEDED);
        paymentRepository.saveAndFlush(p1);

        // Setup failed payment for Course 1 (should not be in revenue)
        Payment p2 = new Payment();
        p2.setUserId(student2.getId());
        p2.setCourseId(course1.getId());
        p2.setAmount(BigDecimal.valueOf(100.00));        p2.setProvider("STRIPE");
        p2.setCurrency("usd");
        p2.setStatus(PaymentStatus.FAILED);
        paymentRepository.saveAndFlush(p2);
        
        // Setup Notifications for Student 1
        Notification n = new Notification();
        n.setUser(student1);
        n.setTitle("Hello");        n.setType(NotificationType.COURSE_ENROLLED);
        n.setMessage("Test message");
        n.setRead(false);
        notificationRepository.saveAndFlush(n);
    }

    @Test
    void testStudentDashboardSuccessAndIsolation() throws Exception {
        // Student 1 accesses own dashboard
        mockMvc.perform(get("/api/v1/dashboard/student")
                .header("Authorization", "Bearer " + student1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.activeCourses", is(1)))
                .andExpect(jsonPath("$.data.unreadNotificationCount", is(1)))
                .andExpect(jsonPath("$.data.overallCourseCompletionPercentage", is(0.0))); // Safe zero calc
                
        // Instructor cannot access student dashboard
        mockMvc.perform(get("/api/v1/dashboard/student")
                .header("Authorization", "Bearer " + instructor1Token))
                .andExpect(status().isForbidden());
    }

    @Test
    void testInstructorDashboardSuccessAndRevenue() throws Exception {
        // Instructor 1 accesses own dashboard
        mockMvc.perform(get("/api/v1/dashboard/instructor")
                .header("Authorization", "Bearer " + instructor1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalCoursesOwned", is(1)))
                .andExpect(jsonPath("$.data.totalRevenue", is(100.0))); // Excludes failed payment

        // Instructor 2 has 0 revenue and 0 courses
        mockMvc.perform(get("/api/v1/dashboard/instructor")
                .header("Authorization", "Bearer " + instructor2Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalCoursesOwned", is(0)))
                .andExpect(jsonPath("$.data.totalRevenue", anyOf(is(0.0), is(0))));
    }

    @Test
    void testInstructorCourseAnalyticsOwnershipProtection() throws Exception {
        // Instructor 1 can access own course analytics
        mockMvc.perform(get("/api/v1/dashboard/instructor/courses/" + course1.getId())
                .header("Authorization", "Bearer " + instructor1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.successfulRevenue", is(100.0)));
                
        // Instructor 2 cannot access Instructor 1's course
        mockMvc.perform(get("/api/v1/dashboard/instructor/courses/" + course1.getId())
                .header("Authorization", "Bearer " + instructor2Token))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAdminDashboardRoleAuthorization() throws Exception {
        // Admin accesses admin dashboard
        mockMvc.perform(get("/api/v1/dashboard/admin")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.users.total", greaterThanOrEqualTo(5)));
                
        // Student cannot access admin dashboard
        mockMvc.perform(get("/api/v1/dashboard/admin")
                .header("Authorization", "Bearer " + student1Token))
                .andExpect(status().isForbidden());
    }
    
    @Test
    void testInstructorDashboardDateValidation() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/instructor?from=2026-12-31&to=2026-01-01")
                .header("Authorization", "Bearer " + instructor1Token))
                .andExpect(status().is5xxServerError()); // From after To
    }
}
