package com.sergiofreire.xray.tutorials.springboot;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.sergiofreire.xray.tutorials.springboot.boundary.IndexController;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

// @SpringBootTest
// @AutoConfigureMockMvc; it is implied whenever @WebMvcTest is used

// @WebMvcTest annotation is used to test only the web layer of the application
// It disables full auto-configuration and instead apply only configuration relevant to MVC tests
@WebMvcTest(IndexController.class)
class IndexControllerMockedIT {

	@Autowired
	private MockMvc mvc;

	@Test
	@Requirement("ST-1")
	void getWelcomeMessage() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.TEXT_PLAIN))
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo("Welcome to this amazing website!")));
	}
}