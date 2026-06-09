package com.myproject.payment_service.controller;

import com.myproject.payment_service.config.VNPayProperties;
import com.myproject.payment_service.dto.request.CreatePaymentRequest;
import com.myproject.payment_service.dto.response.BaseResponse;
import com.myproject.payment_service.service.PaymentService;
import com.myproject.payment_service.util.VNPayUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final VNPayProperties vnPayProperties;

    @GetMapping("/vnpay-return")
    public ResponseEntity<String> vnpayReturn(
            HttpServletRequest request
    ) {

        String responseCode =
                request.getParameter(
                        "vnp_ResponseCode"
                );

        if ("00".equals(responseCode)) {

            return ResponseEntity.ok(
                    "Thanh toán thành công"
            );
        }

        return ResponseEntity.ok(
                "Thanh toán thất bại"
        );
    }

    @PostMapping
    public ResponseEntity<BaseResponse> createPayment(@RequestBody CreatePaymentRequest request) {

        BaseResponse response = paymentService.createPayment(
                        request.getOrderId(),
                        request.getCustomerId(),
                        request.getAmount()
                );

        return ResponseEntity.ok(response);
    }

    /**
     * CHỈ DÙNG ĐỂ TEST - sinh vnp_SecureHash cho URL IPN thủ công
     * Ví dụ: GET /api/payments/dev/gen-hash?vnp_TxnRef=abc&vnp_ResponseCode=00&vnp_TransactionNo=123&vnp_BankCode=NCB
     */
    @GetMapping("/dev/gen-hash")
    public ResponseEntity<Map<String, String>> genHash(HttpServletRequest request) {
        Map<String, String> fields = new TreeMap<>();
        request.getParameterNames().asIterator().forEachRemaining(name -> {
            String value = request.getParameter(name);
            if (value != null && !value.isBlank()) {
                fields.put(name, value);
            }
        });

        StringBuilder hashData = new StringBuilder();
        var iterator = fields.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            hashData.append(entry.getKey()).append("=")
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII));
            if (iterator.hasNext()) hashData.append("&");
        }

        String hash = VNPayUtil.hmacSHA512(vnPayProperties.getHashSecret(), hashData.toString());

        String fullUrl = "http://localhost:8084/api/payments/vnpay-ipn?" +
                hashData + "&vnp_SecureHash=" + hash;

        return ResponseEntity.ok(Map.of(
                "vnp_SecureHash", hash,
                "hashData", hashData.toString(),
                "fullTestUrl", fullUrl
        ));
    }

    @PostMapping("/vnpay-ipn")
    public ResponseEntity<Map<String, String>> vnpayIpn(
            HttpServletRequest request
    ) {

        paymentService.handleIpn(request);

        return ResponseEntity.ok(
                Map.of(
                        "RspCode", "00",
                        "Message", "Confirm Success"
                )
        );
    }

}