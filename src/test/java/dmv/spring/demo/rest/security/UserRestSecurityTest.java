package dmv.spring.demo.rest.security;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import dmv.spring.demo.model.repository.UserRepository;
import dmv.spring.demo.rest.controller.UserRestController;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public abstract class UserRestSecurityTest {

	private static final String MAP = "/rest/users/";
	private static final String USER_EMAIL = "?email=demo.developer@spring.demo";
	private static final int USER_ID = 3;

	@Autowired
	private MockMvc mockMvc;
	
	// To get HttpStatus from child classes where users
	// have assigned different roles 
	abstract protected ResultMatcher getStatus();
	
	/*
	 * These tests are more like integrity tests, so they go
	 * up to the real repository and fetch users data there,
	 * then authenticate and run it through all custom rules 
	 * and exception handlers.
	 * 
	 * Two test cases here rely on demo users existence.
	 */
	
	@Test
	public void getUserById() throws Exception {
		MockHttpServletRequestBuilder request = 
				get(MAP + USER_ID)
				.accept(APPLICATION_JSON)
				.with(csrf());
		mockMvc.perform(request)
		       .andExpect(content().contentType(APPLICATION_JSON_UTF8))
		       .andExpect(getStatus());
	}

	@Test
	public void getUserByEmail() throws Exception {
		MockHttpServletRequestBuilder request = 
				get(MAP + USER_EMAIL)
				.accept(APPLICATION_JSON)
				.with(csrf());
		mockMvc.perform(request)
		       .andExpect(content().contentType(APPLICATION_JSON_UTF8))
		       .andExpect(getStatus());
	}
}
