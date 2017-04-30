package dmv.spring.demo.model.repository;

import static java.util.Collections.emptySet;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.exceptions.EntityAlreadyExistsException;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTest {

	private User user;
	private String email;
	private Set<Role> roles;
	private Role admRole;
	private Role usrRole;
	private Role devRole;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Autowired
	private UserRepository target;

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
		// don't add admin role for now
		usrRole = new Role("USR", "User");
		roles.add(usrRole);
		devRole = new Role("DEV", "Developer");
		roles.add(devRole);
		user.setRoles(roles);
	}

	@After
	public void cleanUp() {
		/*
		 * These tests were done 'on best effort'.
		 * We are not expecting test.user to be
		 * And it should not remain after.
		 * existed in database at this point.
		 * So, trying to remove.
		 */
		try {
			User testUser = target.findByEmail(email);
			target.delete(testUser);
		} catch (EntityDoesNotExistException e) {
			// we are good
		} catch (Exception e) {
			System.out.println("unsuccessful cleanup%n" + e.getMessage());
		}
	}

	@Test
	public void successfulCRUDOperations() {
		User found = null;

		user = target.create(user);
		found = target.findByEmail(user.getEmail());
		compareUsers(found, user);

		user.setLastName("newLastName");
		user.setMiddleName(null);
		target.update(user);
		found = target.findByEmail(user.getEmail());
		compareUsers(found, user);
		assertNull(found.getMiddleName());

		target.delete(user);
		exception.expect(EntityDoesNotExistException.class);
		target.findByEmail(user.getEmail());
	}
	
	private void compareUsers(User act, User exp) {
		assertNotNull("user is null", act);
		assertThat("wrong email", act.getEmail(), is(exp.getEmail()));
		assertThat("wrong first name", act.getFirstName(), is(exp.getFirstName()));
		assertThat("wrong last name", act.getLastName(), is(exp.getLastName()));
		assertThat("wrong middle name", act.getMiddleName(), is(exp.getMiddleName()));
		assertThat("wrong user roles", act.getRoles(), is(exp.getRoles()));
	}

	@Test
	public void findById() {
		User found = null;
		user = target.create(user);
		found = target.findById(user.getId());
		compareUsers(found, user);
	}

	@Test
	public void findByEmail() throws Exception {
		User found = null;
		user = target.create(user);
		found = target.findByEmail(user.getEmail());
		compareUsers(found, user);
	}

	@Test
	public void findWithRoles() {
		Set<Role> roles = user.getRoles();
		User created = target.create(user);
		
		User found = target.findById(created.getId());
		assertThat(found.getRoles(), is(roles));
		
		found = target.findByEmail(created.getEmail());
		assertThat(found.getRoles(), is(roles));
	}
	
	@Test
	public void findWithOutRoles() {
		user.setRoles(null);
		User created = target.create(user);
		
		User found = target.findById(created.getId());
		assertThat(found.getRoles(), is(emptySet()));
		
		found = target.findByEmail(created.getEmail());
		assertThat(found.getRoles(), is(emptySet()));
	}

	@Test
	public void findEmailNull() {
		exception.expect(IllegalArgumentException.class);
		target.findByEmail(null);
	}

	@Test
	public void findIdNull() {
		exception.expect(IllegalArgumentException.class);
		target.findById(null);
	}

	@Test
	public void findEmailEmpty() {
		exception.expect(IllegalArgumentException.class);
		target.findByEmail("");
	}

	@Test
	public void findIdWrong() {
		exception.expect(EntityDoesNotExistException.class);
		target.findById(-1L);
	}

	@Test
	public void findWrongEmail() {
		exception.expect(EntityDoesNotExistException.class);
		target.findByEmail(user.getEmail() + user.getFirstName());
	}

	@Test
	public void createExisted() {
		user = target.create(user);
		exception.expect(EntityAlreadyExistsException.class);
		user = target.create(user);
	}

	@Test
	public void createNull() {
		exception.expect(IllegalArgumentException.class);
		target.create(null);
	}

	@Test
	public void createWithoutEmail() {
		user.setEmail(null);
		exception.expect(IllegalArgumentException.class);
		target.create(user);
	}

	@Test
	public void createWithEmptyEmail() {
		user.setEmail("");
		exception.expect(IllegalArgumentException.class);
		target.create(user);
	}

	@Test
	public void createWithoutMiddleName() {
		User created = null;

		user.setMiddleName(null);
		created = target.create(user);
		compareUsers(created, user);
		assertNull("middle name is not null", created.getMiddleName());
		// we are not expecting provided password to be passed around
		assertNull("provided password returned back", created.getPassword());
	}

	@Test
	public void createWithoutPassword() {
		User created = null;

		user.setPassword(null);
		/* Auto-generated password expected */
		created = target.create(user);
		compareUsers(created, user);
		String password = created.getPassword();
		assertTrue("no password generated", password != null && password.length() > 0);
	}

	@Test
	public void createWithEmptyFirstName() {
		user.setFirstName(null);
		checkDBWasNotAlteredWhenCreateFailed();
	}

	@Test
	public void createWithEmptyLastName() {
		user.setLastName(null);
		checkDBWasNotAlteredWhenCreateFailed();
	}

	@Test
	public void createWithoutRoles() {
		User found = null;

		user.setRoles(null);
		user = target.create(user);
		found = target.findByEmail(user.getEmail());
		assertTrue(found.getRoles().size() == 0);
		assertThat(found.getRoles(), is(Collections.emptySet()));
	}

	@Test
	public void createWithWrongRoles() {
		Role wrong = new Role("WRG", null);
		user.getRoles().add(wrong);
		checkDBWasNotAlteredWhenCreateFailed();
	}

	@Test
	public void createWithNullNameRoles() {
		Role wrong = new Role(null, "some role");
		user.getRoles().add(wrong);
		checkDBWasNotAlteredWhenCreateFailed();
	}

	@Test
	public void createWithTooLongNameRoles() {
		Role wrong = new Role("THE_NAME_IS_TOO_LONG", "some role");
		user.getRoles().add(wrong);
		checkDBWasNotAlteredWhenCreateFailed();
	}

	private void checkDBWasNotAlteredWhenCreateFailed() {
		try {
			target.create(user);
			fail("IllegalArgumentException was expected");
		} catch (IllegalArgumentException e) {
			// check that database stays untouched
			exception.expect(EntityDoesNotExistException.class);
			target.findByEmail(user.getEmail());
		}
	}

	@Test
	public void updateNull() {
		exception.expect(IllegalArgumentException.class);
		target.update(null);
	}

	@Test
	public void updateNotExisted() {
		user.setId(Long.MIN_VALUE);
		exception.expect(EntityDoesNotExistException.class);
		target.update(user);
	}

	@Test
	public void updateNullId() {
		User created = target.create(user);
		created.setId(null);
		checkDBWasNotAlteredWhenUpdateFailed(created);
	}

	@Test
	public void updateWithSameInfo() {
		user = target.create(user);
		/*
		 * Although it seems unnatural,
		 * nothing wrong should happen here
		 */
		target.update(user);
	}

	@Test
	public void updateWithEmptyFirstName() {
		User created = target.create(user);
		created.setFirstName(null);
		checkDBWasNotAlteredWhenUpdateFailed(created);
	}

	@Test
	public void updateWithEmptyLastName() {
		User created = target.create(user);
		created.setLastName(null);
		checkDBWasNotAlteredWhenUpdateFailed(created);
	}

	@Test
	public void updateWithDifferentRoles() {
		User updated = null;

		user = target.create(user);
		user.getRoles().remove(usrRole);
		user.getRoles().add(admRole);
		
		updated = target.update(user);
		Set<Role> updatedRoles = updated.getRoles();
		assertTrue(updatedRoles.contains(admRole));
		assertTrue(updatedRoles.contains(devRole));
		assertFalse(updatedRoles.contains(usrRole));
	}

	@Test
	public void updateWithIncompleteRoles() {
		User updated = null;

		user.getRoles().clear();
		user = target.create(user);

		Role incomplete = new Role("DEV", null);
		user.getRoles().add(incomplete);
		updated = target.update(user);

		Set<Role> updatedRoles = updated.getRoles();
		// We're expecting complete role in the answer
		assertTrue(updatedRoles.contains(devRole));
		// This one was not added
		assertFalse(updatedRoles.contains(usrRole));
	}

	@Test
	public void updateWithWrongRoles() {
		Set<Role> originalRoles = user.getRoles();
		user = target.create(user);
		try {
			Role wrong = new Role("WRG", null);
			Set<Role> updateRoles = new HashSet<>(originalRoles);
			updateRoles.add(wrong);
			user.setRoles(updateRoles);
			target.update(user);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException e) {
			// Assert that DB was not corrupted
			User found = target.findById(user.getId());
			assertThat(found.getRoles(), is(originalRoles));
		}
	}

	@Test
	public void updateWithNullNameRoles() {
		Set<Role> originalRoles = user.getRoles();
		user = target.create(user);
		try {
			Role wrong = new Role(null, "some role");
			Set<Role> updateRoles = new HashSet<>(originalRoles);
			updateRoles.add(wrong);
			user.setRoles(updateRoles);
			target.update(user);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException e) {
			// Assert that DB was not corrupted
			User found = target.findById(user.getId());
			assertThat(found.getRoles(), is(originalRoles));
		}
	}

	@Test
	public void updateWithTooLongNameRoles() {
		User created = target.create(user);

		Role wrong = new Role("WRONG_BECAUSE_TOO_LONG", null);
		created.getRoles().add(wrong);
		checkDBWasNotAlteredWhenUpdateFailed(created);
	}

	private void checkDBWasNotAlteredWhenUpdateFailed(User forUpdate) {
		try {
			target.update(forUpdate);
			fail("IllegalArgumentException was expected");
		} catch (IllegalArgumentException e) {
			// check that database stays untouched
			User found = target.findByEmail(user.getEmail());
			compareUsers(found, user);
		}
	}

	@Test
	public void deleteNotExisted() {
		exception.expect(EntityDoesNotExistException.class);
		user.setId(-1L);
		target.delete(user);
	}

	@Test
	public void deleteWithoutId() {
		user = target.create(user);
		exception.expect(IllegalArgumentException.class);
		user.setId(null);
		target.delete(user);
	}

	@Test
	public void deleteNull() {
		exception.expect(IllegalArgumentException.class);
		target.delete(null);
	}
}
