package com.myproject.notification_service.dao;

import com.myproject.notification_service.dto.request.NewNotificationDTO;
import com.myproject.notification_service.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificationDAO {
    private final SessionFactory sessionFactory;

    public List<Notification> getNotificationByCustomerId(Long customerId, Long cursor, int limit) {
        try(Session session = sessionFactory.openSession()) {
            String sql = "SELECT notification_id, " +
                    "customer_id, " +
                    "notification_type, " +
                    "title, " +
                    "message, " +
                    "created_at " +
                    "FROM notifications " +
                    "WHERE customer_id = ? " +
                    "AND notification_id > ? " +
                    "LIMIT ?";

            List<Notification> notificationList = session.createNativeQuery(sql, Notification.class)
                    .setParameter(1,customerId)
                    .setParameter(2,cursor)
                    .setParameter(3,limit)
                    .getResultList();

            return notificationList;
        }
        catch (Exception e) {
            throw new RuntimeException("#getNotificationByCustomerId " + e.getMessage());
        }
    }

    public void createNewNotification(NewNotificationDTO dto) {
        Transaction tx = null;

        try(Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            String sql = "INSERT INTO notifications(customer_id, notification_type, title, message) " +
                    "VALUES (?,?,?,?)";

            session.createNativeQuery(sql)
                    .setParameter(1,dto.getCustomerId())
                    .setParameter(2,dto.getNotificationType())
                    .setParameter(3,dto.getTitle())
                    .setParameter(4,dto.getMessage())
                    .executeUpdate();

            tx.commit();
        }
        catch (Exception e) {
            throw new RuntimeException("#createNewNotification " + e.getMessage());
        }
    }
}
