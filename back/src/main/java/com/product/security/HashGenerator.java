package com.product.security;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class HashGenerator {
    public static void main(String[] args) {
        String password = "admin123";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println("Hashed password for '" + password + "': " + hashedPassword);
    }
}
