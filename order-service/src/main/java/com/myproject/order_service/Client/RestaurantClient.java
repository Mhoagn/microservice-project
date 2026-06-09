package com.myproject.order_service.Client;

import com.myproject.order_service.dto.client.MenuItemResponse;
import com.myproject.order_service.dto.response.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "restaurant-service")
public interface RestaurantClient {

    @GetMapping("/api/menu-items/{itemId}")
    BaseResponse<MenuItemResponse> getMenuItem(@PathVariable Long itemId);
}