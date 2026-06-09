package com.myproject.restaurant_service.service.impl;

import com.myproject.restaurant_service.dao.MenuItemDAO;
import com.myproject.restaurant_service.dao.RestaurantDAO;
import com.myproject.restaurant_service.dto.request.MenuItemDTO;
import com.myproject.restaurant_service.dto.response.BaseResponse;
import com.myproject.restaurant_service.dto.response.MenuItemResponse;
import com.myproject.restaurant_service.dto.response.ScrollResponse;
import com.myproject.restaurant_service.entity.MenuItem;
import com.myproject.restaurant_service.entity.Restaurant;
import com.myproject.restaurant_service.exception.ForbiddenException;
import com.myproject.restaurant_service.exception.NotFoundException;
import com.myproject.restaurant_service.service.MenuItemService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MenuServiceImpl implements MenuItemService {
    private final MenuItemDAO menuItemDAO;
    private final RestaurantDAO restaurantDAO;


    @Override
    public BaseResponse<ScrollResponse<MenuItemResponse>> getMenuItemByRestaurant(
            Long restaurantId,
            Long cursor,
            int limit
    ) {
        Restaurant restaurant = restaurantDAO.findById(restaurantId);

        if (restaurant == null) {
            throw new NotFoundException("Restaurant not found");
        }

        List<MenuItem> menuItemList =
                menuItemDAO.getMenuItemByRestaurant(restaurantId, cursor, limit + 1);

        boolean hasNext = menuItemList.size() > limit;

        if (hasNext) {
            menuItemList.remove(menuItemList.size() - 1);
        }

        Long nextCursor = menuItemList.isEmpty()
                ? null
                : menuItemList.get(menuItemList.size() - 1).getItemId();

        List<MenuItemResponse> responseList = menuItemList.stream()
                .map(item -> {
                    MenuItemResponse res = new MenuItemResponse();
                    res.setItemId(item.getItemId());
                    res.setItemName(item.getItemName());
                    res.setItemPrice(item.getItemPrice());
                    res.setItemImageUrl(item.getItemImageURL());
                    return res;
                })
                .toList();

        ScrollResponse<MenuItemResponse> scrollResponse =
                new ScrollResponse<>(
                        responseList,
                        nextCursor,
                        hasNext
                );

        return new BaseResponse<>(
                true,
                "Get menu items successfully",
                scrollResponse
        );
    }

    @Override
    public BaseResponse<Void> addNewMenuItem(Long restaurantId,Long ownerId, MenuItemDTO request) {
        Restaurant restaurant = restaurantDAO.findById(restaurantId);

        if(restaurant == null) {
            throw new NotFoundException("Restaurant not found");
        }

        if(!restaurant.getOwnerId().equals(ownerId)) {
            throw new ForbiddenException("User can't add menu item");
        }

        menuItemDAO.addNewMenuItem(restaurantId,request);

        return new BaseResponse<>(
                true,
                "Add a menu item successfully",
                null
        );

    }

    @Override
    public BaseResponse<Void> updateMenuItem(Long menuItemId, Long ownerId, MenuItemDTO request) {
        MenuItem menuItem = menuItemDAO.findById(menuItemId);

        if(menuItem == null) {
            throw new NotFoundException("Menu Item not found");
        }

        Restaurant res = restaurantDAO.findById(menuItem.getRestaurantId());

        if(res == null) {
            throw new NotFoundException("Restaurant not found");
        }

        if(!res.getOwnerId().equals(ownerId)) {
            throw new ForbiddenException("User can't update menu item");
        }

        menuItemDAO.updateMenuItem(menuItemId,request);

        return new BaseResponse<>(
                true,
                "Update a menu item successfully",
                null
        );
    }

    @Override
    public BaseResponse<Void> deleteMenuItem(Long menuItemId, Long ownerId) {
        MenuItem menuItem = menuItemDAO.findById(menuItemId);

        if(menuItem == null) {
            throw new NotFoundException("Menu Item not found");
        }

        Restaurant restaurant = restaurantDAO.findById(menuItem.getRestaurantId());

        if(restaurant == null) {
            throw new NotFoundException("Restaurant not found");
        }

        if(!restaurant.getOwnerId().equals(ownerId)) {
            throw new ForbiddenException("User can't delete menu item");
        }

        menuItemDAO.deleteMenuItemById(menuItemId);

        return new BaseResponse<>(
                true,
                "Delete a menu item successfully",
                null
        );
    }

    @Override
    public BaseResponse<MenuItemResponse> getMenuItemById(Long itemId) {
        MenuItem menuItem = menuItemDAO.findById(itemId);

        if(menuItem == null) {
            throw new NotFoundException("Menu Item not found");
        }
        MenuItemResponse res = new MenuItemResponse();
        res.setItemId(menuItem.getItemId());
        res.setRestaurantId(menuItem.getRestaurantId());
        res.setItemName(menuItem.getItemName());
        res.setItemPrice(menuItem.getItemPrice());
        res.setItemImageUrl(menuItem.getItemImageURL());

        return new BaseResponse<>(
                true,
                "Get a menu item successfully",
                res
        );
    }


}

