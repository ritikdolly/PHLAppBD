package com.plh.foodappbackend.ctrl;

import com.plh.foodappbackend.model.Cart;
import com.plh.foodappbackend.model.User;
import com.plh.foodappbackend.request.AddCartRequest;
import com.plh.foodappbackend.service.CartService;
import com.plh.foodappbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Cart> getCart(@RequestHeader(value = "Authorization", required = false) String jwt) {
        User user = resolveUser();
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(cartService.getCart(user), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody AddCartRequest request,
            @RequestHeader(value = "Authorization", required = false) String jwt) {
        User user = resolveUser();
        if (user == null) {
            return new ResponseEntity<>("User must be logged in", HttpStatus.UNAUTHORIZED);
        }

        try {
            Cart cart = cartService.addToCart(user, request);
            return new ResponseEntity<>(cart, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/item/{foodId}")
    public ResponseEntity<Cart> updateItemQty(@PathVariable String foodId,
            @RequestBody AddCartRequest request, // reusing request for qty
            @RequestHeader(value = "Authorization", required = false) String jwt) {
        User user = resolveUser();
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(cartService.updateItemQuantity(user, foodId, request.getQuantity()),
                HttpStatus.OK);
    }

    @DeleteMapping("/item/{foodId}")
    public ResponseEntity<Cart> removeItem(@PathVariable String foodId,
            @RequestHeader(value = "Authorization", required = false) String jwt) {
        User user = resolveUser();
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(cartService.removeItem(user, foodId), HttpStatus.OK);
    }

    private User resolveUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && !authentication.getPrincipal().equals("anonymousUser")) {
                Object principal = authentication.getPrincipal();
                String email = null;

                if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                    email = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
                } else if (principal instanceof String) {
                    email = (String) principal;
                }

                if (email != null) {
                    return userService.findUserByEmail(email);
                }
            }
        } catch (Exception e) {
            logger.error("Error resolving user: ", e);
        }
        return null;
    }

    @PutMapping("/clear")
    public ResponseEntity<Cart> clearCart(@RequestHeader(value = "Authorization", required = false) String jwt) {
        User user = resolveUser();
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(cartService.clearCart(user), HttpStatus.OK);
    }
}
