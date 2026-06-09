package com.myproject.order_service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class DetailedOrderResponse {
    private Long orderId;

    private Long customerId;

    private Long restaurantId;

    private List<OrderItemResponse> items;

    private Double totalPrice;

    private String status;
    private Timestamp createdAt;
}
