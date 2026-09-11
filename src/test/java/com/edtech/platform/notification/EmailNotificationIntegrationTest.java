package com.edtech.platform.notification;

import com.edtech.platform.email.service.EmailService;
import com.edtech.platform.notification.entity.Notification;
import com.edtech.platform.notification.repository.NotificationRepository;
import com.edtech.platform.user.entity.User;
import com.edtech.platform.user.repository.UserRepository;
import com.edtech.platform.user.event.InstructorVerifiedEvent;
import com.edtech.platform.course.event.CoursePublishedEvent;
import com.edtech.platform.payment.event.PaymentSucceededEvent;
import com.edtech.platform.assignment.event.AssignmentGradedEvent;
import com.edtech.platform.quiz.event.QuizCompletedEvent;
import com.edtech.platform.enrollment.event.CourseEnrollmentEvent;
import com.edtech.platform.user.dto.EmailPreferenceDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class EmailNotificationIntegrationTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    @Autowired 
    private TransactionTemplate transactionTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @MockBean
    private EmailService emailService;

    private User testUser;

    @BeforeEach
    void setup() {
        
        

        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("" + java.util.UUID.randomUUID().toString() + "@email.com");
        testUser.setPasswordHash("hash");
        testUser.setRole(com.edtech.platform.user.entity.Role.STUDENT);
        testUser.setStatus(com.edtech.platform.user.entity.UserStatus.ACTIVE);
        testUser.setEmailNotificationsEnabled(true);
        testUser = userRepository.save(testUser);
    }
    
    @AfterEach
    void cleanup() {
        notificationRepository.deleteAll();
        userRepository.delete(testUser);
        
        
    }

    @Test
    void instructorVerification_triggersEmail() {
        InstructorVerifiedEvent event = new InstructorVerifiedEvent(testUser.getId(), UUID.randomUUID());
        transactionTemplate.execute(status -> { eventPublisher.publishEvent(event); return null; });
        verify(emailService, timeout(2000).times(1)).sendEmail(eq(testUser.getEmail()), anyString(), anyString());
    }

    @Test
    void coursePublication_triggersEmail() {
        CoursePublishedEvent event = new CoursePublishedEvent(UUID.randomUUID(), testUser.getId(), "Test Course");
        transactionTemplate.execute(status -> { eventPublisher.publishEvent(event); return null; });
        verify(emailService, timeout(2000).times(1)).sendEmail(eq(testUser.getEmail()), anyString(), anyString());
    }

    @Test
    void successfulPayment_triggersEmail() {
        PaymentSucceededEvent event = new PaymentSucceededEvent(testUser.getId(), UUID.randomUUID(), UUID.randomUUID(), "Test Course");
        transactionTemplate.execute(status -> { eventPublisher.publishEvent(event); return null; });
        verify(emailService, timeout(2000).times(1)).sendEmail(eq(testUser.getEmail()), anyString(), anyString());
    }

    @Test
    void assignmentGrading_triggersEmail() {
        AssignmentGradedEvent event = new AssignmentGradedEvent(testUser.getId(), UUID.randomUUID(), "Test Assignment");
        transactionTemplate.execute(status -> { eventPublisher.publishEvent(event); return null; });
        verify(emailService, timeout(2000).times(1)).sendEmail(eq(testUser.getEmail()), anyString(), anyString());
    }

    @Test
    void quizCompletion_triggersEmail() {
        QuizCompletedEvent event = new QuizCompletedEvent(testUser.getId(), UUID.randomUUID(), "Test Quiz", 95.5);
        transactionTemplate.execute(status -> { eventPublisher.publishEvent(event); return null; });
        verify(emailService, timeout(2000).times(1)).sendEmail(eq(testUser.getEmail()), anyString(), anyString());
    }

    @Test
    void enrollment_triggersEmail() {
        CourseEnrollmentEvent event = new CourseEnrollmentEvent(testUser.getId(), UUID.randomUUID(), "Test Course");
        transactionTemplate.execute(status -> { eventPublisher.publishEvent(event); return null; });
        verify(emailService, timeout(2000).times(1)).sendEmail(eq(testUser.getEmail()), anyString(), anyString());
    }

    @Test
    void disabledEmailPreference_preventsEmail_butInAppNotificationStillWorks() {
        testUser.setEmailNotificationsEnabled(false);
        userRepository.save(testUser);

        CoursePublishedEvent event = new CoursePublishedEvent(UUID.randomUUID(), testUser.getId(), "Test Course");
        transactionTemplate.execute(status -> { eventPublisher.publishEvent(event); return null; });

        verify(emailService, timeout(2000).times(0)).sendEmail(anyString(), anyString(), anyString());

        List<Notification> notifications = notificationRepository.findAll();
        assertEquals(1, notifications.size());
    }

    @Test
    void emailFailure_doesNotRollbackTransaction() {
        doThrow(new RuntimeException("SMTP Server Down")).when(emailService).sendEmail(anyString(), anyString(), anyString());
        
        CoursePublishedEvent event = new CoursePublishedEvent(UUID.randomUUID(), testUser.getId(), "Test Course");
        transactionTemplate.execute(status -> { eventPublisher.publishEvent(event); return null; });
        
        List<Notification> notifications = notificationRepository.findAll();
        assertEquals(1, notifications.size()); // In-app notification still persisted
    }

    @Test
    void duplicateEvent_doesNotGenerateDuplicateEmail() {
        CoursePublishedEvent event = new CoursePublishedEvent(UUID.randomUUID(), testUser.getId(), "Test Course");
        
        transactionTemplate.execute(status -> { eventPublisher.publishEvent(event); return null; });
        transactionTemplate.execute(status -> { eventPublisher.publishEvent(event); return null; });
        
        verify(emailService, timeout(2000).times(1)).sendEmail(eq(testUser.getEmail()), anyString(), anyString());
    }
}
