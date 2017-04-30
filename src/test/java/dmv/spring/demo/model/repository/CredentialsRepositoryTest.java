package dmv.spring.demo.model.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CredentialsRepositoryTest {
	
	@Autowired
	private CredentialsRepository target;
	@Autowired
	private UserRepository userRepository;
	
	private User user;
	private String email;
	private Set<Role> roles;
	private Role admRole;
	private Role usrRole;
	private Role devRole;
	
	@Before
	public void setUp() throws Exception {
		email = "test.user@mail.address";

		user = new User();
		user.setEmail(email);
		user.setFirstName("First");
		user.setLastName("Last");
		user.setMiddleName("Middle");
		user.setPassword("123456");

		roles = new HashSet<>();
		admRole = new Role("ADM", "Administrator");
		roles.add(admRole);
		usrRole = new Role("USR", "User");
		roles.add(usrRole);
		devRole = new Role("DEV", "Developer");
		roles.add(devRole);
		user.setRoles(roles);
		
		user = userRepository.create(user);
	}

	@After
	public void cleanUp() {
		/*
		 * These tests were done 'on best effort'.
		 * We are not expecting test.user to be
		 * existed in database at this point.
		 * And it should not remain after.
		 * So, trying to remove.
		 */
		try {
			User testUser = userRepository.findByEmail(email);
			userRepository.delete(testUser);
		} catch (EntityDoesNotExistException e) {
			// we are good
		} catch (Exception e) {
			System.out.println("unsuccessful cleanup%n" + e.getMessage());
		}
	}
	@Test
	public void getCredentials() {
		User credentials = target.getCredentials(user.getEmail());
		assertNotNull(credentials.getPassword());
		assertThat(credentials.getEmail(), is(user.getEmail()));
	}

	@Test(expected=EntityDoesNotExistException.class)
	public void getCredentialsNotExist() {
		target.getCredentials(user.getEmail());
	}

}
