package dmv.spring.demo.model.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
		 * existed in database at this point.
		 * And it should not remain after.
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

		target.create(user);
		found = target.findByEmail(user.getEmail());
		assertNotNull("user is null", found);
		assertThat("wrong email", found.getEmail(), is(user.getEmail()));
		assertThat("wrong last name", found.getLastName(), is(user.getLastName()));

		user.setLastName("newLastName");
		user.setMiddleName(null);
		target.update(user);
		found = target.findByEmail(user.getEmail());
		assertThat("wrong new last name", found.getLastName(), is(user.getLastName()));
		assertNull(found.getMiddleName());

		target.delete(user);
		exception.expect(EntityDoesNotExistException.class);
		target.findByEmail(user.getEmail());
	}

	@Test
	public void findById() {
		User found = null;
		target.create(user);
		found = target.findById(user.getId());
		assertNotNull("user is null", found);
		assertThat("wrong email", found.getEmail(), is(user.getEmail()));
		assertThat("wrong last name", found.getLastName(), is(user.getLastName()));
		assertThat(found.getRoles(), is(user.getRoles()));
	}

	@Test
	public void findByEmail() throws Exception {
		User found = null;
		target.create(user);
		found = target.findByEmail(user.getEmail());
		assertNotNull("user is null", found);
		assertThat("wrong email", found.getEmail(), is(user.getEmail()));
		assertThat("wrong last name", found.getFirstName(), is(user.getFirstName()));
		assertThat(found.getRoles(), is(user.getRoles()));
	}

	@Test
	public void findWithRoles() {
		/*
		 * This test relies on initial demo users
		 * existence (with corresponding roles).
		 * We are not aiming DataBase integrity here,
		 * so, this method can be ignored later on
		 */
		User adminDev = target.findByEmail("demo.admin.dev@spring.demo");
		assertNotNull(adminDev);
		assertThat(adminDev.getFirstName(), is("Vasily"));

		Set<Role> roles = adminDev.getRoles();

		Set<String> shortNames = roles.stream()
		     .map(role -> role.getShortName())
		     .collect(Collectors.toSet());

		assertThat(roles.size(), is(2));
		assertTrue(shortNames.contains("ADM"));
		assertTrue(shortNames.contains("DEV"));
		assertFalse(shortNames.contains("USR"));
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
		exception.expect(EntityDoesNotExistException.class);
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
		target.create(user);
		exception.expect(EntityAlreadyExistsException.class);
		target.create(user);
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
		user.setEmail(null);
		exception.expect(IllegalArgumentException.class);
		target.create(user);
	}

	@Test
	public void createWithoutMiddleName() {
		User found = null;

		user.setMiddleName(null);
		target.create(user);
		found = target.findByEmail(user.getEmail());
		assertNull(found.getMiddleName());
	}

	@Test
	public void createWithoutPassword() {
		User created = null;

		user.setPassword(null);
		/* Auto-generated password expected */
		created = target.create(user);
		String password = created.getPassword();
		assertTrue("no password generated", password != null && password.length() > 0);
	}

	@Test
	public void createWithEmptyFirstName() {
		user.setFirstName(null);
		exception.expect(IllegalArgumentException.class);
		target.create(user);
	}

	@Test
	public void createWithEmptyLastName() {
		user.setLastName(null);
		exception.expect(IllegalArgumentException.class);
		target.create(user);
	}

	@Test
	public void createWithoutRoles() {
		User found = null;

		user.setRoles(null);
		target.create(user);
		found = target.findByEmail(user.getEmail());
		assertTrue(found.getRoles().size() == 0);
		assertThat(found.getRoles(), is(Collections.emptySet()));
	}

	@Test
	public void createWithWrongRoles() {
		try {
			Role wrong = new Role("WRG", null);
			user.getRoles().add(wrong);
			target.create(user);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException e) {
			// Assert that user was not created
			exception.expect(EntityDoesNotExistException.class);
			target.findById(user.getId());
		}
	}

	@Test
	public void createWithNullNameRoles() {
		exception.expect(IllegalArgumentException.class);

		Role wrong = new Role(null, "some role");
		user.getRoles().add(wrong);
		target.create(user);
	}

	@Test
	public void createWithTooLongNameRoles() {
		exception.expect(IllegalArgumentException.class);

		Role wrong = new Role("THE_NAME_IS_TOO_LONG", "some role");
		user.getRoles().add(wrong);
		target.create(user);
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
		user.setId(null);
		exception.expect(IllegalArgumentException.class);
		target.update(user);
	}

	@Test
	public void updateWithSameInfo() {
		target.create(user);
		/*
		 * Although it seems unnatural,
		 * nothing wrong should happen here
		 */
		target.update(user);
	}

	@Test
	public void updateWithEmptyFirstName() {
		target.create(user);
		user.setFirstName(null);
		exception.expect(IllegalArgumentException.class);
		target.update(user);
	}

	@Test
	public void updateWithEmptyLastName() {
		target.create(user);
		user.setLastName(null);
		exception.expect(IllegalArgumentException.class);
		target.update(user);
	}

	@Test
	public void updateWithDifferentRoles() {
		User updated = null;

		target.create(user);
		user.getRoles().remove(usrRole);
		target.update(user);
		updated = target.findByEmail(user.getEmail());
		Set<Role> updatedRoles = updated.getRoles();
		assertTrue(updatedRoles.contains(devRole));
		assertFalse(updatedRoles.contains(usrRole));
	}

	@Test
	public void updateWithIncompleteRoles() {
		User updated = null;

		user.getRoles().clear();
		target.create(user);

		Role incomplete = new Role("DEV", null);
		user.getRoles().add(incomplete);
		updated = target.update(user);

		Set<Role> updatedRoles = updated.getRoles();
		// We're expecting complete role in answer
		assertTrue(updatedRoles.contains(devRole));
		// This one was not added
		assertFalse(updatedRoles.contains(usrRole));
	}

	@Test
	public void updateWithWrongRoles() {
		Set<Role> originalRoles = user.getRoles();
		target.create(user);
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
		target.create(user);
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
		exception.expect(IllegalArgumentException.class);

		user.getRoles();
		target.create(user);

		Role wrong = new Role("WRONG_BECAUSE_TOO_LONG", null);
		user.getRoles().add(wrong);
		target.update(user);
	}

	@Test
	public void deleteNotExisted() {
		exception.expect(EntityDoesNotExistException.class);
		user.setId(-1L);
		target.delete(user);
	}

	@Test
	public void deleteWithoutId() {
		target.create(user);
		exception.expect(IllegalArgumentException.class);
		user.setId(null);
		target.delete(user);
	}

	@Test
	public void deleteNull() {
		exception.expect(IllegalArgumentException.class);
		target.delete(null);
	}

	@Test
	public void getCredentials() {
		target.create(user);
		User credentials = target.getCredentials(user.getEmail());
		assertNotNull(credentials.getPassword());
		assertThat(credentials.getEmail(), is(user.getEmail()));
	}

	@Test
	public void getCredentialsNotExist() {
		exception.expect(EntityDoesNotExistException.class);
		target.getCredentials(user.getEmail());
	}
}
