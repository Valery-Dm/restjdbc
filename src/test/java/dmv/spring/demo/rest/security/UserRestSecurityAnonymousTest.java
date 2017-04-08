package dmv.spring.demo.rest.security;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.ResultMatcher;

@WithAnonymousUser
public class UserRestSecurityAnonymousTest extends UserRestSecurityTest {
	
	/*
	 * All requests made by the Anonymous user supposed to be unauthorized.
	 * All wrong credentials situations also fall in this category.
	 */
	private final static ResultMatcher statusMatcher = status().isUnauthorized();

	@Override
	protected ResultMatcher getStatus() {
		return statusMatcher;
	}
	
}
