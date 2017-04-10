package dmv.spring.demo.rest.security;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.repository.UserRepository;
import dmv.spring.demo.rest.TestHelpers;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public abstract class UserRestSecurityTest implements TestHelpers {

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
		user = prepareUser();
		user = repository.create(user);
		userId = user.getId();
	}

	@After
	public void cleanUp() {
		deleteUser(repository);
	}

	/*
	 * These tests are more like integrity tests, so they go
	 * up to the real repository and fetch users data there,
	 * then authenticate and run it through all custom rules
	 * and exception handlers.
	 */

	@Test
	public void getRole() throws Exception {
		performWith(createGetRequest(MAP_ROLES + USR_ROLE));
	}

	@Test
	public void getRoleUsers() throws Exception {
		performWith(createGetRequest(MAP_ROLES + USR_ROLE_USERS));
	}

	@Test
	public void getUserById() throws Exception {
		performWith(createGetRequest(MAP_USERS + "/" + userId));
	}

	@Test
	public void getUserByEmail() throws Exception {
		performWith(createGetRequest(MAP_USERS + USER_EMAIL_QUERY));
	}

	@Test
	public void createUser() throws Exception {
		// Make sure user does not exist
		cleanUp();
		performWith(post(MAP_USERS)
		        .contentType(APPLICATION_JSON)
				.content(prepareJson(user))
				.accept(APPLICATION_JSON));
	}

	@Test
	public void updateUser() throws Exception {
		user.setFirstName("modified");
		performWith(put(MAP_USERS + "/" + userId)
		        .contentType(APPLICATION_JSON)
				.content(prepareJson(user))
				.accept(APPLICATION_JSON));
	}

	@Test
	public void deleteUser() throws Exception {
		mockMvc.perform(delete(MAP_USERS + "/" + userId))
		       .andDo(print())
		       .andExpect(getStatus());
	}

	/* Helper methods */

	private void performWith(MockHttpServletRequestBuilder request) throws Exception {
		mockMvc.perform(request)
	       .andExpect(content().contentType(APPLICATION_JSON_UTF8))
	       .andDo(print())
	       .andExpect(getStatus());
	}
}
