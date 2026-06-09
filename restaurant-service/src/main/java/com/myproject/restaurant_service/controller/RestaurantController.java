package com.myproject.restaurant_service.controller;

import com.myproject.restaurant_service.dto.request.MenuItemDTO;
import com.myproject.restaurant_service.dto.request.RestaurantUpdateDTO;
import com.myproject.restaurant_service.dto.response.BaseResponse;
import com.myproject.restaurant_service.dto.response.RestaurantResponse;
import com.myproject.restaurant_service.dto.response.ScrollResponse;
import com.myproject.restaurant_service.service.MenuItemService;
import com.myproject.restaurant_service.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService restaurantService;
    private final MenuItemService menuItemService;

    @GetMapping()
    public ResponseEntity<BaseResponse> getAllRestaurants(@RequestParam(required = false, defaultValue = "0") Long cursor,
                                                          @RequestParam(defaultValue = "10") int limit)
    {
        BaseResponse<ScrollResponse<RestaurantResponse>> response = restaurantService.getAllRestaurant(cursor,limit);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PatchMapping("/{restaurantId}")
    public ResponseEntity<BaseResponse> updateRestaurantInfo(
            @RequestHeader("X-User-Id") Long ownerId,
            @PathVariable Long restaurantId,
            @RequestBody RestaurantUpdateDTO request
    ) {
        return ResponseEntity.ok(
                restaurantService.updateRestaurantInfo(request, ownerId, restaurantId)
        );
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<BaseResponse> getDetailedRestaurant(@PathVariable Long restaurantId) {
        BaseResponse response = restaurantService.getDetailedRestaurant(restaurantId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/{restaurantId}/menu-items")
    public ResponseEntity<BaseResponse> getMenuItemByRestaurant(
            @PathVariable Long restaurantId,
            @RequestParam(required = false, defaultValue = "0") Long cursor,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(
                menuItemService.getMenuItemByRestaurant(restaurantId, cursor, limit)
        );
    }

    @PostMapping("{restaurantId}/menu-items")
    public ResponseEntity<BaseResponse> addMenuItem(@PathVariable Long restaurantId, @RequestHeader("X-User-Id") Long ownerId,
                                                    @RequestBody MenuItemDTO request) {
        BaseResponse response = menuItemService.addNewMenuItem(restaurantId,ownerId,request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}
