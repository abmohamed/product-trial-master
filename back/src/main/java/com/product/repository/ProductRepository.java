package com.product.repository;

import com.product.model.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {

    private static final Logger logger = LoggerFactory.getLogger(ProductRepository.class);
    private List<Product> products = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AtomicLong counter = new AtomicLong();
    private static final String PRODUCTS_JSON_PATH = "products.json";

    @PostConstruct
    public void init() {
        loadProductsFromFile();
        if (!products.isEmpty()) {
            long maxId = products.stream().mapToLong(Product::getId).max().orElse(0L);
            counter.set(maxId + 1);
        } else {
            // We decided to start IDs from 1 in case the product list is empty
            counter.set(1L); 
        }
        logger.info("The Product Repository is initialized with {} products. So the next ID will be {}.", products.size(), counter.get());
    }

    private void loadProductsFromFile() {
        try {
            ClassPathResource resource = new ClassPathResource(PRODUCTS_JSON_PATH);
            if (!resource.exists()) {
                logger.warn("The products file was not found in the classpath: {}", PRODUCTS_JSON_PATH);
                // Start the initialization with an empty list otherwise we should handle as an error
                this.products = new ArrayList<>();
                return;
            }
            try (InputStream inputStream = resource.getInputStream()) {
                this.products = objectMapper.readValue(inputStream, new TypeReference<List<Product>>() {});
                logger.info("WE successfully loaded {} products from the predefined classpath: {}", products.size(), PRODUCTS_JSON_PATH);
            }
        } catch (IOException e) {
            logger.error("There is an error when loading products from the predefined classpath: {}", PRODUCTS_JSON_PATH, e);
            // Load an empty list in case of error
            this.products = new ArrayList<>();
        }
    }

    public List<Product> findAll() {
        return new ArrayList<>(products);
    }

    public Optional<Product> findById(Long id) {
        return products.stream().filter(product -> product.getId().equals(id)).findFirst();
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(counter.getAndIncrement());
            product.setCreatedAt(System.currentTimeMillis());
            product.setUpdatedAt(System.currentTimeMillis());
            products.add(product);
        } else {
            Optional<Product> existingProductOpt = findById(product.getId());
            if (existingProductOpt.isPresent()) {
                Product existingProduct = existingProductOpt.get();
                product.setCreatedAt(existingProduct.getCreatedAt());
                product.setUpdatedAt(System.currentTimeMillis());
                products.remove(existingProduct);
                products.add(product);
            } else {
                // In case we did not find the product by it's id, then we will assign a new id to avoid any conflicts
                product.setId(counter.getAndIncrement());
                product.setCreatedAt(System.currentTimeMillis());
                product.setUpdatedAt(System.currentTimeMillis());
                products.add(product);
            }
        }
        // Save the product to the json file
        persistProductsToFile(); 
        return product;
    }

    public void deleteById(Long id) {
        products = products.stream()
                           .filter(product -> !product.getId().equals(id))
                           .collect(Collectors.toList());
        // Delete the product to the json file
        persistProductsToFile(); 
    }


    private void persistProductsToFile() {
        try {
            ClassPathResource resource = new ClassPathResource(PRODUCTS_JSON_PATH);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(resource.getFile(), products);
            logger.info("Successfully persisted products to {}", PRODUCTS_JSON_PATH);
        } catch (IOException e) {
            logger.error("Error persisting products to {}", PRODUCTS_JSON_PATH, e);
        }
    }
}
