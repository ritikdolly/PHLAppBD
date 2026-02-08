package com.plh.foodappbackend.serviceImpl;

import com.plh.foodappbackend.model.Cart;
import com.plh.foodappbackend.model.CartItem;
import com.plh.foodappbackend.model.Food;
import com.plh.foodappbackend.model.User;
import com.plh.foodappbackend.repository.CartRepository;
import com.plh.foodappbackend.repository.FoodRepository;
import com.plh.foodappbackend.request.AddCartRequest;
import com.plh.foodappbackend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Override
    public Cart getCart(User user) {
        if (user == null) {
            return new Cart(); // Should not happen if controller enforces auth, but safe fallback
        }

        Optional<Cart> cartOptional = cartRepository.findByUserId(user.getId());

        if (cartOptional.isEmpty()) {
            Cart cart = new Cart();
            cart.setItems(new ArrayList<>());
            cart.setTotalAmount(0.0);
            cart.setUserId(user.getId());
            return cartRepository.save(cart);
        }

        return cartOptional.get();
    }

    @Override
    public Cart addToCart(User user, AddCartRequest request) throws Exception {
        Cart cart = getCart(user);

        Food food = foodRepository.findById(request.getFoodId())
                .orElseThrow(() -> new Exception("Food not found"));

        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        for (CartItem item : cart.getItems()) {
            if (item.getFoodId().equals(food.getId())) {
                throw new Exception("Food item already added to cart");
            }
        }

        CartItem newItem = new CartItem();
        newItem.setFoodId(food.getId());
        newItem.setQuantity(request.getQuantity());
        newItem.setPrice(food.getPrice() * request.getQuantity());
        newItem.setName(food.getName());
        newItem.setImageUrl(food.getImageUrl());

        cart.getItems().add(newItem);
        calculateCartTotal(cart);

        return cartRepository.save(cart);
    }

    @Override
    public Cart updateItemQuantity(User user, String foodId, int quantity) {
        Cart cart = getCart(user);
        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                if (item.getFoodId().equals(foodId)) {
                    item.setQuantity(quantity);

                    // Recalculate price: fetch food to get unit price safely
                    Optional<Food> foodOpt = foodRepository.findById(foodId);
                    if (foodOpt.isPresent()) {
                        item.setPrice(foodOpt.get().getPrice() * quantity);
                    }
                    break;
                }
            }
        }
        calculateCartTotal(cart);
        return cartRepository.save(cart);
    }

    @Override
    public Cart removeItem(User user, String foodId) {
        Cart cart = getCart(user);
        if (cart.getItems() != null) {
            cart.getItems().removeIf(item -> item.getFoodId().equals(foodId));
        }
        calculateCartTotal(cart);
        return cartRepository.save(cart);
    }

    @Override
    public Cart clearCart(User user) {
        Cart cart = getCart(user);
        cart.getItems().clear();
        cart.setTotalAmount(0.0);
        return cartRepository.save(cart);
    }

    private void calculateCartTotal(Cart cart) {
        double total = 0;
        for (CartItem item : cart.getItems()) {
            total += item.getPrice();
        }
        cart.setTotalAmount(total);
    }
}
