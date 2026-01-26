package com.plh.foodappbackend.service;

import com.plh.foodappbackend.model.Review;

import java.util.List;

public interface ReviewService {
    List<Review> getAllReviews();

    void deleteReview(String id);
}
