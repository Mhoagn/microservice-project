package com.myproject.order_service.service.impl;

import com.myproject.order_service.dao.RestaurantDAO;
import com.myproject.order_service.dto.kafka.RestaurantCreatedEvent;
import com.myproject.order_service.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {
    private final RestaurantDAO restaurantDAO;

    @Override
    public void createRestaurant(RestaurantCreatedEvent event) {
        restaurantDAO.createRestaurant(event);
    }
}
