package com.product.controller;

import com.product.model.WishlistItem;
import com.product.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@CrossOrigin(origins = "*")
public class WishlistController {
    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<List<WishlistItem>> getWishlist(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(wishlistService.getUserWishlist(userEmail));
    }

    @PostMapping("/items")
    public ResponseEntity<WishlistItem> addToWishlist(
            Authentication authentication,
            @RequestParam Long productId) {
        String userEmail = authentication.getName();
        WishlistItem item = wishlistService.addToWishlist(userEmail, productId);
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeFromWishlist(@PathVariable Long itemId) {
        wishlistService.removeFromWishlist(itemId);
        return ResponseEntity.noContent().build();
    }
}
