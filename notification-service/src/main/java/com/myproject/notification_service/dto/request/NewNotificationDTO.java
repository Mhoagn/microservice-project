package com.myproject.notification_service.dto.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NewNotificationDTO {
    private Long customerId;

    private String notificationType;

    private String title;

    private String message;
}
