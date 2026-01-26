package com.plh.foodappbackend.serviceImpl;

import com.plh.foodappbackend.model.Review;
import com.plh.foodappbackend.repository.FoodRepository;
import com.plh.foodappbackend.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final FoodRepository foodRepository;

    @Override
    public List<Review> getAllReviews() {
        return foodRepository.findAll().stream()
                .filter(food -> food.getReviews() != null)
                .flatMap(food -> food.getReviews().stream()
                        .map(review -> {
                            // Enrich review with food details dynamically
                            review.setFoodId(food.getId());
                            review.setFoodName(food.getName());
                            return review;
                        }))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void deleteReview(String id) {
        // Inefficient but functional: Find food containing the review
        List<com.plh.foodappbackend.model.Food> foods = foodRepository.findAll();
        for (com.plh.foodappbackend.model.Food food : foods) {
            if (food.getReviews() != null) {
                boolean removed = food.getReviews().removeIf(r -> r.getId().equals(id));
                if (removed) {
                    // Update rating logic (duplicated from FoodService, ideal to reuse or extract)
                    updateRating(food);
                    foodRepository.save(food);
                    return; // Stop after deleting
                }
            }
        }
    }

    private void updateRating(com.plh.foodappbackend.model.Food food) {
        if (food.getReviews() == null || food.getReviews().isEmpty()) {
            food.setRating(0);
        } else {
            double avg = food.getReviews().stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);
            food.setRating(Math.round(avg * 10.0) / 10.0);
        }
    }
}
