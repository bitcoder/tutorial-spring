package com.sergiofreire.xray.tutorials.springboot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.sergiofreire.xray.tutorials.springboot.data.User;
import com.sergiofreire.xray.tutorials.springboot.data.UserRepository;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
class UserUpdateIT {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository repository;

    User user1;

    @BeforeEach
    void resetDb() {
        repository.deleteAll();
        user1 = repository.save(new User("Sergio Freire", "sergiofreire", "dummypassword"));
    }

    @Test
    @Requirement("ST-233")
    void updateUserWithSuccess() {
        User updatedUser = new User("Sergio Updated", "sergioupdated", "newpassword");
        HttpEntity<User> requestEntity = new HttpEntity<>(updatedUser);
        
        ResponseEntity<User> response = restTemplate.exchange(
            "/api/users/" + user1.getId(), 
            HttpMethod.PUT, 
            requestEntity, 
            User.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Sergio Updated");
        assertThat(response.getBody().getUsername()).isEqualTo("sergioupdated");
        assertThat(response.getBody().getPassword()).isEqualTo("newpassword");
        
        // Verify in database
        User dbUser = repository.findById(user1.getId()).orElse(null);
        assertThat(dbUser).isNotNull();
        assertThat(dbUser.getName()).isEqualTo("Sergio Updated");
    }

    @Test
    @Requirement("ST-233")
    void updateUserWithInvalidData() {
        User invalidUser = new User("", "", "");
        HttpEntity<User> requestEntity = new HttpEntity<>(invalidUser);
        
        ResponseEntity<User> response = restTemplate.exchange(
            "/api/users/" + user1.getId(), 
            HttpMethod.PUT, 
            requestEntity, 
            User.class
        );

        // Should return error for invalid data
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        
        // Verify original data is unchanged in database
        User dbUser = repository.findById(user1.getId()).orElse(null);
        assertThat(dbUser).isNotNull();
        assertThat(dbUser.getName()).isEqualTo("Sergio Freire");
    }

    @Test
    @Requirement("ST-233")
    void updateUserNotFound() {
        User updatedUser = new User("New Name", "newusername", "newpassword");
        HttpEntity<User> requestEntity = new HttpEntity<>(updatedUser);
        
        ResponseEntity<User> response = restTemplate.exchange(
            "/api/users/999999", 
            HttpMethod.PUT, 
            requestEntity, 
            User.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
