package com.sergiofreire.xray.tutorials.springboot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sergiofreire.xray.tutorials.springboot.data.User;
import com.sergiofreire.xray.tutorials.springboot.data.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> getUserDetails(Long id) {
        // return optional of found user or return an empty Optional if not found
        Optional<User> user = userRepository.findById(id);
        if (user == null) {
            return Optional.empty();
        } else {
            return user;
        }
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username));
    }

    @Override
    public boolean exists(String username) {
        if (userRepository.findByUsername(username) != null) {
            return true;
        }
        return false;
    }

    @Override
    public User save(User user) {
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
