package com.myproject.payment_service.dao;

import com.myproject.payment_service.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentDAO {
    private final SessionFactory sessionFactory;

    public void savePayment(Payment payment) {
        Transaction tx = null;
        try(Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            String sql = """
            INSERT INTO payments(
                order_id,
                customer_id,
                amount,
                payment_method,
                status,
                vnp_txn_ref,
                created_at,
                updated_at
            )
            VALUES(?,?,?,?,?,?,?,?)
            """;

            session.createNativeQuery(sql)
                    .setParameter(1,payment.getOrderId())
                    .setParameter(2,payment.getCustomerId())
                    .setParameter(3,payment.getAmount())
                    .setParameter(4,payment.getPaymentMethod())
                    .setParameter(5,payment.getStatus())
                    .setParameter(6,payment.getVnpTxnRef())
                    .setParameter(7,payment.getCreatedAt())
                    .setParameter(8,payment.getUpdatedAt())
                    .executeUpdate();

            tx.commit();
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            throw new RuntimeException("#savePayment " + e.getMessage());
        }
    }

    public Payment findByTxnRef(String txnRef) {
        try(Session session = sessionFactory.openSession()) {
            String sql = "SELECT * FROM payments " +
                    "WHERE vnp_txn_ref = ?";

            Payment res = session.createNativeQuery(sql, Payment.class)
                    .setParameter(1,txnRef)
                    .uniqueResult();

            return res;
        }

        catch (Exception e) {
            throw new RuntimeException("#findByTxnRef " + e.getMessage());
        }
    }

    public void updateSuccess(String txnRef, String transactionNo, String bankCode) {
        Transaction tx = null;
        try(Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            String sql = """
                UPDATE payments
                SET
                    status = 'SUCCESS',
                    vnp_transaction_no = :transactionNo,
                    bank_code = :bankCode,
                    paid_at = now(),
                    updated_at = now()
                WHERE vnp_txn_ref = :txnRef
                """;

            session.createNativeQuery(sql)
                    .setParameter("transactionNo", transactionNo)
                    .setParameter("bankCode", bankCode)
                    .setParameter("txnRef", txnRef)
                    .executeUpdate();
            tx.commit();
        }
        catch(Exception e) {
            throw new RuntimeException("#updateSuccess " + e.getMessage());
        }
    }

    public void updateFailed(String txnRef) {
        Transaction tx = null;
        try(Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            String sql = """
                UPDATE payments
                SET
                    status = 'FAILED',
                    updated_at = now()
                WHERE vnp_txn_ref = :txnRef
                """;

            session.createNativeQuery(sql)
                    .setParameter("txnRef",txnRef)
                    .executeUpdate();

            tx.commit();
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            throw new RuntimeException("#updateFailed " + e.getMessage());
        }
    }
}

