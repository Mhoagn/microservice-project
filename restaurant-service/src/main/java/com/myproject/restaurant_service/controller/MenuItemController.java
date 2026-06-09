package com.myproject.restaurant_service.controller;

import com.myproject.restaurant_service.dto.request.MenuItemDTO;
import com.myproject.restaurant_service.dto.response.BaseResponse;
import com.myproject.restaurant_service.service.MenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menu-items")
@RequiredArgsConstructor
public class MenuItemController {
    private final MenuItemService menuItemService;

    @PatchMapping("{itemId}")
    public ResponseEntity<BaseResponse> updateMenuItem(@PathVariable Long itemId,
                                                       @RequestHeader("X-User-Id") Long ownerId,
                                                       @RequestBody MenuItemDTO request) {
        BaseResponse response = menuItemService.updateMenuItem(itemId, ownerId, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("{itemId}")
    public ResponseEntity<BaseResponse> deleteMenuItem(@PathVariable Long itemId,
                                                       @RequestHeader("X-User-Id") Long ownerId) {
        BaseResponse response = menuItemService.deleteMenuItem(itemId, ownerId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("{itemId}")
    public ResponseEntity<BaseResponse> getMenuItemById(@PathVariable Long itemId) {
        BaseResponse response = menuItemService.getMenuItemById(itemId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
