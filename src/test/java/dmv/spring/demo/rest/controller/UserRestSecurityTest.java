package dmv.spring.demo.rest.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserRestSecurityTest {

	private static final String MAP = "/rest/users/";
	private static final MediaType contentType =
			new MediaType(APPLICATION_JSON.getType(),
            APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

	@Autowired
	private MockMvc mockMvc;
	
//	@Autowired
//	private UserRepository userRepository;
	
	private static User admin;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		admin = new User();
		admin.setEmail("some.admin@mail");
		admin.setFirstName("ADMIN");
		List<String> roles = Arrays.asList("ADM", "USR");
		admin.setRoles(roles.stream()
		     .map(shortName -> new Role(shortName, null))
		     .collect(Collectors.toSet()));
		
//		userRepository.create(admin);
	}
	
	@AfterClass
	public static void cleanUp() {
//		userRepository.delete(admin);
	}
	
	@Test
	@WithUserDetails("demo.admin@spring.demo")
	public void testAdmin() throws Exception {
		MockHttpServletRequestBuilder request = 
				get(MAP + 3)
				.with(csrf());
		mockMvc.perform(request)
		       .andExpect(status().is2xxSuccessful());
	}

	@Test
	@WithUserDetails("demo.developer@spring.demo")
	public void testNonAdmin() throws Exception {
		MockHttpServletRequestBuilder request = 
				get(MAP + 3)
				.with(csrf());
		mockMvc.perform(request)
		       .andExpect(status().is4xxClientError());
	}
	
	@Test
	@WithMockUser
	public void testAdminWrongPassword() throws Exception {
		MockHttpServletRequestBuilder request = 
				get(MAP + 3)
				.with(csrf());
		mockMvc.perform(request)
		       .andExpect(status().is4xxClientError());
	}

}
