package com.myproject.order_service.service;

import com.myproject.order_service.dto.kafka.RestaurantCreatedEvent;

public interface RestaurantService {
    void createRestaurant(RestaurantCreatedEvent event);
}
