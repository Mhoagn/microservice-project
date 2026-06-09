package com.myproject.order_service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class CartResponse {
    private Long cartId;

    private Long customerId;

    private Long restaurantId;

    private List<CartItemResponse> items;

    private Double totalPrice;

    private Timestamp createdAt;
}
