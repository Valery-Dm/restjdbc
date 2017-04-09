package dmv.spring.demo.rest.security;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultMatcher;

@WithMockUser(username = "admin", authorities = { "ADM", "USR" })
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
