package com.myproject.order_service.kafka;

import com.myproject.order_service.dto.kafka.OrderCreatedEvent;
import com.myproject.order_service.dto.kafka.PaymentFailedEvent;
import com.myproject.order_service.dto.kafka.PaymentSucceededEvent;
import com.myproject.order_service.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PaymentFailConsumer {
    private final OrderService service;

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 2.0
            )
    )
    @KafkaListener(topics = "payment-failed",
            containerFactory = "paymentFailedFactory"
    )
    public void consume(PaymentFailedEvent event) {
        service.updateOrderStatusFailed(event.getOrderId());
    }

    @DltHandler
    public void dlt(PaymentFailConsumer event) {
        System.out.println("DLT received: " + event);
    }
}
