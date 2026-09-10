import os
path = 'src/main/java/com/edtech/platform/notification/listener/NotificationEventListener.java'
with open(path, 'r', encoding='utf-8') as f:
    c = f.read()

new_methods = '''
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
'''
c = c.replace('}\n', new_methods)

with open(path, 'w', encoding='utf-8') as f:
    f.write(c)
