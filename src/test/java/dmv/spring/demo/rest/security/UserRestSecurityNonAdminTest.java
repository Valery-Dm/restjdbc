package dmv.spring.demo.rest.security;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultMatcher;

//@WithMockUser
@WithUserDetails("demo.developer@spring.demo")
public class UserRestSecurityNonAdminTest extends UserRestSecurityTest {
	
	/*
	 * All requests made by the NonAdmin (yet authorized) user supposed to be forbidden
	 */
	private final static ResultMatcher statusMatcher = status().isForbidden();

	@Override
	protected ResultMatcher getStatus() {
		return statusMatcher;
	}
	
}
