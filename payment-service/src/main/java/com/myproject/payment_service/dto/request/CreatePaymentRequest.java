package com.myproject.payment_service.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentRequest {
    private Long orderId;
    private Long customerId;
    private Double amount;
}
