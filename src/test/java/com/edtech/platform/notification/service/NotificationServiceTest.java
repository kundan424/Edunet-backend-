package com.edtech.platform.notification.service;

import com.edtech.platform.common.exception.EdTechException;
import com.edtech.platform.common.exception.ErrorCode;
import com.edtech.platform.notification.dto.NotificationResponse;
import com.edtech.platform.notification.entity.Notification;
import com.edtech.platform.notification.enums.NotificationType;
import com.edtech.platform.notification.repository.NotificationRepository;
import com.edtech.platform.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private UUID userId;
    private User user;
    private Notification notification;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);

        notification = new Notification();
        notification.setId(UUID.randomUUID());
        notification.setUser(user);
        notification.setType(NotificationType.PAYMENT_SUCCESS);
        notification.setTitle("Payment Success");
        notification.setMessage("Success");
        notification.setRead(false);
    }

    @Test
    void testGetNotification_Success() {
        when(notificationRepository.findById(notification.getId())).thenReturn(Optional.of(notification));
        
        NotificationResponse response = notificationService.getNotification(userId, notification.getId());
        
        assertNotNull(response);
        assertEquals(notification.getId(), response.getId());
    }

    @Test
    void testGetNotification_OtherUser_ThrowsException() {
        when(notificationRepository.findById(notification.getId())).thenReturn(Optional.of(notification));
        
        EdTechException ex = assertThrows(EdTechException.class, () -> 
            notificationService.getNotification(UUID.randomUUID(), notification.getId()));
            
        assertEquals(ErrorCode.FORBIDDEN, ex.getErrorCode());
    }

    @Test
    void testMarkAsRead_Success() {
        when(notificationRepository.findById(notification.getId())).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        
        NotificationResponse response = notificationService.markAsRead(userId, notification.getId());
        
        assertTrue(response.isRead());
        assertNotNull(response.getReadAt());
        verify(notificationRepository).save(notification);
    }

    @Test
    void testGetUserNotifications_UnreadOnly() {
        Page<Notification> page = new PageImpl<>(List.of(notification));
        when(notificationRepository.findByUserIdAndIsRead(userId, false, PageRequest.of(0, 10))).thenReturn(page);
        
        Page<NotificationResponse> result = notificationService.getUserNotifications(userId, false, PageRequest.of(0, 10));
        
        assertEquals(1, result.getTotalElements());
        assertFalse(result.getContent().get(0).isRead());
    }
}
