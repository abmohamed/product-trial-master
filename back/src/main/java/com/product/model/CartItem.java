package com.product.model;

import lombok.Data;

@Data
public class CartItem {
    private Long id;
    private Long productId;
    private String userEmail;
    private int quantity;
    private double price; // Store the price at the time of adding to cart
}
