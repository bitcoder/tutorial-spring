package com.sergiofreire.xray.tutorials.springboot.boundary;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sergiofreire.xray.tutorials.springboot.boundary.dto.UserCreateDTO;
import com.sergiofreire.xray.tutorials.springboot.boundary.dto.UserResponseDTO;
import com.sergiofreire.xray.tutorials.springboot.data.User;
import com.sergiofreire.xray.tutorials.springboot.services.UserService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateDTO userDTO) {
        // Create entity from DTO (only safe fields)
        User user = new User(userDTO.getName(), userDTO.getUsername(), userDTO.getPassword());
        User saved = userService.save(user);
        return new ResponseEntity<>(toResponseDTO(saved), HttpStatus.CREATED);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable(value = "id") Long id)
            throws ResourceNotFoundException {
        User user = userService.getUserDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for id: " + id));
        return ResponseEntity.ok().body(toResponseDTO(user));
    }

    @GetMapping(path="/users" )
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> deleteUserById(@PathVariable(value = "id") Long id)
            throws ResourceNotFoundException {
        User user = userService.getUserDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for id: " + id));
        userService.deleteUser(user);
        return ResponseEntity.ok().body(toResponseDTO(user)); 
    }

    /**
     * Convert User entity to UserResponseDTO, excluding sensitive fields like password
     */
    private UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(user.getId(), user.getName(), user.getUsername());
    }

}
