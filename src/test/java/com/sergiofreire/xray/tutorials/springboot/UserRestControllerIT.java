package com.sergiofreire.xray.tutorials.springboot;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import com.sergiofreire.xray.tutorials.springboot.boundary.dto.UserCreateDTO;
import com.sergiofreire.xray.tutorials.springboot.boundary.dto.UserResponseDTO;
import com.sergiofreire.xray.tutorials.springboot.data.User;
import com.sergiofreire.xray.tutorials.springboot.data.UserRepository;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

/* @SpringBootTest loads the full application, including the web server
 * @AutoConfigureTestDatabase is used to configure a test database instead of the application-defined database
*/
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
class UserRestControllerIT {

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
    @Requirement("ST-2")
    void createUserWithSuccess() {
        UserCreateDTO john = new UserCreateDTO("John Doe", "johndoe", "dummypassword");
        ResponseEntity<UserResponseDTO> response = restTemplate.postForEntity("/api/users", john, UserResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getUsername()).isEqualTo("johndoe");
        
        List<User> foundUsers = repository.findAll();
        assertThat(foundUsers).extracting(User::getUsername).contains("johndoe");
    }

    @Test
    @Requirement("ST-2")
    void dontCreateUserForInvalidData() {
        UserCreateDTO john = new UserCreateDTO("John Doe", "", "dummypassword");
        ResponseEntity<UserResponseDTO> response = restTemplate.postForEntity("/api/users", john, UserResponseDTO.class);
 
        // With @Valid annotation, this should now return 400 (bad request) instead of 500
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
 
        List<User> found = repository.findAll();
        assertThat(found).hasSize(1);
        assertThat(found).extracting(User::getName).doesNotContain("John Doe");
    }

    @Test
    @Requirement("ST-2")
    void getUserWithSuccess() {
        String endpoint = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("127.0.0.1")
                .port(randomServerPort)
                .pathSegment("api", "users", user1.getId().toString() )
                .build()
                .toUriString();

        ResponseEntity<UserResponseDTO> response = restTemplate.exchange(endpoint, HttpMethod.GET, null, new ParameterizedTypeReference<UserResponseDTO>() {
        });
        UserResponseDTO userDTO = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userDTO.getId()).isEqualTo(user1.getId());
        assertThat(userDTO.getName()).isEqualTo(user1.getName());
        assertThat(userDTO.getUsername()).isEqualTo(user1.getUsername());
    }

    @Test
    @Requirement("ST-2")
    void getUserUnsuccess() {
        ResponseEntity<JSONObject> response = restTemplate.exchange("/api/user/-1", HttpMethod.GET, null, new ParameterizedTypeReference<JSONObject>() {
        });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    

    @Test
    @Requirement("ST-2")
    void listAllUsersWithSuccess()  {
        createTempUser("Amanda James", "amanda", "dummypassword");
        createTempUser("Robert Wilson", "robert", "dummypassword");

        ResponseEntity<List<UserResponseDTO>> response = restTemplate
                .exchange("/api/users", HttpMethod.GET, null, new ParameterizedTypeReference<List<UserResponseDTO>>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).extracting(UserResponseDTO::getName).containsExactly("Sergio Freire", "Amanda James", "Robert Wilson");
    }

    @Test
    @Requirement("ST-2")
    void deleteUserWithSuccess() {
        ResponseEntity<UserResponseDTO> response = restTemplate.exchange("/api/users/" + user1.getId(), HttpMethod.DELETE, null, UserResponseDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Sergio Freire");

        List<User> found = repository.findAll();
        assertThat(found).isEmpty();
    }

    @Test
    @Requirement("ST-2")
    void deleteUserUnsuccess() {
        ResponseEntity<UserResponseDTO> response = restTemplate.exchange("/api/users/" + (user1.getId()+2), HttpMethod.DELETE, null, UserResponseDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        List<User> found = repository.findAll();
        assertThat(found).hasSize(1);
    }

    private void createTempUser(String name, String username, String password) {
        User user = new User(name, username, password);
        repository.saveAndFlush(user);
    }

}
