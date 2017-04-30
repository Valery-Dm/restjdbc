package dmv.spring.demo.rest.controller;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.exceptions.EntityAlreadyExistsException;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;
import dmv.spring.demo.model.repository.UserRepository;
import dmv.spring.demo.rest.TestHelpers;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", authorities = { "ADM", "USR" })
public class UserRestControllerTest implements TestHelpers {

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
		perform(get(selfLink), status().isOk())
		       .andExpect(jsonPath("$.email", is(userWithRoles.getEmail())))
		       .andExpect(jsonPath("$.firstName", is(userWithRoles.getFirstName())))
		       .andExpect(jsonPath("$._links[*].href", hasItem(endsWith(selfLink))));
	}

	@Test
	public void getNotExistedUserById() throws Exception {
		String selfLink = buildURL(userNotExisted.getId());
		perform(get(selfLink), status().isNotFound());
	}

	@Test
	public void getUserByEmail() throws Exception {
		String selfLink = buildURL(userWithRoles.getId());
		perform(get(buildURL(userWithRoles.getEmail())), status().isOk())
		       .andExpect(jsonPath("$.email", is(userWithRoles.getEmail())))
		       .andExpect(jsonPath("$.firstName", is(userWithRoles.getFirstName())))
		       .andExpect(jsonPath("$._links[*].href", hasItem(endsWith(selfLink))));
	}

	@Test
	public void getNotExistedUserByEmail() throws Exception {
		perform(get(buildURL(userNotExisted.getEmail())), status().isNotFound());
	}

	@Test
	public void deleteUser() throws Exception {
		performNoContent(delete(buildURL(userWithRoles.getId())), status().isNoContent());
	}

	@Test
	public void deleteNotExistingUser() throws Exception {
		performNoContent(delete(buildURL(userNotExisted.getId())), status().isNotFound());
	}

	@Test
	public void updateUser() throws Exception {
		when(userRepository.update(any(User.class)))
		.thenReturn(userUpdated);
		performWithContent(put(buildURL(userWithRoles.getId())),
				status().isCreated(), prepareJson(userUpdated));
	}

	@Test
	public void updateNotExistedUser() throws Exception {
		when(userRepository.update(any(User.class)))
		.thenThrow(new EntityDoesNotExistException());
		performWithContent(put(buildURL(userNotExisted.getId())),
				status().isNotFound(), prepareJson(userNotExisted));
	}

	@Test
	public void createNewUser() throws Exception {
		when(userRepository.create(any(User.class)))
		.thenReturn(userNotExisted);
		performWithContent(post(buildURL("")),
				status().isCreated(), prepareJson(userNotExisted));
	}

	@Test
	public void createExistedUser() throws Exception {
		when(userRepository.create(any(User.class)))
		.thenThrow(new EntityAlreadyExistsException("User is already there"));
		performWithContent(post(buildURL("")),
				status().isConflict(), prepareJson(userUpdated));
	}

	/*     Helper methods     */

	private static User mockUser(long id) {
		User user = new User();
		user.setId(id);
		user.setEmail("user" + id + "@mail");
		user.setFirstName("Name" + id);
		user.setLastName("Surname" + id);
		return user;
	}

	private ResultActions perform(MockHttpServletRequestBuilder request,
			                      ResultMatcher status) throws Exception {
		return mockMvc.perform(request.accept(APPLICATION_JSON))
					//.andDo(print())
					.andExpect(status)
				    .andExpect(content().contentType(APPLICATION_JSON_UTF8));
	}

	private ResultActions performWithContent(MockHttpServletRequestBuilder request,
			ResultMatcher status, String content) throws Exception {
		return mockMvc.perform(request.accept(APPLICATION_JSON)
				                      .contentType(APPLICATION_JSON)
			                          .content(content))
				//.andDo(print())
				.andExpect(status);
	}

	private ResultActions performNoContent(MockHttpServletRequestBuilder request,
			ResultMatcher status) throws Exception {
		return mockMvc.perform(request.accept(APPLICATION_JSON))
				//.andDo(print())
				.andExpect(status);
	}

	private static void createUsers() {
			userWithRoles = mockUser(111L);
			userUpdated = mockUser(112L);
			userWithoutRoles = mockUser(113L);
			userNotExisted = mockUser(-114L);

			Set<Role> roles = new HashSet<>();
			roles.add(new Role("ADM", "Administrator"));
			roles.add(new Role("USR", "User"));
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
		doThrow(new EntityDoesNotExistException())
		.when(userRepository).deleteById(userNotExisted.getId());
		when(userRepository.findByEmail(userNotExisted.getEmail()))
		.thenThrow(new EntityDoesNotExistException());

		doNothing().when(userRepository).delete(any(User.class));
	}

}
