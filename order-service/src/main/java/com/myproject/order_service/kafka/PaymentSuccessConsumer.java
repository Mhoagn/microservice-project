package com.myproject.order_service.kafka;

import com.myproject.order_service.dto.kafka.PaymentSucceededEvent;
import com.myproject.order_service.dto.kafka.RestaurantCreatedEvent;
import com.myproject.order_service.service.OrderService;
import com.myproject.order_service.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentSuccessConsumer {
    private final OrderService service;

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 2.0
            )
    )
    @KafkaListener(
            topics = "payment-succeeded",
            containerFactory = "paymentSucceededFactory"
    )
    public void consume(
            PaymentSucceededEvent event
    ) {
        service.updateOrderStatusPaid(event.getOrderId());
    }

    @DltHandler
    public void dlt(PaymentSucceededEvent event) {
        System.out.println("DLT received " + event);
    }
}
