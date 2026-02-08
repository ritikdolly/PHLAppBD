package com.plh.foodappbackend.repository;

import com.plh.foodappbackend.model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CartRepository extends MongoRepository<Cart, String> {
    Optional<Cart> findByUserId(String userId);

    Optional<Cart> findBySessionId(String sessionId);
}
