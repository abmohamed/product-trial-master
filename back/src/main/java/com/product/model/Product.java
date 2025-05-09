package com.product.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class Product {
    private Long id;
    
    @NotBlank(message = "The code is a must")
    private String code;
    
    @NotBlank(message = "The name is a must")
    private String name;
    
    private String description;

    private String image;

    private String category;

    private Integer shellId;

    private String internalReference;
    
    private Integer rating;

    @NotNull(message = "The price should be provided")
    @Positive(message = "The price should be positive")
    private Double price;

    @NotNull(message = "The quantity should be provided")
    @Positive(message = "The quantity should be positive")
    private Integer quantity;
    
    private InventoryStatus inventoryStatus;
    private Long createdAt;
    private Long updatedAt;
    
    public enum InventoryStatus {
        INSTOCK, LOWSTOCK, OUTOFSTOCK
    }
}
