package com.plh.foodappbackend.service;

import com.plh.foodappbackend.model.Food;

import java.util.List;

public interface FoodService {

    String addFood(Food food);

    String editFood(String id);

    String deleteFood(String id);

    List<Food> getAllFood();

    Food getFood(String id);

    List<String> getType();
}
