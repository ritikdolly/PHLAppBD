package com.plh.foodappbackend.service;

import com.plh.foodappbackend.model.Cart;
import com.plh.foodappbackend.model.User;
import com.plh.foodappbackend.request.AddCartRequest;

public interface CartService {
    Cart getCart(User user);

    Cart addToCart(User user, AddCartRequest request) throws Exception;

    Cart updateItemQuantity(User user, String foodId, int quantity);

    Cart removeItem(User user, String foodId);

    Cart clearCart(User user);
}
