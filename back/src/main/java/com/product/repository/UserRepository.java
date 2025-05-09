package com.product.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.model.User;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);
    private List<User> users = new ArrayList<>();
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;
    private final PasswordEncoder passwordEncoder;
    private final Path dataPath;

    public UserRepository(ObjectMapper objectMapper, ResourceLoader resourceLoader, PasswordEncoder passwordEncoder) throws IOException {
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
        this.passwordEncoder = passwordEncoder;
        this.dataPath = Paths.get(System.getProperty("user.home"), ".product", "users.json");
        Files.createDirectories(dataPath.getParent());
    }

    @PostConstruct
    public void init() throws IOException {
        logger.info("Initializing UserRepository...");
        if (Files.exists(dataPath)) {
            logger.info("Loading users from file: {}", dataPath);
            users = objectMapper.readValue(dataPath.toFile(), new TypeReference<List<User>>() {});
            logger.info("Loaded {} users from file", users.size());
        } else {
            // Try to load initial data from classpath resource
            Resource resource = resourceLoader.getResource("classpath:users.json");
            if (resource.exists()) {
                try (InputStream is = resource.getInputStream()) {
                    users = objectMapper.readValue(is, new TypeReference<List<User>>() {});
                    logger.info("Loaded {} users from classpath resource", users.size());
                }
            }
        }
        
        // Create initial admin user if no users exist
        if (users.isEmpty()) {
            logger.info("No users found. Creating initial admin user...");
            User adminUser = new User();
            adminUser.setEmail("admin@admin.com");
            adminUser.setUsername("admin");
            adminUser.setFirstname("Admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            users.add(adminUser);
            saveToFile();
            logger.info("Initial admin user created successfully");
        }
    }

    private void saveToFile() {
        try {
            logger.info("Saving {} users to file: {}", users.size(), dataPath);
            objectMapper.writeValue(dataPath.toFile(), users);
            logger.info("Users saved successfully");
        } catch (IOException e) {
            logger.error("Failed to save users to file", e);
            throw new RuntimeException("Failed to save users to file", e);
        }
    }

    public User save(User user) {
        users.add(user);
        saveToFile();
        return user;
    }

    public Optional<User> findByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }
    
    public List<User> findAll() {
        return new ArrayList<>(users);
    }
}
