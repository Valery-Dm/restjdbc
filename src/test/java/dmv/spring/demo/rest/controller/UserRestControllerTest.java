package dmv.spring.demo.rest.controller;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.util.UriUtils.encode;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.exceptions.EntityAlreadyExistsException;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;
import dmv.spring.demo.model.repository.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username="admin",roles={"USER","ADMIN"})
public class UserRestControllerTest {

	private static final String MAP = "/rest/users/";
	private static final MediaType contentType =
			new MediaType(APPLICATION_JSON.getType(),
            APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserRepository userRepository;

	@InjectMocks
	private UserRestController target;

	private static User userWithRoles;
	private static User userWithoutRoles;
	private static User userNotExisted;
	private static User userUpdated;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		createUsers();
	}

	@Before
	public void setUp() throws Exception {
		configureRepository();
	}

	@Test
	public void getUserById() throws Exception {
		String selfLink = buildURL(userWithRoles.getId());
		mockMvc.perform(get(selfLink)
				       .accept(APPLICATION_JSON))
		       .andExpect(status().isOk())
		       .andExpect(content().contentType(contentType))
		       .andDo(print())
		       .andExpect(jsonPath("$.email", is(userWithRoles.getEmail())))
		       .andExpect(jsonPath("$.firstName", is(userWithRoles.getFirstName())))
		       .andExpect(jsonPath("$._links[*].href", hasItem(endsWith(selfLink))));
	}

	@Test
	public void getNotExistedUserById() throws Exception {
		String selfLink = buildURL(userNotExisted.getId());
		mockMvc.perform(get(selfLink)
				       .accept(APPLICATION_JSON))
		       .andExpect(status().isNotFound())
		       .andExpect(content().contentType(contentType))
		       .andDo(print());
	}

	@Test
	public void getUserByEmail() throws Exception {
		String selfLink = buildURL(userWithRoles.getId());
		mockMvc.perform(get(buildURL(userWithRoles.getEmail()))
				       .accept(APPLICATION_JSON))
		       .andExpect(status().isOk())
		       .andExpect(content().contentType(contentType))
		       .andDo(print())
		       .andExpect(jsonPath("$.email", is(userWithRoles.getEmail())))
		       .andExpect(jsonPath("$.firstName", is(userWithRoles.getFirstName())))
		       .andExpect(jsonPath("$._links[*].href", hasItem(endsWith(selfLink))));
	}

	@Test
	public void getNotExistedUserByEmail() throws Exception {
		mockMvc.perform(get(buildURL(userNotExisted.getEmail()))
				       .accept(APPLICATION_JSON))
		       .andExpect(status().isNotFound())
		       .andExpect(content().contentType(contentType))
		       .andDo(print());
	}

	@Test
	public void deleteUser() throws Exception {
		mockMvc.perform(delete(buildURL(userWithRoles.getId()))
				       .accept(APPLICATION_JSON))
		       .andExpect(status().isNoContent())
		       .andDo(print());
	}

	@Test
	public void deleteNotExistedUser() throws Exception {
		mockMvc.perform(delete(buildURL(userNotExisted.getId()))
				       .accept(APPLICATION_JSON))
		       .andExpect(status().isNotFound())
		       .andDo(print());
	}

	@Test
	public void updateUser() throws Exception {
		when(userRepository.update(any(User.class)))
		.thenReturn(userUpdated);
		mockMvc.perform(put(buildURL(userWithRoles.getId()))
				       .accept(APPLICATION_JSON)
				       .contentType(APPLICATION_JSON)
				       .content(createJson(userUpdated)))
		       .andExpect(status().isCreated())
		       .andExpect(content().contentType(contentType))
		       .andDo(print());
	}

	@Test
	public void updateNotExistedUser() throws Exception {
		when(userRepository.update(any(User.class)))
		.thenThrow(new EntityDoesNotExistException());
		mockMvc.perform(put(buildURL(userNotExisted.getId()))
				       .accept(APPLICATION_JSON)
				       .contentType(APPLICATION_JSON)
				       .content(createJson(userNotExisted)))
		       .andExpect(status().isNotFound())
		       .andDo(print());
	}

	@Test
	public void createNewUser() throws Exception {
		when(userRepository.create(any(User.class)))
		.thenReturn(userNotExisted);
		mockMvc.perform(post(buildURL(""))
				       .accept(APPLICATION_JSON)
				       .contentType(APPLICATION_JSON)
				       .content(createJson(userNotExisted)))
		       .andExpect(status().isCreated())
		       .andExpect(content().contentType(contentType))
		       .andDo(print());
	}

	@Test
	public void createNewUserIncomplete() throws Exception {
		doThrow(MethodArgumentNotValidException.class)
		.when(userRepository).create(any(User.class));
		mockMvc.perform(post(buildURL(""))
				       .accept(APPLICATION_JSON)
				       .contentType(APPLICATION_JSON)
				       .content(createJson(userNotExisted)))
		       .andExpect(status().isBadRequest())
		       .andDo(print());
	}

	@Test
	public void createExistedUser() throws Exception {
		when(userRepository.create(any(User.class)))
		.thenThrow(new EntityAlreadyExistsException("User is already there"));
		mockMvc.perform(post(buildURL(""))
				       .accept(APPLICATION_JSON)
				       .contentType(APPLICATION_JSON)
				       .content(createJson(userUpdated)))
		       .andExpect(status().isConflict())
		       .andExpect(content().contentType(contentType))
		       .andDo(print());
	}

	@Ignore
	@Test
	@WithAnonymousUser
	public void testAnonym() {

		//TODO implement anonymous test (security tests)
	}


	/*     Helper methods     */

	private String buildURL(Long id) {
		return MAP + id;
	}

	private String buildURL(String email) throws UnsupportedEncodingException {
		return MAP + "?email=" + encodeEmail(email);
	}

	private String encodeEmail(String email) throws UnsupportedEncodingException {
		return encode(email, "UTF-8");
	}

	private String createJson(User user) {
		StringBuilder builder = new StringBuilder("{\n");
		builder.append("\"id\" : \"")
		       .append(user.getId())
		       .append("\",\n\"email\" : \"")
		       .append(user.getEmail())
			   .append("\",\n\"firstName\" : \"")
			   .append(user.getFirstName())
			   .append("\",\n\"lastName\" : \"")
			   .append(user.getLastName())
			   .append("\",\n\"middleName\" : \"")
			   .append(user.getMiddleName())
			   .append("\",\n\"password\" : \"")
			   .append(user.getPassword())
			   .append("\",\n\"roles\" : [ ");
		Set<Role> roles = user.getRoles();
		if (roles != null) {
			roles.forEach(role -> {
				builder.append("{\n\"shortName\" : \"")
				.append(role.getShortName())
				.append("\",\n\"fullName\" : \"")
				.append(role.getFullName())
				.append("\"\n},");
			});
		}
		builder.replace(builder.length() - 1, builder.length(), "]}");
		return builder.toString();
	}

	private static User mockUser(long id) {
		User user = new User();
		user.setId(id);
		user.setEmail("user" + id + "@mail");
		user.setFirstName("Name" + id);
		user.setLastName("Surname" + id);
		return user;
	}

	private static void createUsers() {
			userWithRoles = mockUser(1L);
			userUpdated = mockUser(1L);
			userWithoutRoles = mockUser(2L);
			userNotExisted = mockUser(3L);

			Set<Role> roles = new HashSet<>();
			roles.add(new Role("ADM", "Administrator"));
			roles.add(new Role("USR", "User"));
	//		when(userWithRoles.getRoles()).thenReturn(roles);
			userWithRoles.setRoles(roles);

			Set<Role> updateRoles = new HashSet<>();
			updateRoles.add(new Role("ADM", "Administrator"));
			userUpdated.setRoles(updateRoles);
			userUpdated.setMiddleName("MiddleName");
		}

	private void configureRepository() throws Exception {
		when(userRepository.findById(userWithRoles.getId()))
		.thenReturn(userWithRoles);
		when(userRepository.findByEmail(userWithRoles.getEmail()))
		.thenReturn(userWithRoles);

		when(userRepository.findById(userWithoutRoles.getId()))
		.thenReturn(userWithoutRoles);
		when(userRepository.findByEmail(userWithoutRoles.getEmail()))
		.thenReturn(userWithoutRoles);

		when(userRepository.findById(userNotExisted.getId()))
		.thenThrow(new EntityDoesNotExistException());
		when(userRepository.findByEmail(userNotExisted.getEmail()))
		.thenThrow(new EntityDoesNotExistException());

		doNothing().when(userRepository).delete(any(User.class));
	}

}
