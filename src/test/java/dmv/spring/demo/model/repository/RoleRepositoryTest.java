package dmv.spring.demo.model.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;

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
	
	@After
	public void cleanUp() {
		role = null;
	}

	@Test
	public void findByShortName() {
		role = target.findByShortName(roleShortName);
		assertNotNull(role);
		assertThat(role.getFullName(), is(roleFullName));
	}
	
	@Test
	public void findNull() {
		role = target.findByShortName(null);
		assertNull(role);
		
		role = target.findByShortName("");
		assertNull(role);
	}
	
	@Test
	public void getRoleUsers() {
		role = target.findByShortName(roleShortName);
		Set<User> users = target.getUsers(role);
		assertTrue(users.size() > 0);
	}

}
