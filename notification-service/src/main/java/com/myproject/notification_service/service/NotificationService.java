package com.myproject.notification_service.service;

import com.myproject.notification_service.dto.kafka.PaymentFailedEvent;
import com.myproject.notification_service.dto.kafka.PaymentSucceededEvent;
import com.myproject.notification_service.dto.response.BaseResponse;
import com.myproject.notification_service.dto.response.ScrollResponse;

public interface NotificationService {
    BaseResponse<ScrollResponse> getNoficationByCustomerId(Long customerId, Long cursor, int limit);
    void createNotificationForOrder(Long customerId, Double amount);
    void createNotificationForPaymentSucceeded(PaymentSucceededEvent event);
    void createNotificationForPaymentFailed(PaymentFailedEvent event);
}
