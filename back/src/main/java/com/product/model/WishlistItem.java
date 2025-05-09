package com.product.model;

import lombok.Data;

@Data
public class WishlistItem {
    private Long id;
    private Long productId;
    private String userEmail;
    private String addedAt;
}
