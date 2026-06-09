package com.myproject.restaurant_service.service.impl;

import com.myproject.restaurant_service.dao.RestaurantDAO;
import com.myproject.restaurant_service.dto.kafka.RestaurantCreatedEvent;
import com.myproject.restaurant_service.dto.kafka.RestaurantOwnerCreatedEvent;
import com.myproject.restaurant_service.dto.request.RestaurantUpdateDTO;
import com.myproject.restaurant_service.dto.response.BaseResponse;
import com.myproject.restaurant_service.dto.response.DetailedRestaurantResponse;
import com.myproject.restaurant_service.dto.response.RestaurantResponse;
import com.myproject.restaurant_service.dto.response.ScrollResponse;
import com.myproject.restaurant_service.entity.Restaurant;
import com.myproject.restaurant_service.exception.ForbiddenException;
import com.myproject.restaurant_service.exception.NotFoundException;
import com.myproject.restaurant_service.kafka.RestaurantProducer;
import com.myproject.restaurant_service.service.RestaurantService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {
    private final RestaurantDAO restaurantDAO;
    private final RestaurantProducer restaurantProducer;

    @Override
    @Transactional
    public void createRestaurant(
            RestaurantOwnerCreatedEvent event
    ) {

        Restaurant restaurant = new Restaurant();

        restaurant.setOwnerId(event.getOwnerId());
        restaurant.setRestaurantName("New Restaurant");
        restaurant.setRestaurantAddress("New address");
        restaurant.setRestaurantPhone("New Phone");

        Long restaurantId =
                restaurantDAO.saveRestaurant(restaurant);

        restaurantProducer.publishRestaurantCreated(
                new RestaurantCreatedEvent(
                        restaurantId,
                        restaurant.getOwnerId()
                )
        );
    }


    @Override
    public BaseResponse<ScrollResponse<RestaurantResponse>> getAllRestaurant(Long cursor, int limit) {

        List<Restaurant> restaurantList =
                restaurantDAO.getAllRestaurants(cursor, limit + 1);

        boolean hasNext = restaurantList.size() > limit;

        if (hasNext) {
            restaurantList.remove(restaurantList.size() - 1);
        }

        Long nextCursor = restaurantList.isEmpty()
                ? null
                : restaurantList.get(restaurantList.size() - 1).getRestaurantId();

        List<RestaurantResponse> responseList = restaurantList.stream()
                .map(r -> {
                    RestaurantResponse res = new RestaurantResponse();
                    res.setRestaurantId(r.getRestaurantId());
                    res.setRestaurantName(r.getRestaurantName());
                    return res;
                })
                .toList();

        ScrollResponse<RestaurantResponse> scrollResponse =
                new ScrollResponse<>(
                        responseList,
                        nextCursor,
                        hasNext
                );

        return new BaseResponse<>(
                true,
                "Get restaurants successfully",
                scrollResponse
        );
    }

    @Override
    public BaseResponse<Void> updateRestaurantInfo(RestaurantUpdateDTO request, Long ownerId, Long restaurantId) {
        Restaurant findRestaurant = restaurantDAO.findById(restaurantId);

        if(findRestaurant == null) {
            throw new NotFoundException("Restaurant not found");
        }

        if(!findRestaurant.getOwnerId().equals(ownerId)) {
            throw new ForbiddenException("User is not owner of this restaurant");
        }

        restaurantDAO.updateRestaurant(request, restaurantId);

        return new BaseResponse<>(
                true,
                "Restaurant updated successfully",
                null
        );
    }

    @Override
    public BaseResponse<DetailedRestaurantResponse> getDetailedRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantDAO.findById(restaurantId);
        if(restaurant == null) {
            throw new NotFoundException("Restaurant not found");
        }
        DetailedRestaurantResponse response = DetailedRestaurantResponse.builder()
                .restaurantId(restaurantId)
                .restaurantName(restaurant.getRestaurantName())
                .restaurantAddress(restaurant.getRestaurantAddress())
                .restaurantPhone(restaurant.getRestaurantPhone())
                .build();
        return new BaseResponse<>(
                true,
                "Get detailed restaurant's information",
                response
        );
    }
}
