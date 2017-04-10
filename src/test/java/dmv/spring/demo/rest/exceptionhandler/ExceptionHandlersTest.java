package dmv.spring.demo.rest.exceptionhandler;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.exceptions.AccessDataBaseException;
import dmv.spring.demo.model.exceptions.EntityAlreadyExistsException;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;
import dmv.spring.demo.model.repository.UserRepository;
import dmv.spring.demo.rest.TestHelpers;
import dmv.spring.demo.rest.controller.UserRestController;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", authorities = { "ADM", "USR" })
public class ExceptionHandlersTest implements TestHelpers {

	private static final String ERROR_MSG = "test error message";
	private static final long USER_ID = 1;
	private User user;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserRepository userRepository;

	@InjectMocks
	private UserRestController target;

	@Before
	public void setUp() {
		user = prepareUser();
	}

	/*
	 * Some of these tests seem redundant as they already
	 * been tested in other unit tests.
	 * Yet I prefer to have them all in one place.
	 * Here I'm focused on various exceptions handling.
	 */

	@Test
	public void userIdDoesNotExist() {
		when(userRepository.findById(USER_ID))
		.thenThrow(new EntityDoesNotExistException(ERROR_MSG));

		perform(get(buildURL(USER_ID)),
				status().isNotFound(), ERROR_MSG);
	}

	@Test
	public void noHandlerFound() {
		perform(get("some/nonexisting/path"),
				status().isNotFound(), "No handler found");
	}

	@Test
	public void notSupportedMethod() {
		perform(post(buildURL(USER_ID)),
				status().isBadRequest(), "not supported");
	}

	@Test
	public void entityAlreadyExists() {
		when(userRepository.create(user))
		.thenThrow(new EntityAlreadyExistsException(ERROR_MSG));

		performWithContent(post(MAP_USERS),
				   prepareJson(user),
			       status().isConflict(),
			       ERROR_MSG);
	}

	@Test
	public void wrongMediaType() {
		perform(post(MAP_USERS)
				// media type not specified
				.content(prepareJson(user)),
				status().isBadRequest(), "not supported");
	}

	@Test
	public void accessDatabase() {
		when(userRepository.findById(USER_ID))
		.thenThrow(new AccessDataBaseException(ERROR_MSG));
		// Should not expose internal error message
		perform(get(buildURL(USER_ID)),
				status().is5xxServerError(), "try again later");
	}

	@Test
	public void sqlError() throws SQLException {
		doThrow(SQLException.class)
		.when(userRepository).findById(USER_ID);
		perform(get(buildURL(USER_ID)),
				status().is5xxServerError(), "try again later");
	}

	@Test
	public void unknownError() throws Exception {
		doThrow(Exception.class)
		.when(userRepository).findById(USER_ID);
		perform(get(buildURL(USER_ID)),
				status().is5xxServerError(), "try again later");
	}

	@Test
	public void httpinputIllegalArgument() {
		perform(get(MAP_USERS + "/?email=mail@1%%%@"),
				status().isBadRequest(), "Invalid encoded sequence");
	}

	@Test
	public void malformedJSON() throws Exception {
		String preparedJson = prepareJson(user);
		// corrupt it
		int length = preparedJson.length();
		preparedJson = preparedJson.substring(length - 2, length);

		performWithContent(post(MAP_USERS),
				   preparedJson,
			       status().isBadRequest(),
				   "Unrecognized");
	}

	@Test
	public void incompleteUserInfo() throws Exception {
		user.setFirstName(null);
		String preparedJson = prepareJson(user);
		performWithContent(post(MAP_USERS),
						   preparedJson,
					       status().isBadRequest(),
						   "may not be null");
	}

	@Test
	public void argumentNotValid() {
		performWithContent(post(MAP_USERS),
				   "{\"other\": \"json\"}",
			       status().isBadRequest(),
				   "may not be null");
	}

	@Test
	public void httpNotReadable() {
		performWithContent(post(MAP_USERS),
			       "not a Json",
			       status().isBadRequest(),
				   "Unrecognized");
	}

	/* Helper methods */

	private void performWithContent(MockHttpServletRequestBuilder requestBuilder,
			                        String preparedJson, ResultMatcher status,
									String errorMsg) {
		try {
			mockMvc.perform(requestBuilder
					.contentType(MediaType.APPLICATION_JSON)
					.content(preparedJson))
			//.andDo(print())
			.andExpect(status)
			.andExpect(jsonPath("$.ex", containsString(errorMsg)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void perform(MockHttpServletRequestBuilder requestBuilder,
			ResultMatcher status,
			String errorMsg) {
		try {
			mockMvc.perform(requestBuilder)
			.andExpect(status)
			.andExpect(jsonPath("$.ex", containsString(errorMsg)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
