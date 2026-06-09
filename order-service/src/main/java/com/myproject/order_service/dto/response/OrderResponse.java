package com.myproject.order_service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponse {
    private Long orderId;

    private Long customerId;

    private Long restaurantId;

    private Double totalPrice;

    private String status;

    private LocalDateTime createdAt;

    private String deliveryAddress;
}
