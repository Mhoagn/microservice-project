package com.myproject.restaurant_service.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuItemDTO {
    private String item_name;
    private Double item_price;
    private String itemImageURL;
}
