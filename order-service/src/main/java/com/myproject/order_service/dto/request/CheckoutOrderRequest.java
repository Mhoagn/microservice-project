package com.myproject.order_service.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutOrderRequest {
    private Long restaurantId;
    private String deliveryAddress;
}
