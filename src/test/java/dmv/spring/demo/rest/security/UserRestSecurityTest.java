package dmv.spring.demo.rest.security;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;
import dmv.spring.demo.model.repository.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public abstract class UserRestSecurityTest {

	private static final String MAP = "/rest/users";
	private static final String USER_EMAIL = "test.user@demo";
	private static final String USER_EMAIL_QUERY = "/?email=" + USER_EMAIL;
	private Long userId;
	private User user;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository repository;

	// To get HttpStatus from child classes where users
	// have assigned different roles
	abstract protected ResultMatcher getStatus();

	@Before
	public void setUp() {
		user = new User();
		user.setEmail(USER_EMAIL);
		user.setFirstName("test");
		user.setLastName("test");
		user = repository.create(user);
		userId = user.getId();
	}

	@After
	public void cleanUp() {
		try {
			repository.delete(
					repository.findByEmail(USER_EMAIL));
		} catch (EntityDoesNotExistException e) {
			// we are good
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * These tests are more like integrity tests, so they go
	 * up to the real repository and fetch users data there,
	 * then authenticate and run it through all custom rules
	 * and exception handlers.
	 */

	@Test
	public void getUserById() throws Exception {
		performWith(createGetRequest("/" + userId));
	}

	@Test
	public void getUserByEmail() throws Exception {
		performWith(createGetRequest(USER_EMAIL_QUERY));
	}

	@Test
	public void createUser() throws Exception {
		// Make sure user does not exist
		cleanUp();
		performWith(post(MAP)
		        .contentType(APPLICATION_JSON)
				.content(createJson(user))
				.accept(APPLICATION_JSON));
	}

	@Test
	public void updateUser() throws Exception {
		user.setFirstName("modified");
		performWith(put(MAP + "/" + userId)
		        .contentType(APPLICATION_JSON)
				.content(createJson(user))
				.accept(APPLICATION_JSON));
	}

	@Test
	public void deleteUser() throws Exception {
		mockMvc.perform(delete(MAP + "/" + userId))
		       .andDo(print())
		       .andExpect(getStatus());
	}

	private String createJson(User user) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(user);
	}

	private MockHttpServletRequestBuilder createGetRequest(String path) {
		return get(MAP + path).accept(APPLICATION_JSON);
	}

	private void performWith(MockHttpServletRequestBuilder request) throws Exception {
		mockMvc.perform(request)
	       .andExpect(content().contentType(APPLICATION_JSON_UTF8))
	       .andDo(print())
	       .andExpect(getStatus());
	}
}
