package dmv.spring.demo.security;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * The only purpose for this class is to set custom UserDetailsService
 * and passwordEncoder
 * @author dmv
 */
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

	public CustomAuthenticationProvider(UserDetailsService userDetailsService,
                                        PasswordEncoder passwordEncoder) {
		setUserDetailsService(userDetailsService);
    	setPasswordEncoder(passwordEncoder);
	}

}
