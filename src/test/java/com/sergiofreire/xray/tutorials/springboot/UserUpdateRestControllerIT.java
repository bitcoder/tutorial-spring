package com.sergiofreire.xray.tutorials.springboot;

import com.sergiofreire.xray.tutorials.springboot.data.User;
import com.sergiofreire.xray.tutorials.springboot.data.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import app.getxray.xray.junit.customjunitxml.annotations.XrayTest;

@SpringBootTest
@AutoConfigureMockMvc
@Requirement("ST-233")
public class UserUpdateRestControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @XrayTest(key = "ST-234")
    public void testUpdateUser_Success() throws Exception {
        // Create a user first
        User user = new User("John Doe", "johndoe", "password123");
        user = userRepository.save(user);
        Long userId = user.getId();

        // Update the user
        String updateJson = """
                {
                    "name": "Jane Doe",
                    "username": "janedoe",
                    "password": "newpassword456"
                }
                """;

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId.intValue())))
                .andExpect(jsonPath("$.name", is("Jane Doe")))
                .andExpect(jsonPath("$.username", is("janedoe")))
                .andExpect(jsonPath("$.password", is("newpassword456")));

        // Verify the update persisted
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Jane Doe")))
                .andExpect(jsonPath("$.username", is("janedoe")));
    }

    @Test
    @XrayTest(key = "ST-235")
    public void testUpdateUser_NotFound() throws Exception {
        String updateJson = """
                {
                    "name": "Jane Doe",
                    "username": "janedoe",
                    "password": "newpassword456"
                }
                """;

        mockMvc.perform(put("/api/users/{id}", 99999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @XrayTest(key = "ST-236")
    public void testUpdateUser_InvalidData() throws Exception {
        // Create a user first
        User user = new User("John Doe", "johndoe", "password123");
        user = userRepository.save(user);
        Long userId = user.getId();

        // Try to update with invalid data (name too short)
        String updateJson = """
                {
                    "name": "J",
                    "username": "janedoe",
                    "password": "newpassword456"
                }
                """;

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @XrayTest(key = "ST-237")
    public void testUpdateUser_CannotChangeId() throws Exception {
        // Create a user first
        User user = new User("John Doe", "johndoe", "password123");
        user = userRepository.save(user);
        Long userId = user.getId();

        // Try to update with a different ID in the JSON (mass assignment attempt)
        String updateJson = """
                {
                    "id": 99999,
                    "name": "Jane Doe",
                    "username": "janedoe",
                    "password": "newpassword456"
                }
                """;

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId.intValue()))) // ID should remain unchanged
                .andExpect(jsonPath("$.name", is("Jane Doe")));
    }
}
