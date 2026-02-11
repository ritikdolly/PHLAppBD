package com.plh.foodappbackend.serviceImpl;

import com.plh.foodappbackend.model.CustomerReview;
import com.plh.foodappbackend.model.User;
import com.plh.foodappbackend.repository.CustomerReviewRepository;
import com.plh.foodappbackend.service.CustomerReviewService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class CustomerReviewServiceImpl implements CustomerReviewService {

    private final CustomerReviewRepository customerReviewRepository;

    @Override
    public CustomerReview createReview(CustomerReview review, User user) throws Exception {
        CustomerReview existingReview = customerReviewRepository.findByUserId(user.getId());

        if (existingReview != null) {
            // Update existing review
            existingReview.setRating(review.getRating());
            existingReview.setComment(review.getComment());
            existingReview.setCreatedAt(new Date()); // Update timestamp
            return customerReviewRepository.save(existingReview);
        }

        // Create new review
        review.setUserId(user.getId());
        review.setUserName(user.getName());
        review.setCreatedAt(new Date());

        return customerReviewRepository.save(review);
    }

    @Override
    public List<CustomerReview> getAllReviews() {
        return customerReviewRepository.findAll();
    }

    @Override
    public void deleteReview(String id) throws Exception {
        if (!customerReviewRepository.existsById(id)) {
            throw new Exception("Review not found");
        }
        customerReviewRepository.deleteById(id);
    }
}
