package com.myproject.order_service.controller;

import com.myproject.order_service.dto.request.AddCartItemRequest;
import com.myproject.order_service.dto.response.BaseResponse;
import com.myproject.order_service.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/items")
    public ResponseEntity<BaseResponse> addItemToCart(@RequestHeader("X-User-Id") Long customerId,
                                                      @RequestHeader("X-Role") String role,
                                                      @Valid @RequestBody AddCartItemRequest request) {
        BaseResponse response = cartService.addItemToCart(customerId, role,request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<BaseResponse> getDetailedCart(@RequestHeader("X-User-Id") Long customerId,
                                                        @RequestHeader("X-Role") String role,
                                                        @PathVariable Long restaurantId) {
        System.out.println(
                "customerId = " + customerId
        );
        BaseResponse response = cartService.getDetailedCart(customerId,role, restaurantId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
