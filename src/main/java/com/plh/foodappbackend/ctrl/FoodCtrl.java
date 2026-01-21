package com.plh.foodappbackend.ctrl;

import com.plh.foodappbackend.model.Food;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/food")
public class FoodCtrl {


    @PostMapping("/add")
    public String addFood(@RequestBody Food food){
        return "";
    }
}
