package dmv.spring.demo.rest.security;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultMatcher;

@WithUserDetails("demo.admin@spring.demo")
public class UserRestSecurityAdminTest extends UserRestSecurityTest {
	
	/*
	 * All requests made by the Admin user supposed to be successful
	 */
	private final static ResultMatcher statusMatcher = status().is2xxSuccessful();

	@Override
	protected ResultMatcher getStatus() {
		return statusMatcher;
	}
	
}
