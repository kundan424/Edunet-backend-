package com.edtech.platform.notification.listener;

import com.edtech.platform.assignment.event.AssignmentGradedEvent;
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
        notification.setMessage("Your payment for " + event.getCourseTitle() + " was successful. You are now enrolled.");
        notification.setReferenceType("PAYMENT");
        notification.setReferenceId(event.getPaymentId().toString());

        notificationRepository.save(notification);
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
        notification.setMessage("Your assignment '" + event.getAssignmentTitle() + "' has been graded.");
        notification.setReferenceType("ASSIGNMENT_SUBMISSION");
        notification.setReferenceId(event.getSubmissionId().toString());

        notificationRepository.save(notification);
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
        notification.setMessage("Congratulations! Your instructor profile has been verified. You can now publish courses.");
        notification.setReferenceType("INSTRUCTOR_PROFILE");
        notification.setReferenceId(event.getProfileId().toString());

        notificationRepository.save(notification);
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
        notification.setMessage("You have completed the quiz '" + event.getQuizTitle() + "' with a score of " + String.format("%.1f", event.getScore()) + "%.");
        notification.setReferenceType("QUIZ_ATTEMPT");
        notification.setReferenceId(event.getAttemptId().toString());

        notificationRepository.save(notification);
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
        notification.setMessage("Congratulations! Your course '" + event.getCourseTitle() + "' has been approved and published.");
        notification.setReferenceType("COURSE");
        notification.setReferenceId(event.getCourseId().toString());

        notificationRepository.save(notification);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleCourseRejectedEvent(com.edtech.platform.course.event.CourseRejectedEvent event) {
        User user = userRepository.getReferenceById(event.getInstructorId());
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(NotificationType.COURSE_REJECTED);
        notification.setTitle("Course Rejected");
        notification.setMessage("Your course '" + event.getCourseTitle() + "' was rejected. Reason: " + event.getReason());
        notification.setReferenceType("COURSE");
        notification.setReferenceId(event.getCourseId().toString());

        notificationRepository.save(notification);
    }
}
