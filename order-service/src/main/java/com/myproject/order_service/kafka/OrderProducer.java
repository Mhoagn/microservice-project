package com.myproject.order_service.kafka;

import com.myproject.order_service.dto.kafka.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderProducer {

    private static final String ORDER_CREATED = "order-created";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderCreated(OrderCreatedEvent event) {

        kafkaTemplate.send(
                ORDER_CREATED,
                event.getOrderId().toString(),
                event
        );
    }
}