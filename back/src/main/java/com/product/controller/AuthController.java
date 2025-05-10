package com.product.controller;

import com.product.model.User;
import com.product.model.dto.AuthResponse;
import com.product.model.dto.LoginRequest;
import com.product.repository.UserRepository;
import com.product.security.JwtTokenUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;
import org.springframework.http.HttpStatus;

@RestController
@CrossOrigin(origins = "*")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthController(UserRepository userRepository, 
                         PasswordEncoder passwordEncoder,
                         JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/api/account")
    public ResponseEntity<?> createAccount(@RequestBody User user) {
        // Validate that required field are present
        if (user.getEmail() == null || user.getEmail().trim().isEmpty() ||
            user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email and password are required");
        }

        // Check that the user exists already 
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
        }

        // MAke sure to encode password and save the user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        // Make sure to Clear the password before returning
        User userResponse = new User();
        userResponse.setEmail(savedUser.getEmail());
        userResponse.setUsername(savedUser.getUsername());
        userResponse.setFirstname(savedUser.getFirstname());

        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/api/token")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        logger.info("Login attempt for email: {}", loginRequest.getEmail());
        
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        if (userOpt.isEmpty()) {
            logger.warn("User not found with email: {}", loginRequest.getEmail());
            return ResponseEntity.status(401).build();
        }
        
        User user = userOpt.get();
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            logger.warn("The user has no password identified: {}", loginRequest.getEmail());
            return ResponseEntity.status(401).build();
        }
        
        boolean matches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
        
        if (!matches) {
            logger.warn("The password is not valid for the user: {}", loginRequest.getEmail());
            return ResponseEntity.status(401).build();
        }
        
        String token = jwtTokenUtil.createToken(user.getEmail());
        logger.info("The user has successfully logged in: {}", loginRequest.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
