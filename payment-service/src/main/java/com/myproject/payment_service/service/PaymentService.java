package com.myproject.payment_service.service;

import com.myproject.payment_service.dto.response.BaseResponse;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

public interface PaymentService {
    BaseResponse<String> createPayment(Long orderId, Long customerId, Double amount);


    void handleIpn(HttpServletRequest request);

}
