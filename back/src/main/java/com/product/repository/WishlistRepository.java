package com.product.repository;

import com.product.model.WishlistItem;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class WishlistRepository {
    private final Map<Long, WishlistItem> wishlistItems = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public WishlistItem save(WishlistItem item) {
        if (item.getId() == null) {
            item.setId(idGenerator.incrementAndGet());
        }
        wishlistItems.put(item.getId(), item);
        return item;
    }

    public void deleteById(Long id) {
        wishlistItems.remove(id);
    }

    public List<WishlistItem> findByUserEmail(String userEmail) {
        return wishlistItems.values().stream()
                .filter(item -> item.getUserEmail().equals(userEmail))
                .toList();
    }

    public boolean existsByUserEmailAndProductId(String userEmail, Long productId) {
        return wishlistItems.values().stream()
                .anyMatch(item -> item.getUserEmail().equals(userEmail) 
                        && item.getProductId().equals(productId));
    }
}
