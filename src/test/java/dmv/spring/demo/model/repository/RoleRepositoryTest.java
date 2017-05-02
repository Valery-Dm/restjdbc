package dmv.spring.demo.model.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RoleRepositoryTest {

	private Role role;
	private String roleShortName;
	private String roleFullName;

	@Autowired
	private RoleRepository target;

	@Before
	public void setUp() throws Exception {
		/* Pre-existing role */
		roleShortName = "ADM";
		roleFullName= "Administrator";
		role = null;
	}

	@Test
	public void findByShortName() {
		int T = 1;
		while (T-- > 0)
			role = target.findByShortName(roleShortName);
		assertNotNull(role);
		assertThat(role.getShortName(), is(roleShortName));
		assertThat(role.getFullName(), is(roleFullName));
	}

	@Test(expected=EntityDoesNotExistException.class)
	public void findByShortNameNull() {
		target.findByShortName(null);
	}

	@Test(expected=EntityDoesNotExistException.class)
	public void findByShortNameWrong() {
		target.findByShortName("not exsiting" + roleShortName);
	}

	@Test
	public void getRoleUsers() {
		role = target.findByShortName(roleShortName);
		Set<User> users = target.getUsers(role);
		/*
		 * This test relies on fact that database is
		 * not empty from the very beginning (real or
		 * demo users are already there, and at least
		 * one Administrator is present)
		 */
		assertTrue(users.size() > 0);
	}

	@Test(expected=IllegalArgumentException.class)
	public void getRoleUsersNullRole() {
		target.getUsers(null);
	}

	@Test(expected=EntityDoesNotExistException.class)
	public void getRoleUsersShortNameAbsent() {
		role = target.findByShortName(roleShortName);
		role.setShortName("not exsiting" + roleShortName);
		target.getUsers(role);
	}

	@Test(expected=EntityDoesNotExistException.class)
	public void getRoleUsersShortNameNull() {
		role = target.findByShortName(roleShortName);
		role.setShortName(null);
		target.getUsers(role);
	}

}
