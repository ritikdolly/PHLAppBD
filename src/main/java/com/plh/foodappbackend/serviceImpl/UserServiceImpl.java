package com.plh.foodappbackend.serviceImpl;

import com.plh.foodappbackend.model.User;
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
    public User getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void addFavorite(String userId, String foodId) {
        User u = userRepository.findById(userId).orElse(null);
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
        User u = userRepository.findById(userId).orElse(null);
        if (u != null && u.getFavorites() != null) {
            u.getFavorites().remove(foodId);
            userRepository.save(u);
        }
    }

    @Override
    public List<String> getFavorites(String userId) {
        User u = userRepository.findById(userId).orElse(null);
        return (u != null && u.getFavorites() != null) ? u.getFavorites() : new ArrayList<>();
    }

    @Override
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId()).orElse(null);
        if (existingUser != null) {
            existingUser.setName(user.getName());
            existingUser.setPhone(user.getPhone());
            existingUser.setBio(user.getBio());
            // existingUser.setEmail(user.getEmail()); // Optional: decide if email update
            // is allowed here
            // Persist other fields like address if needed, but for now ProfilePage only has
            // these.
            return userRepository.save(existingUser);
        }
        return null;
    }
}
