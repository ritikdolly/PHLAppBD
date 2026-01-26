package com.plh.foodappbackend.service;

import com.plh.foodappbackend.model.user;
import java.util.List;

public interface UserService {
    user getUserById(String id);

    void addFavorite(String userId, String foodId);

    void removeFavorite(String userId, String foodId);

    List<String> getFavorites(String userId);
}
