package com.sergiofreire.xray.tutorials.springboot;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.sergiofreire.xray.tutorials.springboot.boundary.GreetingController;

// @SpringBootTest
// @AutoConfigureMockMvc; it is implied whenever @WebMvcTest is used

@WebMvcTest(GreetingController.class)
public class GreetingControllerMockedIT {

	@Autowired
	private MockMvc mvc;

	@Test
	public void getDefaultGreeting() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/greeting").accept(MediaType.TEXT_PLAIN))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Hello, World!")));
	}

	@Test
	public void getPersonalizedGreeting() throws Exception {
		// perform get request and pass name parameter on the mvc object
		mvc.perform(
				// MockMvcRequestBuilders.get("/greeting?name=John").accept(MediaType.TEXT_PLAIN))
				MockMvcRequestBuilders.get("/greeting").param("name", "John").accept(MediaType.TEXT_PLAIN))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Hello, John!")));
	}
}