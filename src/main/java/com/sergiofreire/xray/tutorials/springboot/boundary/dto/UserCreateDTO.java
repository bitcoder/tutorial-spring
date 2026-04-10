package com.sergiofreire.xray.tutorials.springboot.boundary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for creating new users.
 * This DTO only exposes safe fields that clients are allowed to set.
 */
public class UserCreateDTO {

    @Size(min = 2)
    @NotBlank(message = "Name is mandatory")
    private String name;

    @Size(min = 5)
    @NotBlank(message = "Username is mandatory")
    private String username;

    @Size(min = 5)
    @NotBlank(message = "Password is mandatory")
    private String password;

    public UserCreateDTO() {
    }

    public UserCreateDTO(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
