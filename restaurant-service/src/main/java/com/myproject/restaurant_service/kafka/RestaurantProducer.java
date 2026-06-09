package com.myproject.restaurant_service.kafka;

import com.myproject.restaurant_service.dto.kafka.RestaurantCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishRestaurantCreated(
            RestaurantCreatedEvent event
    ) {

        kafkaTemplate.send(
                "restaurant-created",
                event.getRestaurantId().toString(),
                event
        );
    }
}