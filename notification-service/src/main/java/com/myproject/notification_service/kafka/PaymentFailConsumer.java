package com.myproject.notification_service.kafka;

import com.myproject.notification_service.dto.kafka.PaymentFailedEvent;
import com.myproject.notification_service.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PaymentFailConsumer {
    private final NotificationService service;

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 2.0
            )
    )
    @KafkaListener(
            topics = "payment-failed",
            containerFactory = "paymentFailedFactory"
    )
    public void consume(PaymentFailedEvent event) {
        service.createNotificationForPaymentFailed(event);
    }

    @DltHandler
    public void dlt(PaymentFailedEvent event) {
        System.out.println("Dlt received " + event);
    }
}