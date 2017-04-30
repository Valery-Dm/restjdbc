package dmv.spring.demo.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.web.util.UriUtils.encode;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;
import dmv.spring.demo.model.repository.UserRepository;

/**
 * A bunch of helper methods for testing
 *
 * @author dmv
 */
public interface TestHelpers {

	static final String MAP_USERS = "/rest/users";
	static final String MAP_ROLES = "/rest/roles";
	static final String USERS = "/users";
	static final String USR_ROLE = "/USR";
	static final String USR_ROLE_USERS = "/USR/users";
	static final String USER_EMAIL = "test.user@demo";
	static final String USER_EMAIL_QUERY = "/?email=" + USER_EMAIL;

	default String buildURL(Long id) {
		return MAP_USERS + "/" + id;
	}

	default String buildURL(String email) throws UnsupportedEncodingException {
		return MAP_USERS + "?email=" + encode(email, "UTF-8");
	}

	default User prepareUser() {
		User user = new User();
		user.setEmail(USER_EMAIL);
		user.setFirstName("First");
		user.setLastName("Last");
		user.setMiddleName("Middle");
		user.setPassword("123456");
		return user;
	}

	default User prepareUserWithRoles() {
		User user = new User();
		user.setEmail(USER_EMAIL);
		user.setFirstName("First");
		user.setLastName("Last");
		user.setMiddleName("Middle");
		user.setPassword("123456");

		Set<Role> roles = new HashSet<>();
		Role usrRole = new Role("USR", "User");
		roles.add(usrRole);
		Role devRole = new Role("DEV", "Developer");
		roles.add(devRole);
		user.setRoles(roles);

		return user;
	}

	default void deleteUser(UserRepository repository) {
		try {
			repository.delete(
					repository.findByEmail(USER_EMAIL));
		} catch (EntityDoesNotExistException e) {
			// we are good
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	default String prepareJson(User user) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(user);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

	default MockHttpServletRequestBuilder createGetRequest(String path) {
		return get(path).accept(APPLICATION_JSON);
	}
}
