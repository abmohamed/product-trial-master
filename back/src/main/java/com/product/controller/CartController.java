package com.product.controller;

import com.product.model.CartItem;
import com.product.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(cartService.getUserCart(userEmail));
    }

    @PostMapping("/items")
    public ResponseEntity<CartItem> addToCart(
            Authentication authentication,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity) {
        String userEmail = authentication.getName();
        CartItem cartItem = cartService.addToCart(userEmail, productId, quantity);
        return ResponseEntity.ok(cartItem);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long itemId) {
        cartService.removeFromCart(itemId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/items/{itemId}")
    public ResponseEntity<CartItem> updateQuantity(
            @PathVariable Long itemId,
            @RequestParam int quantity) {
        CartItem updated = cartService.updateQuantity(itemId, quantity);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        String userEmail = authentication.getName();
        cartService.clearCart(userEmail);
        return ResponseEntity.noContent().build();
    }
}
