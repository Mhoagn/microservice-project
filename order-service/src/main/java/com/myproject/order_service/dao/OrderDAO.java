package com.myproject.order_service.dao;

import com.myproject.order_service.dto.request.CheckoutOrderRequest;
import com.myproject.order_service.dto.response.CartItemResponse;
import com.myproject.order_service.dto.response.OrderItemResponse;
import com.myproject.order_service.dto.response.DetailedOrderResponse;
import com.myproject.order_service.entity.CartItem;
import com.myproject.order_service.entity.Order;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
@RequiredArgsConstructor
public class OrderDAO {
    private final SessionFactory sessionFactory;

    public DetailedOrderResponse getOrderById(Long orderId) {

        try(Session session = sessionFactory.openSession()) {

            String sql = """
            SELECT
                o.order_id,
                o.customer_id,
                o.restaurant_id,
                o.status,
                o.created_at,

                oi.order_item_id,
                oi.item_id,
                oi.item_name,
                oi.item_price,
                oi.quantity

            FROM orders o

            LEFT JOIN order_items oi
                ON oi.order_id = o.order_id

            WHERE o.order_id = :orderId
            """;

            List<Object[]> rows =
                    session.createNativeQuery(sql)
                            .setParameter("orderId", orderId)
                            .list();

            if(rows.isEmpty()) {
                return null;
            }

            Double totalPrice = 0.0;

            DetailedOrderResponse response =
                    new DetailedOrderResponse();

            List<OrderItemResponse> items =
                    new ArrayList<>();

            for(Object[] row : rows) {

                if(response.getOrderId() == null) {

                    response.setOrderId(((Number) row[0]).longValue());

                    response.setCustomerId(((Number) row[1]).longValue());

                    response.setRestaurantId(((Number) row[2]).longValue());

                    response.setStatus(row[3].toString());

                    response.setCreatedAt((Timestamp) row[4]);
                }

                // no order items
                if (row[5] == null) {
                    continue;
                }

                OrderItemResponse item =
                        new OrderItemResponse();

                item.setOrderItemId(((Number) row[5]).longValue());

                item.setItemId(((Number) row[6]).longValue());

                item.setItemName((String) row[7]);

                double itemPrice = ((Number) row[8]).doubleValue();

                item.setItemPrice(itemPrice);

                int quantity = ((Number) row[9]).intValue();

                item.setQuantity(quantity);

                double subtotal = itemPrice * quantity;

                item.setSubtotal(subtotal);

                totalPrice += subtotal;

                items.add(item);
            }

            response.setItems(items);

            response.setTotalPrice(totalPrice);

            return response;
        }
        catch (Exception e) {
            throw new RuntimeException("#getOrderById " + e.getMessage());
        }
    }

    public List<Order> getOrderByCustomerId(Long customerId, Long cursor, int limit) {
        try(Session session = sessionFactory.openSession()) {
            String sql = "SELECT o.order_id, " +
                    "o.customer_id, " +
                    "o.restaurant_id, " +
                    "o.total_price, " +
                    "o.status, " +
                    "o.created_at, " +
                    "o.delivery_address " +
                    "FROM orders o " +
                    "WHERE o.customer_id = ? " +
                    "AND o.order_id > ? " +
                    "LIMIT ?";

            List<Order> orders = session.createNativeQuery(sql, Order.class)
                    .setParameter(1,customerId)
                    .setParameter(2,cursor)
                    .setParameter(3,limit)
                    .getResultList();

            return orders;
        } catch (Exception e) {
            throw new RuntimeException("#getOrderByCustomerId " + e.getMessage());
        }
    }

    public List<Order> getOrderByRestaurantId(Long restaurantId,Long cursor, int limit) {
        try(Session session = sessionFactory.openSession()) {
            String sql = "SELECT o.order_id, " +
                    "o.customer_id, " +
                    "o.restaurant_id, " +
                    "o.total_price, " +
                    "o.status, " +
                    "o.created_at, " +
                    "o.delivery_address " +
                    "FROM orders o " +
                    "WHERE o.restaurant_id = ? " +
                    "AND o.order_id > ? " +
                    "LIMIT ?";

            List<Order> orders = session.createNativeQuery(sql, Order.class)
                    .setParameter(1,restaurantId)
                    .setParameter(2,cursor)
                    .setParameter(3,limit)
                    .getResultList();

            return orders;
        } catch (Exception e) {
            throw new RuntimeException("#getOrderByRestaurantId " + e.getMessage());
        }
    }

    public Long createNewOrder(Long customerId, CheckoutOrderRequest request, Double totalPrice) {
        Transaction tx = null;
        try(Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            String sql = "INSERT INTO orders(customer_id, restaurant_id, total_price, delivery_address,status) " +
                    "VALUES(?,?,?,?,?) " +
                    "RETURNING order_id";
            Object res = session.createNativeQuery(sql)
                    .setParameter(1,customerId)
                    .setParameter(2,request.getRestaurantId())
                    .setParameter(3,totalPrice)
                    .setParameter(4,request.getDeliveryAddress())
                    .setParameter(5,"PENDING")
                    .getSingleResult();
            Long orderId = ((Number) res).longValue();

            tx.commit();

            return orderId;
        }
        catch (Exception e) {
            if(tx != null) tx.rollback();
            throw new RuntimeException("#createNewOrder " + e.getMessage());
        }
    }

    public void createOrderItem(Long orderId, CartItemResponse cartItem) {

        Transaction tx = null;

        try (Session session = sessionFactory.openSession()) {

            tx = session.beginTransaction();

            String sql = """
            INSERT INTO order_items
            (
                order_id,
                item_id,
                item_name,
                item_price,
                quantity
            )
            VALUES
            (
                :orderId,
                :itemId,
                :itemName,
                :itemPrice,
                :quantity
            )
            """;

            session.createNativeQuery(sql)
                    .setParameter("orderId", orderId)
                    .setParameter("itemId", cartItem.getItemId())
                    .setParameter("itemName", cartItem.getItemName())
                    .setParameter("itemPrice", cartItem.getItemPrice())
                    .setParameter("quantity", cartItem.getQuantity())
                    .executeUpdate();

            tx.commit();

        } catch (Exception e) {

            if (tx != null) {
                tx.rollback();
            }

            throw new RuntimeException(
                    "#createOrderItem " + e.getMessage()
            );
        }
    }

    public void updateOrderStatus(Long orderId,String status) {
        Transaction tx = null;
        try(Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            String sql = """
                UPDATE orders
                SET status = ?
                WHERE order_id = ?
                """;

            session.createNativeQuery(sql)
                    .setParameter(1, status)
                    .setParameter(2, orderId)
                    .executeUpdate();

            tx.commit();
        }
        catch (Exception e) {
            if(tx != null) tx.rollback();

            throw new RuntimeException("#updateOrderStatus " + e.getMessage());
        }
    }
}
