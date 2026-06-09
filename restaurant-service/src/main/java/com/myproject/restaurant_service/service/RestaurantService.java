package com.myproject.restaurant_service.service;

import com.myproject.restaurant_service.dto.kafka.RestaurantCreatedEvent;
import com.myproject.restaurant_service.dto.kafka.RestaurantOwnerCreatedEvent;
import com.myproject.restaurant_service.dto.request.RestaurantUpdateDTO;
import com.myproject.restaurant_service.dto.response.BaseResponse;
import com.myproject.restaurant_service.dto.response.DetailedRestaurantResponse;
import com.myproject.restaurant_service.dto.response.RestaurantResponse;
import com.myproject.restaurant_service.dto.response.ScrollResponse;

public interface RestaurantService {
    void createRestaurant(RestaurantOwnerCreatedEvent event);
    BaseResponse<ScrollResponse<RestaurantResponse>> getAllRestaurant(Long cursor, int limit);
    BaseResponse<Void> updateRestaurantInfo(RestaurantUpdateDTO request, Long ownerId, Long restaurantId);
    BaseResponse<DetailedRestaurantResponse> getDetailedRestaurant(Long restaurantId);
}
