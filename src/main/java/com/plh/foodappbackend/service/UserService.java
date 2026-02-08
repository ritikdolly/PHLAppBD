package com.plh.foodappbackend.service;

import com.plh.foodappbackend.model.User;
import java.util.List;

public interface UserService {
    User getUserById(String id);

    void addFavorite(String userId, String foodId);

    void removeFavorite(String userId, String foodId);

    List<String> getFavorites(String userId);

    User updateUser(User user);

    User findUserByEmail(String email);
}
