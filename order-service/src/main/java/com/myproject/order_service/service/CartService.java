package com.myproject.order_service.service;

import com.myproject.order_service.dto.request.AddCartItemRequest;
import com.myproject.order_service.dto.response.BaseResponse;
import com.myproject.order_service.dto.response.CartResponse;

public interface CartService {
    BaseResponse<Void> addItemToCart(Long customerId,String role, AddCartItemRequest request);
    BaseResponse<CartResponse> getDetailedCart(Long customerId,String role, Long restaurantId);
}
