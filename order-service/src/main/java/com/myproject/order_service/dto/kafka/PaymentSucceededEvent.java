package com.myproject.order_service.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSucceededEvent {

    private Long paymentId;

    private Long orderId;

    private Long customerId;

    private Double amount;

    private String paymentMethod;

    private String transactionNo;
}