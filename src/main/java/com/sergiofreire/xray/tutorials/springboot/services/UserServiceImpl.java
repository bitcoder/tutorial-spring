package com.sergiofreire.xray.tutorials.springboot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sergiofreire.xray.tutorials.springboot.data.User;
import com.sergiofreire.xray.tutorials.springboot.data.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> getUserDetails(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username));
    }

    @Override
    public boolean exists(String username) {
        return userRepository.findByUsername(username) != null;
    }

    @Override
    public User save(User user) {
        // Hash the password before saving to the database
        // Only hash if the password is not already a BCrypt hash
        // BCrypt hashes start with $2a$, $2b$, or $2y$ and are 60 characters long
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String password = user.getPassword();
            if (!password.startsWith("$2a$") && !password.startsWith("$2b$") && !password.startsWith("$2y$")) {
                user.setPassword(passwordEncoder.encode(password));
            }
        }
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }
}
