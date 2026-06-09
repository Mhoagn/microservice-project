package com.myproject.auth_service.dao;

import com.myproject.auth_service.entity.User;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserDao {
    private final SessionFactory sessionFactory;

    public User findByEmail(String email) {
        try(Session session = sessionFactory.openSession()) {
            String sql = "SELECT * FROM users " +
                    "WHERE email = ? ";

            User findUser = session.createNativeQuery(sql, User.class)
                    .setParameter(1, email)
                    .uniqueResult();
            return findUser;
        }
        catch (Exception e) {
            throw new RuntimeException("#findByEmail" + e.getMessage());
        }
    }

    public User findById(Long id) {
        try(Session session = sessionFactory.openSession()) {
            String sql = "SELECT * FROM users" +
                    " WHERE userid = ?";

            User findUser = session.createNativeQuery(sql, User.class)
                    .setParameter(1,id)
                    .uniqueResult();
            return findUser;
        }
        catch (Exception e) {
            throw new RuntimeException("#findById " + e.getMessage());
        }
    }

    public void saveUser(User newUser) {
        try (Session session = sessionFactory.openSession()) {

            Transaction tx = session.beginTransaction();

            String sql = """
            INSERT INTO users(email, password, role)
            VALUES (?, ?, ?)
            RETURNING userid
        """;

            Object result = session.createNativeQuery(sql)
                    .setParameter(1, newUser.getEmail())
                    .setParameter(2, newUser.getPassword())
                    .setParameter(3, newUser.getUserRole().name())
                    .getSingleResult();

            Long generatedId = ((Number) result).longValue();

            newUser.setUserId(generatedId);

            tx.commit();

        } catch (Exception e) {
            throw new RuntimeException("#saveUser " + e.getMessage());
        }
    }
}
