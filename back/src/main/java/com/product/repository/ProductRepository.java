package com.product.repository;

import com.product.model.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import org.springframework.util.FileCopyUtils;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {

    private static final Logger logger = LoggerFactory.getLogger(ProductRepository.class);
    private List<Product> products = new ArrayList<>();
    private final ObjectMapper objMap = new ObjectMapper();
    private final AtomicLong countAtom = new AtomicLong();
    private static final String PRODUCTS_JSON_FILE = "products.json";
    private static final String PATH_TO_PRODUCTS_JSON = "src/main/resources/";

    @PostConstruct
    public void init() {
        getProductsFromJSON();
        if (!products.isEmpty()) {
            long maximumId = products.stream().mapToLong(Product::getId).max().orElse(0L);
            countAtom.set(maximumId + 1);
        } else {
            // We decided to start IDs from 1 in case the product list is empty
            countAtom.set(1L);
        }
        logger.info("The Product Repository is initialized with {} products. So the next ID will be {}.", products.size(), countAtom.get());
    }

    private void getProductsFromJSON() {
        try {
            ClassPathResource resourcePath = new ClassPathResource(PRODUCTS_JSON_FILE);
            if (!resourcePath.exists()) {
                logger.warn("The products file was not found in the classpath: {}", PRODUCTS_JSON_FILE);
                // Start the initialization with an empty list otherwise we should handle as an error
                this.products = new ArrayList<>();
                return;
            }
            try (InputStream inputStream = resourcePath.getInputStream()) {
                this.products = objMap.readValue(inputStream, new TypeReference<List<Product>>() {});
                logger.info("WE successfully loaded {} products from the predefined classpath: {}", products.size(), PRODUCTS_JSON_FILE);
            }
        } catch (IOException e) {
            logger.error("There is an error when loading products from the predefined classpath: {}", PRODUCTS_JSON_FILE, e);
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
            product.setId(countAtom.getAndIncrement());
            product.setCreatedAt(System.currentTimeMillis());
            product.setUpdatedAt(System.currentTimeMillis());
            products.add(product);
        } else {
            Optional<Product> findByIdexistingProduct = findById(product.getId());
            if (findByIdexistingProduct.isPresent()) {
                Product verifiedExistingProduct = findByIdexistingProduct.get();
                product.setCreatedAt(verifiedExistingProduct.getCreatedAt());
                product.setUpdatedAt(System.currentTimeMillis());
                products.remove(verifiedExistingProduct);
                products.add(product);
            } else {
                // In case we did not find the product by it's id, then we will assign a new id to avoid any conflicts
                product.setId(countAtom.getAndIncrement());
                product.setCreatedAt(System.currentTimeMillis());
                product.setUpdatedAt(System.currentTimeMillis());
                products.add(product);
            }
        }
        // Save the product to the json file
        persistProductsToJSONFile(); 
        return product;
    }

    public void deleteById(Long id) {
        products = products.stream()
                           .filter(product -> !product.getId().equals(id))
                           .collect(Collectors.toList());
        // Delete the product to the json file
        persistProductsToJSONFile();
    }

    private void persistProductsToJSONFile() {
        try {
            // Get the json file using path to find the file path
            File jsonFile = new File(PATH_TO_PRODUCTS_JSON + PRODUCTS_JSON_FILE);

            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(jsonFile), StandardCharsets.UTF_8)) {
                objMap.writerWithDefaultPrettyPrinter().writeValue(writer, products);
                logger.info("Successfully persisted {} products to {}", products.size(), jsonFile.getAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("Error persisting products to {}", PRODUCTS_JSON_FILE, e);
        }
    }
}
