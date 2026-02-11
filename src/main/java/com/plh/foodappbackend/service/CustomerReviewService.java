package com.plh.foodappbackend.service;

import com.plh.foodappbackend.model.CustomerReview;
import com.plh.foodappbackend.model.User;

import java.util.List;

public interface CustomerReviewService {
    CustomerReview createReview(CustomerReview review, User user) throws Exception;

    List<CustomerReview> getAllReviews();

    void deleteReview(String id) throws Exception;
}
