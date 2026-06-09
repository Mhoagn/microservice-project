package com.myproject.notification_service.service.impl;

import com.myproject.notification_service.dao.NotificationDAO;
import com.myproject.notification_service.dto.kafka.PaymentFailedEvent;
import com.myproject.notification_service.dto.kafka.PaymentSucceededEvent;
import com.myproject.notification_service.dto.request.NewNotificationDTO;
import com.myproject.notification_service.dto.response.BaseResponse;
import com.myproject.notification_service.dto.response.NotificationResponse;
import com.myproject.notification_service.dto.response.ScrollResponse;
import com.myproject.notification_service.entity.Notification;
import com.myproject.notification_service.enums.NotificationType;
import com.myproject.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationDAO notificationDAO;

    @Override
    public BaseResponse<ScrollResponse> getNoficationByCustomerId(Long customerId,Long cursor, int limit) {
        List<Notification> notificationList = notificationDAO.getNotificationByCustomerId(customerId,cursor,limit);

        boolean hasNext = notificationList.size() > limit;

        if (hasNext) {
            notificationList.remove(notificationList.size() - 1);
        }

        Long nextCursor = notificationList.isEmpty()
                ? null
                : notificationList.get(notificationList.size() - 1).getNotificationId();

        List<NotificationResponse> responseList = notificationList.stream()
                .map(item -> {
                    NotificationResponse res = NotificationResponse.builder()
                            .notificationId(item.getNotificationId())
                            .notificationType(item.getNotificationType())
                            .title(item.getTitle())
                            .message(item.getMessage())
                            .createdAt(item.getCreatedAt())
                            .build();
                    return res;
                })
                .toList();

        ScrollResponse<NotificationResponse> scrollResponse =
                new ScrollResponse<>(
                        responseList,
                        nextCursor,
                        hasNext
                );

        return new BaseResponse<>(
                true,
                "Get notifications for customer successfully",
                scrollResponse
        );
    }

    @Override
    public void createNotificationForOrder(Long customerId, Double amount) {
        String title = "Đơn hàng đã được tạo";

        String notificationType = NotificationType.ORDER_CREATED.toString();

        String message = String.format(
                "Đơn hàng của bạn đã được tạo thành công. Tổng thanh toán: %.2f VND. "
                        + "Vui lòng hoàn tất thanh toán để tiếp tục xử lý đơn hàng.",
                amount
        );

        NewNotificationDTO dto = NewNotificationDTO.builder()
                .customerId(customerId)
                .notificationType(notificationType)
                .title(title)
                .message(message)
                .build();

        notificationDAO.createNewNotification(dto);
    }

    @Override
    public void createNotificationForPaymentSucceeded(PaymentSucceededEvent event) {
        String title = "Thanh toán thành công";
        String notificationType = NotificationType.PAYMENT_SUCCEEDED.toString();

        String message = String.format(
                "Thanh toán cho đơn hàng #%d đã thành công. "
                        + "Số tiền %.2f VND đã được xác nhận.",
                event.getOrderId(),
                event.getAmount()
        );

        NewNotificationDTO dto = NewNotificationDTO.builder()
                .customerId(event.getCustomerId())
                .notificationType(notificationType)
                .title(title)
                .message(message)
                .build();

        notificationDAO.createNewNotification(dto);
    }

    @Override
    public void createNotificationForPaymentFailed(PaymentFailedEvent event) {
        String title = "Thanh toán thất bại";
        String notificationType = NotificationType.PAYMENT_FAILED.toString();

        String message = String.format(
                "Thanh toán cho đơn hàng #%d không thành công. "
                        + "Lý do: %s",
                event.getOrderId(),
                event.getReason()
        );

        NewNotificationDTO dto = NewNotificationDTO.builder()
                .customerId(event.getCustomerId())
                .notificationType(notificationType)
                .title(title)
                .message(message)
                .build();

        notificationDAO.createNewNotification(dto);
    }

}
