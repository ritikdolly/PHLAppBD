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
}
