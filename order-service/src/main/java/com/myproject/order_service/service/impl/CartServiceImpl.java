package com.myproject.order_service.service.impl;

import com.myproject.order_service.Client.RestaurantClient;
import com.myproject.order_service.Validator.CartBusinessValidator;
import com.myproject.order_service.dao.CartDAO;
import com.myproject.order_service.dto.client.MenuItemResponse;
import com.myproject.order_service.dto.request.AddCartItemRequest;
import com.myproject.order_service.dto.response.BaseResponse;
import com.myproject.order_service.dto.response.CartResponse;
import com.myproject.order_service.entity.Cart;
import com.myproject.order_service.entity.CartItem;
import com.myproject.order_service.exception.BadRequestException;
import com.myproject.order_service.exception.ForbiddenException;
import com.myproject.order_service.exception.NotFoundException;
import com.myproject.order_service.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartDAO cartDAO;

    private final RestaurantClient restaurantClient;

    private final CartBusinessValidator validator;

    @Override
    public BaseResponse<Void> addItemToCart(Long customerId,String role, AddCartItemRequest request) {

        if(!role.equals("Customer")) {
            throw new ForbiddenException("Only customer can add cart items");
        }
        // 1. find cart
        Cart cart = cartDAO.findByCustomerAndRestaurant(customerId, request.getRestaurantId());

        Long cartId;
        // 2. create cart if not exists
        if (cart == null) {
            cartId = cartDAO.createNewCart(customerId, request.getRestaurantId());
        }
        else {
            cartId = cart.getCartId();
        }

        // 3. call restaurant-service
        BaseResponse<MenuItemResponse> response = restaurantClient.getMenuItem(request.getItemId());
        MenuItemResponse item = response.getData();

        // 4. validate item
        if (item == null) {
            throw new NotFoundException("Item not found");
        }

        if(!item.getRestaurantId().equals(request.getRestaurantId())) {
            throw new BadRequestException(
                    "Item does not belong to restaurant"
            );
        }


        // 5. find existing cart item
        CartItem existingItem = cartDAO.findCartItem(cartId, request.getItemId());

        // 6A. item exists -> update quantity
        if (existingItem != null) {
            Integer currentQuantity = existingItem.getQuantity();
            Integer updateQuantity = request.getQuantity();
            Integer cartQuantity = cartDAO.getCartQuantity(cartId);

            validator.validateItemQuantity(currentQuantity,updateQuantity);

            Integer newQuantity = currentQuantity + updateQuantity;
            Integer futureCartQuantity = cartQuantity + newQuantity;

            validator.validateCartQuantity(futureCartQuantity);

            cartDAO.updateQuantity(existingItem.getCartItemId(), newQuantity);
        }

        // 6B. item not exists -> insert
        else {

            cartDAO.addItemToCart(
                    customerId,
                    cartId,
                    request,
                    item.getItemName(),
                    item.getItemPrice()
            );
        }

        return new BaseResponse<>(
                true,
                "Add item to cart successfully",
                null
        );
    }

    @Override
    public BaseResponse<CartResponse> getDetailedCart(Long customerId,String role, Long restaurantId) {

        if(!role.equals("Customer")) {
            throw new ForbiddenException("Only customer can see detailed cart");
        }
        Cart cart = cartDAO.findByCustomerAndRestaurant(customerId,restaurantId);

        if(cart == null) {
            throw new NotFoundException("Cart not found");
        }

        CartResponse detailedInfo = cartDAO.getDetailedCartbyRestaurant(customerId,restaurantId);
        System.out.println(detailedInfo);

        return new BaseResponse<>(
                true,
                "Get detailed cart",
                detailedInfo
        );
    }
}

