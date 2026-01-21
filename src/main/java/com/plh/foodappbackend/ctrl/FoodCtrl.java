package com.plh.foodappbackend.ctrl;

import com.plh.foodappbackend.model.Food;
import com.plh.foodappbackend.service.FoodService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/foods")
@AllArgsConstructor
public class FoodCtrl {

    private FoodService foodService;

    @PostMapping("/add")
    public String addFood(@RequestBody Food food){
        return foodService.addFood(food);
    }

    @PostMapping("/edit")
    public String editFood(@RequestBody String id){
        return foodService.editFood(id);
    }

    @PostMapping("/delete")
    public String deleteFood(@RequestBody String id){
        return foodService.deleteFood(id);
    }

    @PostMapping("/type")
    public List<String> deleteFood(){
        return foodService.getType();
    }

    @PostMapping("/get/one")
    public Food getFood(@RequestBody String id){
        return foodService.getFood(id);
    }
    @PostMapping("/get/all")
    public List<Food> getFood(){
        return foodService.getAllFood();
    }



}
