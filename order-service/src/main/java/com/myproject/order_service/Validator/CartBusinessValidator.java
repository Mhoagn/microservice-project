package com.myproject.order_service.Validator;

import com.myproject.order_service.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class CartBusinessValidator {
    private static Integer MAX_QUANTITY_ITEM = 10;
    private static Integer MAX_QUANTITY_CART = 50;

    public void validateItemQuantity(Integer currentQuantity, Integer newQuantity) {
        if(currentQuantity + newQuantity > MAX_QUANTITY_ITEM) {
            throw new BadRequestException("Maximum item quantity exceeded");
        }
    }

    public void validateCartQuantity(Integer futureCartQuantity) {
        if(futureCartQuantity > MAX_QUANTITY_CART) {
            throw new BadRequestException("Maximum quantity exceeded");
        }
    }
}
