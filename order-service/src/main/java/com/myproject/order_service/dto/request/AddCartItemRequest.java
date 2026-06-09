package com.myproject.order_service.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Setter
@Getter
public class AddCartItemRequest {
    @NotNull
    private Long restaurantId;

    @NotNull
    private Long itemId;

    @Min(1)
    @Max(10)
    private Integer quantity;
}
