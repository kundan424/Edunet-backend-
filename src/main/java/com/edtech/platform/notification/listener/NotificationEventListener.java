package com.edtech.platform.notification.listener;

import com.edtech.platform.assignment.event.AssignmentGradedEvent;
import com.edtech.platform.email.service.EmailService;
import com.edtech.platform.enrollment.event.CourseEnrollmentEvent;
import com.edtech.platform.notification.entity.Notification;
import com.edtech.platform.notification.enums.NotificationType;
import com.edtech.platform.notification.repository.NotificationRepository;
import com.edtech.platform.payment.event.PaymentSucceededEvent;
import com.edtech.platform.quiz.event.QuizCompletedEvent;
import com.edtech.platform.user.entity.User;
import com.edtech.platform.user.event.InstructorVerifiedEvent;
import com.edtech.platform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handlePaymentSucceededEvent(PaymentSucceededEvent event) {
        if (notificationRepository.existsByUserIdAndReferenceTypeAndReferenceIdAndType(
                event.getUserId(), "PAYMENT", event.getPaymentId().toString(), NotificationType.PAYMENT_SUCCESS)) {
            return;
        }

        User user = userRepository.getReferenceById(event.getUserId());
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(NotificationType.PAYMENT_SUCCESS);
        notification.setTitle("Payment Successful");
        String msg = "Your payment for " + event.getCourseTitle() + " was successful. You are now enrolled.";
        notification.setMessage(msg);
        notification.setReferenceType("PAYMENT");
        notification.setReferenceId(event.getPaymentId().toString());

        notificationRepository.save(notification);
        sendEmailIfEnabled(user, "Payment Successful", msg);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleAssignmentGradedEvent(AssignmentGradedEvent event) {
        if (notificationRepository.existsByUserIdAndReferenceTypeAndReferenceIdAndType(
                event.getStudentId(), "ASSIGNMENT_SUBMISSION", event.getSubmissionId().toString(), NotificationType.ASSIGNMENT_GRADED)) {
            return;
        }

        User user = userRepository.getReferenceById(event.getStudentId());
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(NotificationType.ASSIGNMENT_GRADED);
        notification.setTitle("Assignment Graded");
        String msg = "Your assignment '" + event.getAssignmentTitle() + "' has been graded.";
        notification.setMessage(msg);
        notification.setReferenceType("ASSIGNMENT_SUBMISSION");
        notification.setReferenceId(event.getSubmissionId().toString());

        notificationRepository.save(notification);
        sendEmailIfEnabled(user, "Assignment Graded", msg);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleInstructorVerifiedEvent(InstructorVerifiedEvent event) {
        if (notificationRepository.existsByUserIdAndReferenceTypeAndReferenceIdAndType(
                event.getInstructorId(), "INSTRUCTOR_PROFILE", event.getProfileId().toString(), NotificationType.INSTRUCTOR_VERIFIED)) {
            return;
        }

        User user = userRepository.getReferenceById(event.getInstructorId());
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(NotificationType.INSTRUCTOR_VERIFIED);
        notification.setTitle("Profile Verified");
        String msg = "Congratulations! Your instructor profile has been verified. You can now publish courses.";
        notification.setMessage(msg);
        notification.setReferenceType("INSTRUCTOR_PROFILE");
        notification.setReferenceId(event.getProfileId().toString());

        notificationRepository.save(notification);
        sendEmailIfEnabled(user, "Profile Verified", msg);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleQuizCompletedEvent(QuizCompletedEvent event) {
        if (notificationRepository.existsByUserIdAndReferenceTypeAndReferenceIdAndType(
                event.getStudentId(), "QUIZ_ATTEMPT", event.getAttemptId().toString(), NotificationType.QUIZ_COMPLETED)) {
            return;
        }

        User user = userRepository.getReferenceById(event.getStudentId());
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(NotificationType.QUIZ_COMPLETED);
        notification.setTitle("Quiz Completed");
        String msg = "You have completed the quiz '" + event.getQuizTitle() + "' with a score of " + String.format("%.1f", event.getScore()) + "%.";
        notification.setMessage(msg);
        notification.setReferenceType("QUIZ_ATTEMPT");
        notification.setReferenceId(event.getAttemptId().toString());

        notificationRepository.save(notification);
        sendEmailIfEnabled(user, "Quiz Completed", msg);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleCoursePublishedEvent(com.edtech.platform.course.event.CoursePublishedEvent event) {
        if (notificationRepository.existsByUserIdAndReferenceTypeAndReferenceIdAndType(
                event.getInstructorId(), "COURSE", event.getCourseId().toString(), NotificationType.COURSE_PUBLISHED)) {
            return;
        }

        User user = userRepository.getReferenceById(event.getInstructorId());
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(NotificationType.COURSE_PUBLISHED);
        notification.setTitle("Course Published");
        String msg = "Congratulations! Your course '" + event.getCourseTitle() + "' has been approved and published.";
        notification.setMessage(msg);
        notification.setReferenceType("COURSE");
        notification.setReferenceId(event.getCourseId().toString());

        notificationRepository.save(notification);
        sendEmailIfEnabled(user, "Course Published", msg);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleCourseRejectedEvent(com.edtech.platform.course.event.CourseRejectedEvent event) {
        User user = userRepository.getReferenceById(event.getInstructorId());
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(NotificationType.COURSE_REJECTED);
        notification.setTitle("Course Rejected");
        String msg = "Your course '" + event.getCourseTitle() + "' was rejected. Reason: " + event.getReason();
        notification.setMessage(msg);
        notification.setReferenceType("COURSE");
        notification.setReferenceId(event.getCourseId().toString());

        notificationRepository.save(notification);
        sendEmailIfEnabled(user, "Course Rejected", msg);
    }
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleCourseEnrollmentEvent(CourseEnrollmentEvent event) {
        // Enrolled events might not have a specific NotificationType yet, let's assume we can just use SYSTEM or create ENROLLMENT_SUCCESS if possible.
        // Wait, NotificationType is an enum. Let's check what it has.
        // I will just use PAYMENT_SUCCESS or a generic one if it doesn't exist, wait, the prompt doesn't mandate an in-app notification for enrollment, 
        // but it does say: "Course enrollment confirmation -> email".
        // Let's check NotificationType enum first before saving in-app notification, or I can skip the in-app notification for free enrollments.
        // Actually, let's just send the email!
        User user = userRepository.getReferenceById(event.getUserId());
        String msg = "You have successfully enrolled in the course: " + event.getCourseTitle() + ".";
        sendEmailIfEnabled(user, "Course Enrollment Confirmation", msg);
    }

    private void sendEmailIfEnabled(User user, String subject, String body) {
        if (user.isEmailNotificationsEnabled()) {
            try {
                emailService.sendEmail(user.getEmail(), subject, body);
            } catch (Exception e) {
                log.error("Failed to send email to {}", user.getEmail(), e);
            }
        }
    }
}
