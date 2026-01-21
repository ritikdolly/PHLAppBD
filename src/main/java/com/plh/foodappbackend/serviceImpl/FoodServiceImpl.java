package com.plh.foodappbackend.serviceImpl;

import com.plh.foodappbackend.model.Food;
import com.plh.foodappbackend.service.FoodService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodServiceImpl implements FoodService {
    @Override
    public String addFood(Food food) {
        return "";
    }

    @Override
    public String editFood(String id) {
        return "";
    }

    @Override
    public String deleteFood(String id) {
        return "";
    }

    @Override
    public List<Food> getAllFood() {
        return List.of();
    }



    @Override
    public List<String> getType() {
        return List.of();
    }

    @Override
    public Food getFood(String id) {
        return null;
    }
}
