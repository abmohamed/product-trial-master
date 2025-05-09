package com.product.service;

import com.product.model.WishlistItem;
import com.product.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WishlistService {
    private final WishlistRepository wishlistRepository;

    public WishlistService(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    public List<WishlistItem> getUserWishlist(String userEmail) {
        return wishlistRepository.findByUserEmail(userEmail);
    }

    public WishlistItem addToWishlist(String userEmail, Long productId) {
        if (wishlistRepository.existsByUserEmailAndProductId(userEmail, productId)) {
            throw new RuntimeException("Product already in wishlist");
        }

        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setUserEmail(userEmail);
        wishlistItem.setProductId(productId);
        wishlistItem.setAddedAt(LocalDateTime.now().toString());

        return wishlistRepository.save(wishlistItem);
    }

    public void removeFromWishlist(Long wishlistItemId) {
        wishlistRepository.deleteById(wishlistItemId);
    }
}
