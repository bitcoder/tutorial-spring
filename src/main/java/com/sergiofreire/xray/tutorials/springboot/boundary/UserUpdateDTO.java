package com.sergiofreire.xray.tutorials.springboot.boundary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for updating user information.
 * Does not include the ID field to prevent mass assignment vulnerabilities.
 */
public class UserUpdateDTO {

    @Size(min = 2)
    @NotBlank(message = "Name is mandatory")
    private String name;

    @Size(min = 5)
    @NotBlank(message = "Username is mandatory")
    private String username;

    @Size(min = 5)
    @NotBlank(message = "Password is mandatory")
    private String password;

    public UserUpdateDTO() {
    }

    public UserUpdateDTO(String name, String username, String password) {
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
