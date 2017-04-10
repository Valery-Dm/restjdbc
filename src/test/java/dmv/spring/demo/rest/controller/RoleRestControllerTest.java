package dmv.spring.demo.rest.controller;

import static java.util.Collections.emptySet;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;
import dmv.spring.demo.model.repository.RoleRepository;
import dmv.spring.demo.rest.TestHelpers;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", authorities = { "ADM", "USR" })
public class RoleRestControllerTest implements TestHelpers {

	@Autowired
	private MockMvc mockMvc;

	private static Role roleAdm;
	private static Role roleDev;
	private static Role roleAbs;

	private static Set<User> usersAdm;
	private static User userAdm1;
	private static User userAdm2;

	@MockBean
	private RoleRepository roleRepository;

	@InjectMocks
	private RoleRestController target;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		mockRoles();
		mockUsers();
	}

	@Before
	public void setUp() {
		configureRepository();
	}

	@Test
	public void getRole() throws Exception {
		checkGetRole(roleAdm);
		checkGetRole(roleDev);
	}

	private void checkGetRole(Role role) throws Exception {
		Matcher<String> roleLink = endsWith("/" + role.getShortName());
		performGet(rolePath(role.getShortName()), status().isOk())
		       .andExpect(jsonPath("$.shortName", is(role.getShortName())))
		       .andExpect(jsonPath("$.fullName", is(role.getFullName())))
		       .andExpect(jsonPath("$._links[*].href", hasItem(roleLink)));
	}
	
	@Test
	public void getAbsentRole() throws Exception {
		performGet(rolePath(roleAbs.getShortName()), status().isNotFound());
	}
	
	@Test
	public void getRoleUsers() throws Exception {
		performGet(roleUsersPath(roleAdm.getShortName()), status().isOk());
	}

	@Test
	public void getRoleUsersEmpty() throws Exception {
		performGetNoContent(roleUsersPath(roleDev.getShortName()), status().isNoContent());
	}

	@Test
	public void getAbsentRoleUsers() throws Exception {
		performGet(roleUsersPath(roleAbs.getShortName()), status().isNotFound());
	}

	/* Helper methods */
	
	private String roleUsersPath(String shortName) {
		return MAP_ROLES + "/" + shortName + USERS;
	}

	private String rolePath(String shortName) {
		return MAP_ROLES + "/" + shortName;
	}

	private ResultActions performGet(String path, ResultMatcher status) throws Exception {
		return mockMvc.perform(get(path).accept(APPLICATION_JSON))
				//.andDo(print())
				.andExpect(status)
			    .andExpect(content().contentType(APPLICATION_JSON_UTF8));
	}

	private ResultActions performGetNoContent(String path, ResultMatcher status) throws Exception {
		return mockMvc.perform(get(path).accept(APPLICATION_JSON))
				//.andDo(print())
				.andExpect(status);
	}

	private void configureRepository() {
		when(roleRepository.findByShortName(roleAdm.getShortName()))
		.thenReturn(roleAdm);
		when(roleRepository.findByShortName(roleDev.getShortName()))
		.thenReturn(roleDev);
		when(roleRepository.findByShortName(roleAbs.getShortName()))
		.thenThrow(new EntityDoesNotExistException("Test 404 exception"));

		when(roleRepository.getUsers(roleAdm)).thenReturn(usersAdm);
		when(roleRepository.getUsers(roleDev)).thenReturn(emptySet());
		when(roleRepository.getUsers(roleAbs))
		.thenThrow(new EntityDoesNotExistException("Test 404 exception"));
	}

	private static void mockUsers() {
		userAdm1 = Mockito.mock(User.class);
		when(userAdm1.getEmail()).thenReturn("admin1@mail");
		when(userAdm1.getId()).thenReturn(1L);
		userAdm2 = Mockito.mock(User.class);
		when(userAdm2.getEmail()).thenReturn("admin2@mail");
		when(userAdm2.getId()).thenReturn(2L);

		usersAdm = new HashSet<>();
		usersAdm.add(userAdm1);
		usersAdm.add(userAdm2);
	}

	private static void mockRoles() {
		roleAdm = Mockito.mock(Role.class);
		when(roleAdm.getShortName()).thenReturn("ADM");
		when(roleAdm.getFullName()).thenReturn("Administrator");
		roleDev = Mockito.mock(Role.class);
		when(roleDev.getShortName()).thenReturn("DEV");
		when(roleDev.getFullName()).thenReturn("Developer");
		roleAbs = Mockito.mock(Role.class);
		when(roleAbs.getShortName()).thenReturn("ABS");
		when(roleAbs.getFullName()).thenReturn("Absent role");
	}

}
