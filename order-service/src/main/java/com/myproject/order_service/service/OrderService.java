package com.myproject.order_service.service;

import com.myproject.order_service.dto.request.CheckoutOrderRequest;
import com.myproject.order_service.dto.response.BaseResponse;
import com.myproject.order_service.dto.response.DetailedOrderResponse;
import com.myproject.order_service.dto.response.OrderResponse;
import com.myproject.order_service.dto.response.ScrollResponse;
import com.myproject.order_service.entity.Order;

public interface OrderService {
    BaseResponse<DetailedOrderResponse> getOrderDetailed(Long customerId, String role, Long orderId);
    BaseResponse<ScrollResponse<OrderResponse>> getOrderByCustomer(Long customerId, String role,Long cursor, int limit);
    BaseResponse<ScrollResponse<OrderResponse>> getOrderByRestaurant(Long ownerId, String role, Long cursor, int limit);
    BaseResponse<Void> createNewOrder(Long customerId, String role,CheckoutOrderRequest request);
    void updateOrderStatusPaid(Long orderId);
    void updateOrderStatusFailed(Long orderId);
}
