package dmv.spring.demo.restcontroller;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

import org.junit.AfterClass;
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

import dmv.spring.demo.model.entity.User;
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
	
	private static User user1;
	private static User user2;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		user1 = mockUser(1L);
		user2 = mockUser(2L);
	}

	@Before
	public void setUp() {
		configureRepository();
	}

	@Test
	public void getUserById() throws Exception {
		String selfLink = MAP + user1.getId();
		mockMvc.perform(get(selfLink)
				       .accept(APPLICATION_JSON))
		       .andExpect(status().isOk())
		       .andExpect(content().contentType(contentType))
		       .andDo(print())
		       .andExpect(jsonPath("$.email", is(user1.getEmail())))
		       .andExpect(jsonPath("$.firstName", is(user1.getFirstName())))
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
		when(userRepository.findById(user1.getId()))
		.thenReturn(user1);
		when(userRepository.findById(user2.getId()))
		.thenReturn(user2);
	}

}
