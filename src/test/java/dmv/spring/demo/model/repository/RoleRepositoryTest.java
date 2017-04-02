package dmv.spring.demo.model.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.BeforeClass;
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

	private static Role role;
	private static String roleShortName;
	private static String roleFullName;

	@Autowired
	private RoleRepository target;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/* Pre-existing role */
		roleShortName = "ADM";
		roleFullName= "Administrator";
	}

	@Test
	public void findByShortName() {
		role = target.findByShortName(roleShortName);
		assertNotNull(role);
		assertThat(role.getFullName(), is(roleFullName));
	}

	@Test(expected=IllegalArgumentException.class)
	public void findByShortNameNull() {
		target.findByShortName(null);
	}

	@Test(expected=EntityDoesNotExistException.class)
	public void findByShortNameWrong() {
		target.findByShortName(roleFullName);
	}

	@Test
	public void getRoleUsers() {
		role = target.findByShortName(roleShortName);
		Set<User> users = target.getUsers(role);
		//System.out.println(users);
		/*
		 * This test relies on fact that database is
		 * not empty from the very beginning (real or
		 * demo users are already there, and at least
		 * one Administrator is present)
		 */
		assertTrue(users.size() > 0);
	}

	@Test
	public void getRoleUsersShortNameAbsent() {
		role = target.findByShortName(roleShortName);
		role.setShortName(null);
		Set<User> users = target.getUsers(role);
		/*
		 * Empty list is expected
		 */
		assertTrue(users.size() == 0);
	}

}
