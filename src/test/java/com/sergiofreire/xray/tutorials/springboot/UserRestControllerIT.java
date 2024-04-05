package com.sergiofreire.xray.tutorials.springboot;

import org.json.JSONException;
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
    public void resetDb() {
        repository.deleteAll();
        user1 = repository.save(new User("Sergio Freire", "sergiofreire", "dummypassword"));
    }

    @Test
    @Requirement("ST-2")
    void createUserWithSuccess() {
        User john = new User("John Doe", "johndoe", "dummypassword");
        ResponseEntity<User> entity = restTemplate.postForEntity("/api/users", john, User.class);

        List<User> foundUsers = repository.findAll();
        assertThat(foundUsers).extracting(User::getUsername).contains("johndoe");
    }

    @Test
    @Requirement("ST-2")
    void dontCreateUserForInvalidData() {
        User john = new User("John Doe", "", "dummypassword");
        ResponseEntity<User> response = restTemplate.postForEntity("/api/users", john, User.class);
 
        // ideally, the server shouldnt return 500, but 400 (bad request)
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
 
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

        ResponseEntity<User> response = restTemplate.exchange(endpoint, HttpMethod.GET, null, new ParameterizedTypeReference<User>() {
        });
        User user = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(user1).isEqualTo(user);
    }

    @Test
    @Requirement("ST-2")
    void getUserUnsuccess() throws JSONException {
        ResponseEntity<JSONObject> response = restTemplate.exchange("/api/user/-1", HttpMethod.GET, null, new ParameterizedTypeReference<JSONObject>() {
        });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    

    @Test
    @Requirement("ST-2")
    void listAllUsersWithSuccess()  {
        createTempUser("Amanda James", "amanda", "dummypassword");
        createTempUser("Robert Junior", "robert", "dummypassword");

        ResponseEntity<List<User>> response = restTemplate
                .exchange("/api/users", HttpMethod.GET, null, new ParameterizedTypeReference<List<User>>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).extracting(User::getName).containsExactly("Sergio Freire", "Amanda James", "Robert Junior");
    }

    @Test
    @Requirement("ST-2")
    void deleteUserWithSuccess() {
        ResponseEntity<User> response = restTemplate.exchange("/api/users/" + user1.getId(), HttpMethod.DELETE, null, User.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Sergio Freire");

        List<User> found = repository.findAll();
        assertThat(found).isEmpty();
    }

    @Test
    @Requirement("ST-2")
    void deleteUserUnsuccess() {
        ResponseEntity<User> response = restTemplate.exchange("/api/users/" + (user1.getId()+2), HttpMethod.DELETE, null, User.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        List<User> found = repository.findAll();
        assertThat(found).hasSize(1);
    }

    private void createTempUser(String name, String username, String password) {
        User user = new User(name, username, password);
        repository.saveAndFlush(user);
    }

}
