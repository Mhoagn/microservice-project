package com.myproject.order_service.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemResponse {
    private Long cartItemId;

    private Long itemId;

    private String itemName;

    private Double itemPrice;

    private Integer quantity;

    private Double subtotal;
}
