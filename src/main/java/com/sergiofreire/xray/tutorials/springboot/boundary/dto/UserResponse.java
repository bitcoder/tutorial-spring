package com.sergiofreire.xray.tutorials.springboot.boundary.dto;

import com.sergiofreire.xray.tutorials.springboot.data.User;

/**
 * User representation exposed by the API. Never includes the password.
 */
public record UserResponse(Long id, String name, String username) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getUsername());
    }
}
