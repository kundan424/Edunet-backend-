package com.edtech.platform.notification.service;

import com.edtech.platform.common.exception.EdTechException;
import com.edtech.platform.common.exception.ErrorCode;
import com.edtech.platform.notification.dto.NotificationResponse;
import com.edtech.platform.notification.entity.Notification;
import com.edtech.platform.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUserNotifications(UUID userId, Boolean unread, Pageable pageable) {
        Page<Notification> notifications;
        if (unread != null) {
            notifications = notificationRepository.findByUserIdAndIsRead(userId, unread, pageable);
        } else {
            notifications = notificationRepository.findByUserId(userId, pageable);
        }
        return notifications.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public NotificationResponse getNotification(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EdTechException(ErrorCode.RESOURCE_NOT_FOUND, "Notification not found"));
        
        if (!notification.getUser().getId().equals(userId)) {
            throw new EdTechException(ErrorCode.FORBIDDEN, "You can only access your own notifications");
        }
        
        return mapToResponse(notification);
    }

    @Transactional
    public NotificationResponse markAsRead(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EdTechException(ErrorCode.RESOURCE_NOT_FOUND, "Notification not found"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new EdTechException(ErrorCode.FORBIDDEN, "You can only mark your own notifications as read");
        }

        if (!notification.isRead()) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            notification = notificationRepository.save(notification);
        }

        return mapToResponse(notification);
    }

    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsRead(userId, LocalDateTime.now());
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .referenceType(notification.getReferenceType())
                .referenceId(notification.getReferenceId())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .build();
    }
}
