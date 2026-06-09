package com.myproject.restaurant_service.kafka;

import com.myproject.restaurant_service.dto.kafka.RestaurantOwnerCreatedEvent;
import com.myproject.restaurant_service.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantOwnerConsumer {

    private final RestaurantService service;

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 2.0
            ),
            dltTopicSuffix = "-dlt"
    )
    @KafkaListener(
            topics = "restaurant-owner-created",
            groupId = "restaurant-group"
    )
    public void consume(
            RestaurantOwnerCreatedEvent event
    ) {
        service.createRestaurant(event);

    }

    @DltHandler
    public void dlt(RestaurantOwnerCreatedEvent event) {

        System.out.println("DLT received: " + event);
    }
}
