package dmv.spring.demo.rest.security;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ UserRestSecurityAdminTest.class,
	            UserRestSecurityNonAdminTest.class,
	            UserRestSecurityAnonymousTest.class })
public class UserRestSecurityTests {

}
