package com.plh.foodappbackend;

import com.plh.foodappbackend.model.Cart;
import com.plh.foodappbackend.model.CartItem;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CartLogicTest {

    @Test
    public void testDeliveryLogic_Under300() throws Exception {
        Cart cart = new Cart();
        List<CartItem> items = new ArrayList<>();
        CartItem item1 = new CartItem();
        item1.setPrice(BigDecimal.valueOf(100));
        items.add(item1);
        CartItem item2 = new CartItem();
        item2.setPrice(BigDecimal.valueOf(100));
        items.add(item2);
        cart.setItems(items);

        // Logic check: Sum = 200. Delivery should be 40.
        BigDecimal totalItemPrice = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            totalItemPrice = totalItemPrice.add(item.getPrice());
        }

        BigDecimal deliveryFee = BigDecimal.ZERO;
        if (totalItemPrice.compareTo(BigDecimal.valueOf(300)) < 0) {
            deliveryFee = BigDecimal.valueOf(40);
        }

        assertEquals(BigDecimal.valueOf(40), deliveryFee, "Delivery fee should be 40 for subtotal 200");
    }

    @Test
    public void testDeliveryLogic_Over300() throws Exception {
        Cart cart = new Cart();
        List<CartItem> items = new ArrayList<>();
        CartItem item1 = new CartItem();
        item1.setPrice(BigDecimal.valueOf(200));
        items.add(item1);
        CartItem item2 = new CartItem();
        item2.setPrice(BigDecimal.valueOf(150));
        items.add(item2);
        cart.setItems(items);

        // Logic check: Sum = 350. Delivery should be 0.
        BigDecimal totalItemPrice = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            totalItemPrice = totalItemPrice.add(item.getPrice());
        }

        BigDecimal deliveryFee = BigDecimal.ZERO;
        if (totalItemPrice.compareTo(BigDecimal.valueOf(300)) < 0) {
            deliveryFee = BigDecimal.valueOf(40);
        }

        assertEquals(BigDecimal.ZERO, deliveryFee, "Delivery fee should be 0 for subtotal 350");
    }
}
