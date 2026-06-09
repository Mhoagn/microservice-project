package com.myproject.order_service.dao;

import com.myproject.order_service.dto.kafka.RestaurantCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RestaurantDAO {
    private final SessionFactory sessionFactory;

    public void createRestaurant(RestaurantCreatedEvent event) {
        Transaction tx = null;
        try(Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            String sql = "INSERT INTO restaurant_projection(restaurant_id, owner_id) " +
                    "VALUES(?,?)";
            session.createNativeQuery(sql)
                    .setParameter(1,event.getRestaurantId())
                    .setParameter(2,event.getOwnerId())
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            throw new RuntimeException("#createRestaurant " + e.getMessage());
        }
    }

    public Long findRestaurantId(Long ownerId) {
        try (Session session = sessionFactory.openSession()) {

            String sql = """
                SELECT restaurant_id
                FROM restaurant_projection
                WHERE owner_id = ?
                """;

            Object result = session.createNativeQuery(sql)
                    .setParameter(1, ownerId)
                    .uniqueResult();

            return result != null
                    ? ((Number) result).longValue()
                    : null;

        } catch (Exception e) {
            throw new RuntimeException(
                    "#findRestaurantId " + e.getMessage()
            );
        }
    }
}
