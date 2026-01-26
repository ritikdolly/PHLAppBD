package com.plh.foodappbackend.serviceImpl;

import com.plh.foodappbackend.model.user;
import com.plh.foodappbackend.repository.UserRepository;
import com.plh.foodappbackend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public user getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void addFavorite(String userId, String foodId) {
        user u = userRepository.findById(userId).orElse(null);
        if (u != null) {
            if (u.getFavorites() == null) {
                u.setFavorites(new ArrayList<>());
            }
            if (!u.getFavorites().contains(foodId)) {
                u.getFavorites().add(foodId);
                userRepository.save(u);
            }
        }
    }

    @Override
    public void removeFavorite(String userId, String foodId) {
        user u = userRepository.findById(userId).orElse(null);
        if (u != null && u.getFavorites() != null) {
            u.getFavorites().remove(foodId);
            userRepository.save(u);
        }
    }

    @Override
    public List<String> getFavorites(String userId) {
        user u = userRepository.findById(userId).orElse(null);
        return (u != null && u.getFavorites() != null) ? u.getFavorites() : new ArrayList<>();
    }
}
