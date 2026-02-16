package com.plh.foodappbackend.repository;

import com.plh.foodappbackend.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByIsReadFalseOrderByCreatedAtDesc();
}
