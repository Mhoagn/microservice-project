package com.myproject.order_service.service.impl;

import com.myproject.order_service.dao.CartDAO;
import com.myproject.order_service.dao.OrderDAO;
import com.myproject.order_service.dao.RestaurantDAO;
import com.myproject.order_service.dto.kafka.OrderCreatedEvent;
import com.myproject.order_service.dto.request.CheckoutOrderRequest;
import com.myproject.order_service.dto.response.*;
import com.myproject.order_service.entity.Cart;
import com.myproject.order_service.entity.CartItem;
import com.myproject.order_service.entity.Order;
import com.myproject.order_service.enums.OrderStatus;
import com.myproject.order_service.exception.ForbiddenException;
import com.myproject.order_service.exception.NotFoundException;
import com.myproject.order_service.kafka.OrderProducer;
import com.myproject.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderDAO orderDAO;
    private final CartDAO cartDAO;
    private final RestaurantDAO restaurantDAO;
    private final OrderProducer orderProducer;

    @Override
    public BaseResponse<DetailedOrderResponse> getOrderDetailed(Long customerId, String role, Long orderId) {
        if(!role.equals("Customer")) {
            throw new ForbiddenException("Only customer can see detailed order");
        }
        DetailedOrderResponse orderResponse = orderDAO.getOrderById(orderId);
        if(!orderResponse.getCustomerId().equals(customerId)) {
            throw new ForbiddenException("User cannot see detail");
        }

        return new BaseResponse<>(
                true,
                "Get detailed order successfully",
                orderResponse
        );
    }

    @Override
    public BaseResponse<ScrollResponse<OrderResponse>> getOrderByCustomer(Long customerId, String role, Long cursor, int limit) {
        if(!role.equals("Customer")) {
            throw new ForbiddenException("Only customers can see their orders");
        }

        List<Order> orderList = orderDAO.getOrderByCustomerId(customerId, cursor, limit);

        boolean hasNext = orderList.size() > limit;

        if (hasNext) {
            orderList.remove(orderList.size() - 1);
        }

        Long nextCursor = orderList.isEmpty()
                ? null
                : orderList.get(orderList.size() - 1).getOrderId();

        List<OrderResponse> responseList = orderList.stream()
                .map(item -> {
                    OrderResponse res = new OrderResponse();
                    res.setOrderId(item.getOrderId());
                    res.setCustomerId(item.getCustomerId());
                    res.setRestaurantId(item.getRestaurantId());
                    res.setTotalPrice(item.getTotalPrice());
                    res.setStatus(item.getStatus());
                    res.setCreatedAt(item.getCreatedAt());
                    res.setDeliveryAddress(item.getDeliveryAddress());
                    return res;
                })
                .toList();

        ScrollResponse<OrderResponse> scrollResponse =
                new ScrollResponse<>(
                        responseList,
                        nextCursor,
                        hasNext
                );

        return new BaseResponse<>(
                true,
                "Get orders for customer successfully",
                scrollResponse
        );
    }

    @Override
    public BaseResponse<ScrollResponse<OrderResponse>> getOrderByRestaurant(Long ownerId, String role,Long cursor, int limit) {
        if(!role.equals("RestaurantOwner")) {
            throw new ForbiddenException("Only restaurant owners can see their orders");
        }

        Long restaurantId = restaurantDAO.findRestaurantId(ownerId);

        List<Order> orderList = orderDAO.getOrderByRestaurantId(restaurantId,cursor,limit);

        boolean hasNext = orderList.size() > limit;

        if (hasNext) {
            orderList.remove(orderList.size() - 1);
        }

        Long nextCursor = orderList.isEmpty()
                ? null
                : orderList.get(orderList.size() - 1).getOrderId();

        List<OrderResponse> responseList = orderList.stream()
                .map(item -> {
                    OrderResponse res = new OrderResponse();
                    res.setOrderId(item.getOrderId());
                    res.setCustomerId(item.getCustomerId());
                    res.setRestaurantId(item.getRestaurantId());
                    res.setTotalPrice(item.getTotalPrice());
                    res.setStatus(item.getStatus());
                    res.setCreatedAt(item.getCreatedAt());
                    res.setDeliveryAddress(item.getDeliveryAddress());
                    return res;
                })
                .toList();

        ScrollResponse<OrderResponse> scrollResponse =
                new ScrollResponse<>(
                        responseList,
                        nextCursor,
                        hasNext
                );

        return new BaseResponse<>(
                true,
                "Get orders for restaurant owner successfully",
                scrollResponse
        );

    }

    @Override
    public BaseResponse<Void> createNewOrder(Long customerId,String role, CheckoutOrderRequest request) {
        if(!role.equals("Customer")) {
            throw new ForbiddenException("Only customers can checkout their orders");
        }

        Long restaurantId = request.getRestaurantId();

        CartResponse cart = cartDAO.getDetailedCartbyRestaurant(customerId,restaurantId);
        Double totalPrice = cart.getTotalPrice();

        if(cart == null) {
            throw new NotFoundException("Customer doesn't have any cart in this restaurant");
        }

        Long orderId = orderDAO.createNewOrder(customerId,request,totalPrice);

        List<CartItemResponse> cartItemList = cart.getItems();

        for (CartItemResponse cartItem : cartItemList) {
            orderDAO.createOrderItem(orderId,cartItem);
        }

        cartDAO.deleteCart(cart.getCartId());

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(orderId)
                .customerId(customerId)
                .totalAmount(totalPrice)
                .build();

        orderProducer.publishOrderCreated(event);

        return new BaseResponse<>(
                true,
                "checkout cart successfully",
                null
        );
    }

    @Override
    public void updateOrderStatusPaid(Long orderId) {
        String status = OrderStatus.PAID.toString();
        orderDAO.updateOrderStatus(orderId, status);
    }

    @Override
    public void updateOrderStatusFailed(Long orderId) {
        String status = OrderStatus.FAILED.toString();
        orderDAO.updateOrderStatus(orderId, status);
    }
}
