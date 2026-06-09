package com.myproject.order_service.dao;

import com.myproject.order_service.dto.request.AddCartItemRequest;
import com.myproject.order_service.dto.response.CartItemResponse;
import com.myproject.order_service.dto.response.CartResponse;
import com.myproject.order_service.entity.Cart;
import com.myproject.order_service.entity.CartItem;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CartDAO {
    private final SessionFactory sessionFactory;

    public Cart findByCustomerAndRestaurant(Long customerId, Long restaurantId) {
        try (Session session = sessionFactory.openSession()) {
            String sql = "SELECT * FROM carts " +
                    "WHERE customer_id = ? " +
                    "AND restaurant_id = ?";

            Cart cart = (Cart) session.createNativeQuery(sql)
                    .addEntity(Cart.class)
                    .setParameter(1, customerId)
                    .setParameter(2, restaurantId)
                    .uniqueResult();
            return cart;
        }
        catch (Exception e) {
            throw new RuntimeException("#findByCustomerAndRestaurant " + e.getMessage());
        }
    }

    public Long createNewCart(Long customerId, Long restaurantId) {

        Transaction tx = null;

        try (Session session = sessionFactory.openSession()) {

            tx = session.beginTransaction();

            String sql = """
                INSERT INTO carts (customer_id, restaurant_id)
                VALUES (:customerId, :restaurantId)
                RETURNING cart_id
                """;

            Object result = session.createNativeQuery(sql)
                    .setParameter("customerId", customerId)
                    .setParameter("restaurantId", restaurantId)
                    .getSingleResult();

            Long generatedId = ((Number) result).longValue();

            tx.commit();

            return generatedId;
        }
        catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new RuntimeException("#createNewCart " + e.getMessage());
        }
    }

    public void addItemToCart(Long customerId, Long cartId,AddCartItemRequest request,String item_name, Double item_price) {
        Transaction tx = null;

        try(Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            String sql = "INSERT INTO cartitems (cart_id,item_id ,item_name, item_price,quantity) " +
                    "VALUES (?,?,?,?,?)";
            session.createNativeQuery(sql)
                            .setParameter(1,cartId)
                                    .setParameter(2,request.getItemId())
                                            .setParameter(3,item_name)
                                                    .setParameter(4,item_price, StandardBasicTypes.DOUBLE)
                                                            .setParameter(5,request.getQuantity())
                                                                    .executeUpdate();
            tx.commit();
        }
        catch (Exception e) {
            throw new RuntimeException("#addItemToCart "+ e.getMessage());
        }
    }

    public List<CartItem> findAllCartItem(Long cartId) {
        try(Session session = sessionFactory.openSession()) {
            String sql = """
                SELECT
                    cart_item_id,
                    cart_id,
                    item_id,
                    item_name,
                    item_price,
                    quantity
                FROM cartitems
                WHERE cart_id = ?
                """;
            List<CartItem> cartItemList = session.createNativeQuery(sql, CartItem.class)
                    .setParameter(1,cartId)
                    .getResultList();

            return cartItemList;
        }
        catch (Exception e) {
            throw new RuntimeException("#findAllCartItem " + e.getMessage());
        }
    }

    public CartItem findCartItem(Long cartId, Long itemId) {

        try (Session session = sessionFactory.openSession()) {

            String sql = """
                SELECT *
                FROM cartitems
                WHERE cart_id = ?
                AND item_id = ?
                """;

            CartItem item = (CartItem) session.createNativeQuery(sql)
                    .addEntity(CartItem.class)
                    .setParameter(1, cartId)
                    .setParameter(2, itemId)
                    .uniqueResult();
            return item;
        }
        catch (Exception e) {
            throw new RuntimeException(
                    "#findCartItem " + e.getMessage()
            );
        }
    }

    public void updateQuantity(Long cartItemId, Integer newQuantity) {

        Transaction tx = null;

        try (Session session = sessionFactory.openSession()) {

            tx = session.beginTransaction();

            String sql = """
                UPDATE cartitems
                SET quantity = ?
                WHERE cart_item_id = ?
                """;

            session.createNativeQuery(sql)
                    .setParameter(1, newQuantity)
                    .setParameter(2, cartItemId)
                    .executeUpdate();

            tx.commit();
        }
        catch (Exception e) {

            if (tx != null) {
                tx.rollback();
            }
            throw new RuntimeException("#updateQuantity " + e.getMessage()
            );
        }
    }

    public CartResponse getDetailedCartbyRestaurant(Long customerId, Long restaurantId) {

        try (Session session = sessionFactory.openSession()) {

            String sql = """
                SELECT
                    c.cart_id,
                    c.customer_id,
                    c.restaurant_id,
                    c.createdat,

                    ci.cart_item_id,
                    ci.item_id,
                    ci.item_name,
                    ci.item_price,
                    ci.quantity

                FROM carts c
                LEFT JOIN cartitems ci
                    ON ci.cart_id = c.cart_id

                WHERE c.restaurant_id = :restaurantId
                AND c.customer_id = :customerId
                """;

            List<Object[]> rows =
                    session.createNativeQuery(sql)
                            .setParameter("restaurantId", restaurantId)
                            .setParameter("customerId",customerId)
                            .list();

            if (rows.isEmpty()) {
                return null;
            }

            CartResponse response = new CartResponse();

            List<CartItemResponse> items = new ArrayList<>();

            double totalPrice = 0;

            for (Object[] row : rows) {

                // set cart info once
                if (response.getCartId() == null) {

                    response.setCartId(((Number) row[0]).longValue());

                    response.setCustomerId(((Number) row[1]).longValue());

                    response.setRestaurantId(((Number) row[2]).longValue());

                    response.setCreatedAt((Timestamp) row[3]);
                }

                // no item
                if (row[4] == null) {
                    continue;
                }

                CartItemResponse item = new CartItemResponse();

                item.setCartItemId(((Number) row[4]).longValue());

                item.setItemId(((Number) row[5]).longValue());

                item.setItemName((String) row[6]);

                double itemPrice = ((Number) row[7]).doubleValue();

                item.setItemPrice(itemPrice);

                int quantity = ((Number) row[8]).intValue();

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

            throw new RuntimeException(
                    "#getDetailedCartbyRestaurant "
                            + e.getMessage()
            );
        }
    }

    public void deleteCart(Long cartId) {
        Transaction tx = null;
        try(Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            String sql = "DELETE FROM carts " +
                    "WHERE cart_id = ?";

            session.createNativeQuery(sql)
                    .setParameter(1,cartId)
                    .executeUpdate();

            tx.commit();
        }
        catch(Exception e) {
            if(tx != null) tx.rollback();
            throw new RuntimeException("#deleteCart "+ e.getMessage());
        }
    }

    public Integer getCartQuantity(Long cartId) {
        try(Session session = sessionFactory.openSession()) {
            String sql = """
                    SELECT COALESCE(SUM(quantity), 0)
                                    FROM cartitems
                                    WHERE cart_id = ? 
                    """;

            Number result = (Number) session.createNativeQuery(sql)
                    .setParameter(1,cartId)
                    .uniqueResult();

            return result.intValue();
        }
        catch (Exception e) {
            throw new RuntimeException("#getCartQuantity " + e.getMessage());
        }
    }
}
