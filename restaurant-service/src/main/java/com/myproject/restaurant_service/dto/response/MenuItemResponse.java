package com.myproject.restaurant_service.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuItemResponse {
    private Long itemId;
    private Long restaurantId;
    private String itemName;
    private Double itemPrice;
    private String itemImageUrl;
}
