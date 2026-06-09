package com.myproject.restaurant_service.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponse {
    private Long restaurantId;
    private String restaurantName;
}
