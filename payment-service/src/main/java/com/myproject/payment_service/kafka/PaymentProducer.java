package com.myproject.payment_service.kafka;

import com.myproject.payment_service.dto.kafka.PaymentFailedEvent;
import com.myproject.payment_service.dto.kafka.PaymentSucceededEvent;
import com.myproject.payment_service.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentProducer {
    private static final String PAYMENT_SUCCESSED = "payment-succeeded";

    private static final String PAYMENT_FAILED = "payment-failed";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentCompleted(PaymentSucceededEvent event) {
        kafkaTemplate.send(
                PAYMENT_SUCCESSED,
                event.getPaymentId().toString(),
                event
        );
    }

    public void publishPaymentFailed (PaymentFailedEvent event) {
        kafkaTemplate.send(
                PAYMENT_FAILED,
                event.getOrderId().toString(),
                event
        );
    }

}
