package com.myproject.payment_service.kafka;

import com.myproject.payment_service.dto.kafka.OrderCreatedEvent;
import com.myproject.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderConsumer {
    private final PaymentService service;

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
        service.createPayment(event.getOrderId(), event.getCustomerId(), event.getTotalAmount());
    }

    @DltHandler
    public void dlt(OrderCreatedEvent event) {
        System.out.println("Dlt received " + event);
    }
}
