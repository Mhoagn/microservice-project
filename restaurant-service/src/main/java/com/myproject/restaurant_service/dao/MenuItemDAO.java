package com.myproject.restaurant_service.dao;

import com.myproject.restaurant_service.dto.request.MenuItemDTO;
import com.myproject.restaurant_service.entity.MenuItem;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MenuItemDAO {
    private final SessionFactory sessionFactory;

    public MenuItem findById(Long menuItemId) {
        try (Session session = sessionFactory.openSession()) {
            String sql = "SELECT * FROM menuitems " +
                    "WHERE item_id = ?";

            MenuItem res = session.createNativeQuery(sql, MenuItem.class)
                    .setParameter(1,menuItemId)
                    .uniqueResult();

            return res;
        }
    }

    public List<MenuItem> getMenuItemByRestaurant(Long restaurantId, Long cursor, int limit) {
        try(Session session = sessionFactory.openSession()) {
            String sql = "SELECT * " +
                    "FROM menuitems m " +
                    "WHERE m.restaurant_id = ? " +
                    "AND m.item_id > ? " +
                    "Limit ?";

            List<MenuItem> res = session.createNativeQuery(sql, MenuItem.class)
                    .setParameter(1, restaurantId)
                    .setParameter(2,cursor)
                    .setParameter(3,limit)
                    .getResultList();

            return res;
        }
        catch (Exception e) {
            throw new RuntimeException("#getMenuItemByRestaurant");
        }
    }

    public MenuItem getMenuItemById(Long menuItemId) {
        try(Session session = sessionFactory.openSession()) {
            String sql = "SELECT * FROM menuitems " +
                    "WHERE item_id = ?";

            MenuItem item = session.createNativeQuery(sql, MenuItem.class)
                    .setParameter(1,menuItemId)
                    .uniqueResult();

            return item;
        }
        catch (Exception e) {
            throw new RuntimeException("#getMenuItemById " + e.getMessage());
        }
    }

    public void addNewMenuItem(Long restaurantId, MenuItemDTO menuItemDTO) {

        Transaction tx = null;

        try(Session session = sessionFactory.openSession()) {

            tx = session.beginTransaction();

            String sql = """
            INSERT INTO menuitems(
                item_name,
                item_price,
                item_image_url,
                restaurant_id
            )
            VALUES(?,?,?,?)
            """;

            var query = session.createNativeQuery(sql);

            query.setParameter(
                    1,
                    menuItemDTO.getItem_name() != null
                            ? menuItemDTO.getItem_name()
                            : ""
            );

            query.setParameter(
                    2,
                    menuItemDTO.getItem_price() != null
                            ? menuItemDTO.getItem_price()
                            : 0.0
            );

            query.setParameter(
                    3,
                    menuItemDTO.getItemImageURL() != null
                            ? menuItemDTO.getItemImageURL()
                            : ""
            );

            query.setParameter(4, restaurantId);

            query.executeUpdate();

            tx.commit();

        } catch (Exception e) {

            if(tx != null) {
                tx.rollback();
            }

            throw new RuntimeException("#addNewMenuItem " + e.getMessage());
        }
    }

    public void updateMenuItem(Long menuItemId, MenuItemDTO request) {
        Transaction tx = null;

        try(Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            StringBuilder sql = new StringBuilder("UPDATE menuitems SET ");
            List<Object> params = new ArrayList<>();

            if(request.getItem_name() != null) {
                sql.append("item_name = ?, ");
                params.add(request.getItem_name());
            }

            if(request.getItem_price() != null) {
                sql.append("item_price = ?, ");
                params.add(request.getItem_price());
            }

            if(request.getItemImageURL() != null) {
                sql.append("item_image_url = ?, ");
                params.add(request.getItemImageURL());
            }

            sql.setLength(sql.length() - 2);
            sql.append(" WHERE item_id = ?");
            params.add(menuItemId);

            var query = session.createNativeQuery(sql.toString());

            for(int i = 0; i < params.size(); i++) {
                query.setParameter(i+1,params.get(i));
            }

            query.executeUpdate();
            tx.commit();
        }
        catch (Exception e) {
            if(tx != null) tx.rollback();

            throw new RuntimeException("#updateMenuItem " + e.getMessage());
        }
    }

    public void deleteMenuItemById(Long menuItemId){
        Transaction tx = null;

        try(Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            String sql = "DELETE FROM menuitems " +
                    "WHERE item_id = ?";

            session.createNativeQuery(sql)
                    .setParameter(1,menuItemId)
                    .executeUpdate();

            tx.commit();
        }
        catch (Exception e) {
            if(tx != null) tx.rollback();

            throw new RuntimeException("#deleteMenuItemById " + e.getMessage());
        }
    }
}
