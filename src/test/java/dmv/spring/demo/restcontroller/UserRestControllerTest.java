package dmv.spring.demo.restcontroller;

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

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.repository.UserRepository;
import dmv.spring.demo.rest.controller.UserRestController;

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
	private static User userAnonymous;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		userWithRoles = mockUser(1L);
		userAnonymous = mockUser(2L);
		
		Set<Role> roles = new HashSet<>();
		roles.add(new Role("ADM", "Administrator"));
		roles.add(new Role("USR", "User"));
		when(userWithRoles.getRoles()).thenReturn(roles);
	}

	@Before
	public void setUp() {
		configureRepository();
	}

	@Test
	public void getUserById() throws Exception {
		String selfLink = MAP + userWithRoles.getId();
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
	public void getUserByEmail() throws Exception {
		String url = MAP + "?email=" + userWithRoles.getEmail();
		String selfLink = MAP + userWithRoles.getId();
		mockMvc.perform(get(url)
				       .accept(APPLICATION_JSON))
		       .andExpect(status().isOk())
		       .andExpect(content().contentType(contentType))
		       .andDo(print())
		       .andExpect(jsonPath("$.email", is(userWithRoles.getEmail())))
		       .andExpect(jsonPath("$.firstName", is(userWithRoles.getFirstName())))
		       .andExpect(jsonPath("$._links[*].href", hasItem(endsWith(selfLink))));
	}

	@Ignore
	@Test
	@WithAnonymousUser
	public void testAnonym() {
		
		//TODO implement anonymous test
	}

	private static User mockUser(long id) {
		User user = Mockito.mock(User.class);
		when(user.getId()).thenReturn(id);
		when(user.getEmail()).thenReturn("user" + id + "@mail");
		when(user.getFirstName()).thenReturn("Name" + id);
		when(user.getLastName()).thenReturn("Surname" + id);
		return user;
	}

	private void configureRepository() {
		when(userRepository.findById(userWithRoles.getId()))
		.thenReturn(userWithRoles);
		when(userRepository.findByEmail(userWithRoles.getEmail()))
		.thenReturn(userWithRoles);
		when(userRepository.findById(userAnonymous.getId()))
		.thenReturn(userAnonymous);
		when(userRepository.findByEmail(userAnonymous.getEmail()))
		.thenReturn(userAnonymous);
	}

}
