package com.sergiofreire.xray.tutorials.springboot.boundary.dto;

import java.util.Objects;

/**
 * Data Transfer Object for user responses.
 * This DTO excludes sensitive fields like password.
 */
public class UserResponseDTO {

    private Long id;
    private String name;
    private String username;

    public UserResponseDTO() {
    }

    public UserResponseDTO(Long id, String name, String username) {
        this.id = id;
        this.name = name;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserResponseDTO that = (UserResponseDTO) o;
        return Objects.equals(id, that.id) && 
               Objects.equals(name, that.name) && 
               Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, username);
    }
}
