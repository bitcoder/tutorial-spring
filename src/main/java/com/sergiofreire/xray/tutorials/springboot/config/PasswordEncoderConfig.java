package com.sergiofreire.xray.tutorials.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class to provide a PasswordEncoder bean for password hashing.
 * Uses BCrypt algorithm which is a strong hashing function designed for passwords.
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * Creates a BCryptPasswordEncoder bean.
     * BCrypt automatically handles salt generation and is computationally expensive
     * to protect against brute-force attacks.
     *
     * @return PasswordEncoder instance using BCrypt algorithm
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
