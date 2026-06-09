package com.myproject.auth_service.kafka;

import com.myproject.auth_service.dto.kafka.RestaurantOwnerCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantOwnerProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC =
            "restaurant-owner-created";

    public void sendRestaurantOwnerCreatedEvent(
            RestaurantOwnerCreatedEvent event
    ) {

        kafkaTemplate.send(TOPIC, event);
    }
}
