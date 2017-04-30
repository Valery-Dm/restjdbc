package dmv.spring.demo.model.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;
import dmv.spring.demo.rest.TestHelpers;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CredentialsRepositoryTest implements TestHelpers {

	@Autowired
	private CredentialsRepository target;
	@Autowired
	private UserRepository userRepository;

	private User user;

	@Before
	public void setUp() throws Exception {
		user = userRepository.create(prepareUserWithRoles());
	}

	@After
	public void cleanUp() {
		deleteUser(userRepository);
	}
	@Test
	public void getCredentials() {
		User credentials = target.getCredentials(user.getEmail());
		assertNotNull(credentials.getPassword());
		assertThat(credentials.getEmail(), is(user.getEmail()));
	}

	@Test(expected=EntityDoesNotExistException.class)
	public void getCredentialsNotExist() {
		target.getCredentials(user.getEmail() + "not existing");
	}

}
