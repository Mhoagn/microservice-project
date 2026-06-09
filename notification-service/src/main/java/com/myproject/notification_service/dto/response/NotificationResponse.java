package com.myproject.notification_service.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class NotificationResponse {
    private Long notificationId;

    private String notificationType;

    private String title;

    private String message;

    private LocalDateTime createdAt;
}
