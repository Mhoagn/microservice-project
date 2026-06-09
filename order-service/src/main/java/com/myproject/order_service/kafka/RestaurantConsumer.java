package com.myproject.order_service.kafka;

import com.myproject.order_service.dto.kafka.RestaurantCreatedEvent;
import com.myproject.order_service.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RestaurantConsumer {
    private final RestaurantService service;

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 2.0
            )
    )
    @KafkaListener(
            topics = "restaurant-created",
            containerFactory = "restaurantCreatedFactory"
    )
    public void consume(RestaurantCreatedEvent event) {
        service.createRestaurant(event);
    }

    @DltHandler
    public void dlt(RestaurantCreatedEvent event) {
        System.out.println("Dlt received " + event);
    }
}
