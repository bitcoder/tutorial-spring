package com.sergiofreire.xray.tutorials.springboot.boundary;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sergiofreire.xray.tutorials.springboot.data.User;
import com.sergiofreire.xray.tutorials.springboot.services.UserService;

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

    @PostMapping("/users" )
    public ResponseEntity<User> createUser(@RequestBody User user) {
        HttpStatus status = HttpStatus.CREATED;
        User saved = userService.save(user);
        return new ResponseEntity<>(saved, status);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getCarById(@PathVariable(value = "id") Long id)
            throws ResourceNotFoundException {
        User user = userService.getUserDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for id: " + id));
        return ResponseEntity.ok().body(user);
    }

    @GetMapping(path="/users" )
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<User> deleteUserById(@PathVariable(value = "id") Long id)
            throws ResourceNotFoundException {
        User user = userService.getUserDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for id: " + id));
        userService.deleteUser(user);
        return ResponseEntity.ok().body(user); 
    }

}
