package com.myproject.notification_service.kafka;

import com.myproject.notification_service.dto.kafka.OrderCreatedEvent;
import com.myproject.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderConsumer {
    private final NotificationService service;

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 2.0
            )
    )
    @KafkaListener(
            topics = "order-created",
            containerFactory = "orderCreatedFactory"
    )
    public void consume(OrderCreatedEvent event) {
        service.createNotificationForOrder(event.getCustomerId(),event.getTotalAmount());
    }

    @DltHandler
    public void dlt(OrderCreatedEvent event) {
        System.out.println("Dlt received " + event);
    }
}