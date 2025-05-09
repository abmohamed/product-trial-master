package com.product.repository;

import com.product.model.CartItem;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CartRepository {
    private final Map<Long, CartItem> cartItems = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public CartItem save(CartItem item) {
        if (item.getId() == null) {
            item.setId(idGenerator.incrementAndGet());
        }
        cartItems.put(item.getId(), item);
        return item;
    }

    public void deleteById(Long id) {
        cartItems.remove(id);
    }

    public List<CartItem> findByUserEmail(String userEmail) {
        return cartItems.values().stream()
                .filter(item -> item.getUserEmail().equals(userEmail))
                .toList();
    }

    public void deleteByUserEmail(String userEmail) {
        cartItems.values().removeIf(item -> item.getUserEmail().equals(userEmail));
    }

    public List<CartItem> findAll() {
        return new ArrayList<>(cartItems.values());
    }
}
