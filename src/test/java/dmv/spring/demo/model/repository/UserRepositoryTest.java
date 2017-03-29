package dmv.spring.demo.model.repository;

import static java.util.Collections.emptySet;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dmv.spring.demo.model.entity.User;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class UserRepositoryTest {
	
	@Autowired
	private UserRepository target;
	private static User user;
	
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

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void findByEmail() {
		User found = target.findByEmail("some@mail.com");
		assertNotNull("found is null", found);
		assertThat("wrong email", found.getEmail(), is(user.getEmail()));
		assertThat("wrong last name", found.getLastName(), is(user.getLastName()));
	}
	
	@Test
	public void crud() {
		//target.delete(user);
		target.create(user);
		User found = target.findByEmail(user.getEmail());
		//assertNotNull("user is null", found);
		
		user.setLastName("NewLast");
		target.update(user);
		//assertThat("wrong last name", found.getLastName(), is(user.getLastName()));
		
		target.delete(user);
		found = target.findByEmail(user.getEmail());
		assertNull("user is not null", found);
	}

}
