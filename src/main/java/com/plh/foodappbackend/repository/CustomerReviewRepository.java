package com.plh.foodappbackend.repository;

import com.plh.foodappbackend.model.CustomerReview;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerReviewRepository extends MongoRepository<CustomerReview, String> {
    boolean existsByUserId(String userId);

    CustomerReview findByUserId(String userId);
}
