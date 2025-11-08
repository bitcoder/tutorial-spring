package com.sergiofreire.xray.tutorials.springboot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;

/* load the full application */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IndexControllerIT {

	@Autowired
	private TestRestTemplate template;

    @Test
    void getWelcomeMessage() throws Exception {
        ResponseEntity<String> response = template.getForEntity("/", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).toString().equals("text/html;charset=UTF-8");
        // che
        assertThat(response.getBody()).contains("Welcome to this amazing website!");
    }
}