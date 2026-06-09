package com.myproject.order_service.controller;

import com.myproject.order_service.dto.response.BaseResponse;
import com.myproject.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer-orders")
@RequiredArgsConstructor
public class CustomerController {
    private final OrderService orderService;

    @GetMapping("/orders")
    public ResponseEntity<BaseResponse> getOrderByCustomer(@RequestHeader("X-User-Id") Long customerId,
                                                           @RequestHeader("X-Role") String role,
                                                           @RequestParam(required = false, defaultValue = "0") Long cursor,
                                                           @RequestParam(defaultValue = "10") int limit) {
        BaseResponse response = orderService.getOrderByCustomer(customerId,role,cursor,limit);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
