package com.product.service;

import com.product.model.CartItem;
import com.product.model.Product;
import com.product.repository.CartRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ProductService productService;

    public CartService(CartRepository cartRepository, ProductService productService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
    }

    public List<CartItem> getUserCart(String userEmail) {
        return cartRepository.findByUserEmail(userEmail);
    }

    public CartItem addToCart(String userEmail, Long productId, int quantity) {
        Product product = productService.getProductById(productId);
        
        CartItem cartItem = new CartItem();
        cartItem.setUserEmail(userEmail);
        cartItem.setProductId(productId);
        cartItem.setQuantity(quantity);
        cartItem.setPrice(product.getPrice());

        return cartRepository.save(cartItem);
    }

    public void removeFromCart(Long cartItemId) {
        cartRepository.deleteById(cartItemId);
    }

    public void clearCart(String userEmail) {
        cartRepository.deleteByUserEmail(userEmail);
    }

    public CartItem updateQuantity(Long cartItemId, int quantity) {
        List<CartItem> allItems = cartRepository.findAll();
        CartItem item = allItems.stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));

        if (quantity <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than 0");
        }

        item.setQuantity(quantity);
        return cartRepository.save(item);
    }
}
