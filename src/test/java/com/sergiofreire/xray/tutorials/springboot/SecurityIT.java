package com.sergiofreire.xray.tutorials.springboot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.sergiofreire.xray.tutorials.springboot.boundary.dto.UserResponseDTO;
import com.sergiofreire.xray.tutorials.springboot.data.User;
import com.sergiofreire.xray.tutorials.springboot.data.UserRepository;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Security tests to verify protection against common vulnerabilities
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
class SecurityIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository repository;

    @BeforeEach
    void resetDb() {
        repository.deleteAll();
    }

    @Test
    @Requirement("ST-336")
    void massAssignmentVulnerabilityIsFixed() {
        // Create a user with a specific ID in the database
        User existingUser = repository.save(new User("Existing User", "existing", "password123"));
        Long existingId = existingUser.getId();

        // Attempt to create a new user with a malicious payload that tries to set the ID
        String maliciousJson = String.format(
            "{\"id\": %d, \"name\": \"Malicious User\", \"username\": \"hacker\", \"password\": \"hacked\"}", 
            existingId
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(maliciousJson, headers);

        ResponseEntity<UserResponseDTO> response = restTemplate.postForEntity("/api/users", request, UserResponseDTO.class);

        // Verify that the request succeeded
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Verify that a NEW user was created, not overwriting the existing one
        List<User> allUsers = repository.findAll();
        assertThat(allUsers).hasSize(2);

        // Verify the existing user was NOT modified
        User stillExisting = repository.findById(existingId).orElseThrow();
        assertThat(stillExisting.getName()).isEqualTo("Existing User");
        assertThat(stillExisting.getUsername()).isEqualTo("existing");

        // Verify the new user has a DIFFERENT ID
        assertThat(response.getBody().getId()).isNotEqualTo(existingId);
        assertThat(response.getBody().getName()).isEqualTo("Malicious User");
    }

    @Test
    @Requirement("ST-336")
    void passwordNotExposedInGetResponses() {
        // Create a user
        User user = repository.save(new User("John Doe", "johndoe", "secretpassword123"));

        // Get the user via the API
        ResponseEntity<UserResponseDTO> response = restTemplate.getForEntity("/api/users/" + user.getId(), UserResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // Verify that the response does NOT contain the password field
        // The UserResponseDTO should only have id, name, and username
        UserResponseDTO userDTO = response.getBody();
        assertThat(userDTO.getId()).isEqualTo(user.getId());
        assertThat(userDTO.getName()).isEqualTo("John Doe");
        assertThat(userDTO.getUsername()).isEqualTo("johndoe");
        
        // Verify the password is NOT in the JSON response by checking the raw response
        String jsonResponse = response.getBody().toString();
        assertThat(jsonResponse).doesNotContain("secretpassword123");
        assertThat(jsonResponse).doesNotContain("password");
    }
}
