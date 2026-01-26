package com.plh.foodappbackend.ctrl;

import com.plh.foodappbackend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserCtrl {

    private final UserService userService;

    @PostMapping("/{userId}/favorites/{foodId}")
    public void addFavorite(@PathVariable String userId, @PathVariable String foodId) {
        userService.addFavorite(userId, foodId);
    }

    @DeleteMapping("/{userId}/favorites/{foodId}")
    public void removeFavorite(@PathVariable String userId, @PathVariable String foodId) {
        userService.removeFavorite(userId, foodId);
    }

    @GetMapping("/{userId}/favorites")
    public List<String> getFavorites(@PathVariable String userId) {
        return userService.getFavorites(userId);
    }
}
