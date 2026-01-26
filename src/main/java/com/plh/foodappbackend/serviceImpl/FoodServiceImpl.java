package com.plh.foodappbackend.serviceImpl;

import com.plh.foodappbackend.model.Food;
import com.plh.foodappbackend.repository.FoodRepository;
import com.plh.foodappbackend.service.FoodService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FoodServiceImpl implements FoodService {

    private final FoodRepository foodRepository;

    @Override
    public Food addFood(Food food) {
        return foodRepository.save(food);
    }

    @Override
    public Food updateFood(String id, Food food) {
        food.setId(id); // Ensure ID matches path
        return foodRepository.save(food);
    }

    @Override
    public void deleteFood(String id) {
        foodRepository.deleteById(id);
    }

    @Override
    public List<Food> getAllFoods() {
        return foodRepository.findAll();
    }

    @Override
    public Food getFoodById(String id) {
        return foodRepository.findById(id).orElse(null);
    }

    @Override
    public List<String> getFoodTypes() {
        return foodRepository.findAll().stream()
                .flatMap(food -> food.getTypes().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Food addReview(String foodId, com.plh.foodappbackend.model.Review review) {
        Food food = foodRepository.findById(foodId).orElse(null);
        if (food != null) {
            if (food.getReviews() == null) {
                food.setReviews(new java.util.ArrayList<>());
            }
            food.getReviews().add(review);
            updateRating(food);
            return foodRepository.save(food);
        }
        return null;
    }

    @Override
    public Food deleteReview(String foodId, String reviewId) {
        Food food = foodRepository.findById(foodId).orElse(null);
        if (food != null && food.getReviews() != null) {
            food.getReviews().removeIf(r -> r.getId().equals(reviewId));
            updateRating(food);
            return foodRepository.save(food);
        }
        return null;
    }

    private void updateRating(Food food) {
        if (food.getReviews() == null || food.getReviews().isEmpty()) {
            food.setRating(0);
        } else {
            double avg = food.getReviews().stream()
                    .mapToDouble(com.plh.foodappbackend.model.Review::getRating)
                    .average()
                    .orElse(0.0);
            food.setRating(Math.round(avg * 10.0) / 10.0);
        }
    }
}
