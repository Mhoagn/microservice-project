package com.myproject.notification_service.controller;

import com.myproject.notification_service.dto.response.BaseResponse;
import com.myproject.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService service;

    @GetMapping
    public ResponseEntity<BaseResponse> getNotifications(@RequestHeader("X-User-Id") Long customerId,
                                                         @RequestParam(required = false, defaultValue = "0") Long cursor,
                                                         @RequestParam(defaultValue = "10") int limit) {
        BaseResponse response = service.getNoficationByCustomerId(customerId,cursor,limit);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
