package com.myproject.restaurant_service.service;

import com.myproject.restaurant_service.dto.request.MenuItemDTO;
import com.myproject.restaurant_service.dto.response.BaseResponse;
import com.myproject.restaurant_service.dto.response.MenuItemResponse;
import com.myproject.restaurant_service.dto.response.ScrollResponse;

public interface MenuItemService {
    BaseResponse<ScrollResponse<MenuItemResponse>> getMenuItemByRestaurant(Long restaurantId,Long cursor, int limit);
    BaseResponse<Void> addNewMenuItem(Long restaurantId,Long ownerId, MenuItemDTO request);
    BaseResponse<Void> updateMenuItem(Long menuItemId, Long ownerId, MenuItemDTO request);
    BaseResponse<Void> deleteMenuItem(Long menuItemId, Long owner);

    BaseResponse<MenuItemResponse> getMenuItemById(Long itemId);
}
