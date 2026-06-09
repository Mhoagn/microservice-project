package com.myproject.order_service.controller;

import com.myproject.order_service.dto.request.CheckoutOrderRequest;
import com.myproject.order_service.dto.response.BaseResponse;
import com.myproject.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("{orderId}")
    public ResponseEntity<BaseResponse> getDetailedOrder(@RequestHeader("X-User-Id") Long customerId,
                                                         @RequestHeader("X-Role") String role,
                                                         @PathVariable Long orderId) {
        BaseResponse response = orderService.getOrderDetailed(customerId,role,orderId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/checkout")
    public ResponseEntity<BaseResponse> checkOutCart(@RequestHeader("X-User-Id") Long customerId,
                                                     @RequestHeader("X-Role") String role,
                                                     @RequestBody CheckoutOrderRequest request) {
        BaseResponse response = orderService.createNewOrder(customerId,role,request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}

