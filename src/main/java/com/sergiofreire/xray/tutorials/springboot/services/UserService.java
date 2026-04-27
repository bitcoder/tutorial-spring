package com.sergiofreire.xray.tutorials.springboot.services;

import java.util.List;
import java.util.Optional;

import com.sergiofreire.xray.tutorials.springboot.boundary.ResourceNotFoundException;
import com.sergiofreire.xray.tutorials.springboot.data.User;

public interface UserService {

    public Optional<User> getUserDetails(Long id);

    public Optional<User> getUserByUsername(String username);

    public List<User> getAllUsers();

    public boolean exists(String username);

    public User save(User user);

    public User updateUser(Long id, User userDetails) throws ResourceNotFoundException;

    public void deleteUser(User user);
}
