package dmv.spring.demo.model.repository;

import static java.util.Collections.emptySet;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.exceptions.EntityAlreadyExistsException;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class UserRepositoryTest {
	
	private static User user;
	
	@Autowired
	private UserRepository target;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		user = new User();
		user.setEmail("test.user@mail.com");
		user.setFirstName("First");
		user.setLastName("Last");
		user.setMiddleName("Middle");
		user.setPassword("123456");
		user.setRoles(emptySet());
	}

	@After
	public void cleanUp() {
		/*
		 * These tests were done 'on best effort'.
		 * We are not expecting test.user to be
		 * existed in database at this point.
		 * So, trying to remove.
		 */
		try {
			target.delete(user);
		} catch (EntityDoesNotExistException e) {
			// There is nothing to do here
		}
	}
	
	@Test
	public void crud() {
		User found = target.findByEmail(user.getEmail());
		assertNull("user is not null", found);
		
		target.create(user);
		found = target.findByEmail(user.getEmail());
		assertNotNull("user is null", found);
		assertThat("wrong email", found.getEmail(), is(user.getEmail()));
		assertThat("wrong last name", found.getLastName(), is(user.getLastName()));
		
		user.setLastName("NewLast");
		target.update(user);
		found = target.findByEmail(user.getEmail());
		assertThat("wrong new last name", found.getLastName(), is(user.getLastName()));
		
		target.delete(user);
		found = target.findByEmail(user.getEmail());
		assertNull("user is not null", found);
	}
	
	@Test(expected=EntityAlreadyExistsException.class)
	public void createExisted() {
		target.create(user);
		target.create(user);
	}

	@Test(expected=EntityDoesNotExistException.class)
	public void updateNotExisted() {
		target.update(user);
	}

	@Test(expected=EntityDoesNotExistException.class)
	public void deleteNotExisted() {
		target.delete(user);
	}
	
	@Test
	public void findNull() {
		User found = target.findByEmail(null);
		assertNull("user is not null", found);

		found = target.findByEmail("");
		assertNull("user is not null", found);
	}

	@Test(expected=IllegalArgumentException.class)
	public void createNull() {
		target.create(null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void updateNull() {
		target.update(null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void deleteNull() {
		target.delete(null);
	}
}
