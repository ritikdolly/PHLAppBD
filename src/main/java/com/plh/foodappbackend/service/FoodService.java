package com.plh.foodappbackend.service;

import com.plh.foodappbackend.model.Food;

import java.util.List;

public interface FoodService {

    Food addFood(Food food);

    Food updateFood(String id, Food food);

    void deleteFood(String id);

    List<Food> getAllFoods();

    Food getFoodById(String id);

    List<String> getFoodTypes();
}
