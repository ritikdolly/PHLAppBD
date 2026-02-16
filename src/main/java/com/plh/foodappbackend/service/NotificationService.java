package com.plh.foodappbackend.service;

import com.plh.foodappbackend.model.Notification;
import com.plh.foodappbackend.model.Order;
import com.plh.foodappbackend.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    public void sendOrderNotification(Order order) {
        Notification notification = new Notification();
        notification.setMessage("New order placed by " + order.getUserName());
        notification.setOrderId(order.getId());
        notification.setTotalAmount(order.getTotalAmount());
        notification.setCustomerName(order.getUserName());
        notification.setCreatedAt(new Date());
        notification.setRead(false);

        Notification savedNotification = notificationRepository.save(notification);

        // Send to WebSocket topic
        messagingTemplate.convertAndSend("/topic/admin-notifications", savedNotification);
    }

    public List<Notification> getUnreadNotifications() {
        return notificationRepository.findByIsReadFalseOrderByCreatedAtDesc();
    }

    public void markAsRead(String id) {
        notificationRepository.findById(id).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    public void markAllAsRead() {
        List<Notification> notifications = notificationRepository.findByIsReadFalseOrderByCreatedAtDesc();
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }
}
