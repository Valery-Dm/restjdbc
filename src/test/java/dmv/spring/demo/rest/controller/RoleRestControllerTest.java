package dmv.spring.demo.rest.controller;

import static java.util.Collections.emptySet;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;
import dmv.spring.demo.model.repository.RoleRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username="admin",roles={"USER","ADMIN"})
public class RoleRestControllerTest {

	private static final String MAP = "/rest/roles/";
	private static final String USERS = "/users";
	private static final MediaType contentType =
			new MediaType(APPLICATION_JSON.getType(),
            APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

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
		mockMvc.perform(get(MAP + role.getShortName())
				       .accept(APPLICATION_JSON))
		       .andExpect(status().isOk())
		       .andExpect(content().contentType(contentType))
		       .andDo(print())
		       .andExpect(jsonPath("$.shortName", is(role.getShortName())))
		       .andExpect(jsonPath("$.fullName", is(role.getFullName())))
		       .andExpect(jsonPath("$._links[*].href", hasItem(roleLink)));
	}

	@Test
	public void getAbsentRole() throws Exception {
		mockMvc.perform(get(MAP + roleAbs.getShortName())
			       .accept(APPLICATION_JSON))
	       .andExpect(status().isNotFound())
	       .andExpect(content().contentType(contentType))
	       .andDo(print());
	}

	@Test
	public void getRoleUsers() throws Exception {
		mockMvc.perform(get(MAP + roleAdm.getShortName() + USERS)
			       .accept(APPLICATION_JSON))
	       .andExpect(status().isOk())
	       .andExpect(content().contentType(contentType))
	       .andDo(print());
	}

	@Test
	public void getRoleUsersEmpty() throws Exception {
		mockMvc.perform(get(MAP + roleDev.getShortName() + USERS)
			       .accept(APPLICATION_JSON))
		   .andExpect(status().isNoContent())
	       .andDo(print());
	}

	@Test
	public void getAbsentRoleUsers() throws Exception {
		mockMvc.perform(get(MAP + roleAbs.getShortName() + USERS)
			       .accept(APPLICATION_JSON))
	       .andExpect(status().isNotFound())
	       .andExpect(content().contentType(contentType))
	       .andDo(print());
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
