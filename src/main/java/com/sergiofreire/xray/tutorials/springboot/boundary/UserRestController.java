package com.sergiofreire.xray.tutorials.springboot.boundary;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sergiofreire.xray.tutorials.springboot.boundary.dto.CreateUserRequest;
import com.sergiofreire.xray.tutorials.springboot.boundary.dto.UserResponse;
import com.sergiofreire.xray.tutorials.springboot.data.User;
import com.sergiofreire.xray.tutorials.springboot.services.UserService;

import jakarta.validation.Valid;

import java.util.List;

/**
 * REST API endpoints for managing users
 */
@RestController
@RequestMapping("/api")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        User saved = userService.save(new User(request.name(), request.username(), request.password()));
        return new ResponseEntity<>(UserResponse.from(saved), HttpStatus.CREATED);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable(value = "id") Long id)
            throws ResourceNotFoundException {
        User user = userService.getUserDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for id: " + id));
        return ResponseEntity.ok().body(UserResponse.from(user));
    }

    @GetMapping(path = "/users")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers().stream().map(UserResponse::from).toList();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable(value = "id") Long id)
            throws ResourceNotFoundException {
        User user = userService.getUserDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for id: " + id));
        userService.deleteUser(user);
        return ResponseEntity.noContent().build();
    }

}
