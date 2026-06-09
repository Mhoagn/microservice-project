package com.myproject.restaurant_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantUpdateDTO {
    private String restaurantName;
    private String restaurantAddress;
    private String restaurantPhone;
}
