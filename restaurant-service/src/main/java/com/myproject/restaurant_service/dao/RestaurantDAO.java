package com.myproject.restaurant_service.dao;

import com.myproject.restaurant_service.dto.request.RestaurantUpdateDTO;
import com.myproject.restaurant_service.entity.Restaurant;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RestaurantDAO {

    private final SessionFactory sessionFactory;

    public Restaurant findById(Long id) {
        try(Session session = sessionFactory.openSession()) {
            String sql = "SELECT * " +
                    "FROM restaurants " +
                    "WHERE restaurant_id = ?";

            Restaurant res = session.createNativeQuery(sql, Restaurant.class)
                    .setParameter(1,id)
                    .uniqueResult();
            return res;
        }
        catch (Exception e) {
            throw new RuntimeException("#findById " + e.getMessage());
        }
    }

    public List<Restaurant> getAllRestaurants(Long cursor, int limit) {
        try(Session session = sessionFactory.openSession()) {
            String sql = "SELECT * FROM restaurants" +
                    " WHERE restaurant_id > COALESCE(?, 0) " +
                    "ORDER BY restaurant_id ASC " +
                    "LIMIT ?";
            List<Restaurant> resutl = session.createNativeQuery(sql, Restaurant.class)
                    .setParameter(1,cursor)
                    .setParameter(2,limit)
                    .getResultList();

            return resutl;
        } catch (Exception e) {
            throw new RuntimeException("#getAllRestaurants "  +e.getMessage());
        }
    }

    public Long saveRestaurant(Restaurant restaurant) {
        try(Session session = sessionFactory.openSession()){
            Transaction tx = session.beginTransaction();
            // Use positional parameter for native query to avoid named-parameter issues
            String sql = """
                INSERT INTO restaurants(
                    restaurant_name,
                    owner_id,
                    restaurant_address,
                    restaurant_phone
                )
                VALUES (?,?,?,?)
                RETURNING restaurant_id
                """;
            Long restaurantId = ((Number) session.createNativeQuery(sql)
                    .setParameter(1, restaurant.getRestaurantName())
                    .setParameter(2, restaurant.getOwnerId())
                    .setParameter(3, restaurant.getRestaurantAddress())
                    .setParameter(4, restaurant.getRestaurantPhone())
                    .getSingleResult())
                    .longValue();

            tx.commit();
            return restaurantId;
        }
        catch (Exception e) {
            throw new RuntimeException("#saveRestaurant " + e.getMessage());
        }
    }

    public void updateRestaurant(RestaurantUpdateDTO restaurantUpdateDTO, Long restaurantId) {
        try(Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            List<Object> params = new ArrayList<>();
            StringBuilder sql = new StringBuilder("UPDATE restaurants SET");
            if(restaurantUpdateDTO.getRestaurantName() != null){
                sql.append(" restaurant_name = ?, ");
                params.add(restaurantUpdateDTO.getRestaurantName());
            }
            if(restaurantUpdateDTO.getRestaurantAddress() != null) {
                sql.append(" restaurant_address = ?, ");
                params.add(restaurantUpdateDTO.getRestaurantAddress());
            }
            if(restaurantUpdateDTO.getRestaurantPhone() != null) {
                sql.append(" restaurant_phone = ?, ");
                params.add(restaurantUpdateDTO.getRestaurantPhone());
            }

            sql.setLength(sql.length() - 2);

            sql.append(" WHERE restaurant_id = ?");
            params.add(restaurantId);
            var query = session.createNativeQuery(sql.toString());

            for(int i = 0; i < params.size(); i++) {
                query.setParameter(i + 1, params.get(i));
            }

            query.executeUpdate();
            tx.commit();
        }
        catch (Exception e) {
            throw new RuntimeException("#updateRestaurant " + e.getMessage());
        }
    }
}
