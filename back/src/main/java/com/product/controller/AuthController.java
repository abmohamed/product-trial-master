package com.product.controller;

import com.product.model.User;
import com.product.model.dto.AuthResponse;
import com.product.model.dto.LoginRequest;
import com.product.repository.UserRepository;
import com.product.security.JwtTokenUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

@RestController
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
    public ResponseEntity<User> createAccount(@RequestBody User user) {
        // The password should be encoded before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        // The password should be cleared before returning
        savedUser.setPassword(null);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/api/token")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        logger.info("Login trying for user with the email: {}", loginRequest.getEmail());
        
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        if (userOpt.isEmpty()) {
            logger.warn("The user was not found with the email: {}", loginRequest.getEmail());
            return ResponseEntity.status(401).build();
        }
        
        User user = userOpt.get();
        boolean matches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
        
        if (!matches) {
            return ResponseEntity.status(401).build();
        }
        
        String token = jwtTokenUtil.createToken(user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
