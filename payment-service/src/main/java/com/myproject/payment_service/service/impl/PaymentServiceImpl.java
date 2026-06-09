package com.myproject.payment_service.service.impl;

import com.myproject.payment_service.config.VNPayProperties;
import com.myproject.payment_service.dao.PaymentDAO;
import com.myproject.payment_service.dto.kafka.PaymentFailedEvent;
import com.myproject.payment_service.dto.kafka.PaymentSucceededEvent;
import com.myproject.payment_service.dto.response.BaseResponse;
import com.myproject.payment_service.entity.Payment;
import com.myproject.payment_service.kafka.PaymentProducer;
import com.myproject.payment_service.service.PaymentService;
import com.myproject.payment_service.util.VNPayUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentDAO paymentDAO;

    private final VNPayProperties vnPayProperties;

    private final PaymentProducer paymentProducer;

    @Override
    public BaseResponse<String> createPayment(Long orderId, Long customerId, Double amount) {

        // 1. Sinh mã giao dịch duy nhất
        String txnRef = UUID.randomUUID()
                .toString()
                .replace("-", "");

        // 2. Lưu payment vào database
        Payment payment = new Payment();

        payment.setOrderId(orderId);
        payment.setCustomerId(customerId);
        payment.setAmount(amount);

        payment.setPaymentMethod("VNPAY");

        payment.setStatus("PENDING");

        payment.setVnpTxnRef(txnRef);

        payment.setCreatedAt(LocalDateTime.now());

        payment.setUpdatedAt(LocalDateTime.now());

        paymentDAO.savePayment(payment);

        // 3. Tạo danh sách tham số gửi sang VNPay
        Map<String, String> params = new TreeMap<>();

        params.put("vnp_Version", "2.1.0");

        params.put("vnp_Command", "pay");

        params.put("vnp_TmnCode", vnPayProperties.getTmnCode());

        params.put("vnp_Amount", String.valueOf(Math.round(amount * 100)));
        params.put("vnp_CurrCode", "VND");

        params.put("vnp_TxnRef", txnRef);

        params.put("vnp_OrderInfo",
                "Thanh toan don hang " + orderId);

        params.put("vnp_OrderType", "other");

        params.put("vnp_Locale", "vn");

        params.put("vnp_ReturnUrl",
                vnPayProperties.getReturnUrl());

        params.put("vnp_IpAddr", "127.0.0.1");

        String createDate = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        params.put("vnp_CreateDate", createDate);

        // 4. Build hashData và queryString
        StringBuilder hashData = new StringBuilder();

        StringBuilder query = new StringBuilder();

        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();

        while (iterator.hasNext()) {

            Map.Entry<String, String> entry = iterator.next();

            String key = entry.getKey();

            String value = entry.getValue();

            try {

                String encodedValue =
                        URLEncoder.encode(value, StandardCharsets.UTF_8.toString());

                hashData.append(key)
                        .append("=")
                        .append(encodedValue);

                query.append(key)
                        .append("=")
                        .append(encodedValue);

                if (iterator.hasNext()) {

                    hashData.append("&");

                    query.append("&");
                }

            } catch (UnsupportedEncodingException ex) {

                throw new RuntimeException(
                        "Encode VNPay param failed",
                        ex
                );
            }
        }

        // 5. Sinh chữ ký
        String secureHash =
                VNPayUtil.hmacSHA512(vnPayProperties.getHashSecret(), hashData.toString());

        // 6. Tạo URL thanh toán
        String paymentUrl =
                vnPayProperties.getPayUrl()
                        + "?"
                        + query
                        + "&vnp_SecureHash="
                        + secureHash;

        return new BaseResponse<>(
                true,
                "Creating payment successfully",
                paymentUrl
        );
    }


    @Override
    public void handleIpn(
            HttpServletRequest request
    ) {

        boolean validSignature = VNPayUtil.verifySignature(request, vnPayProperties.getHashSecret());

        if (!validSignature) {
            throw new RuntimeException("Invalid VNPay signature");
        }

        String txnRef = request.getParameter("vnp_TxnRef");

        String responseCode = request.getParameter("vnp_ResponseCode");

        String transactionNo = request.getParameter("vnp_TransactionNo");

        String bankCode = request.getParameter("vnp_BankCode");

        if (txnRef == null || txnRef.isBlank()) {
            throw new RuntimeException("Invalid txnRef");
        }

        Payment payment = paymentDAO.findByTxnRef(txnRef);

        if (payment == null) {
            throw new RuntimeException("Payment not found");
        }

        // Chống xử lý nhiều lần
        if ("SUCCESS".equals(payment.getStatus())) {
            return;
        }

        if ("00".equals(responseCode)) {

            paymentDAO.updateSuccess(
                    txnRef,
                    transactionNo,
                    bankCode
            );

            /*
             * Publish Kafka Event
             */

            PaymentSucceededEvent event = PaymentSucceededEvent.builder()
                    .paymentId(payment.getPaymentId())
                    .orderId(payment.getOrderId())
                    .customerId(payment.getCustomerId())
                    .amount(payment.getAmount())
                    .transactionNo(payment.getVnpTransactionNo())
                    .paymentMethod(payment.getPaymentMethod())
                    .build();

            paymentProducer.publishPaymentCompleted(event);

        } else {

            paymentDAO.updateFailed(
                    txnRef
            );

            PaymentFailedEvent event = PaymentFailedEvent.builder()
                    .orderId(payment.getOrderId())
                    .customerId(payment.getCustomerId())
                    .amount(payment.getAmount())
                    .reason("VNPAY payment failed")
                    .build();

            paymentProducer.publishPaymentFailed(event);
        }
    }
}
