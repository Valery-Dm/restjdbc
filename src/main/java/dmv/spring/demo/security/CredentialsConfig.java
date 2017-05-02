package dmv.spring.demo.security;

import java.security.SecureRandom;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Manage beans and methods that work with user credentials information.
 *
 * @author dmv
 */
@Configuration
public class CredentialsConfig {

	/**
	 * @return cryptographically strong random number generator
	 */
	@Bean
	public SecureRandom getSecureRandom() {
		return new SecureRandom();
	}

	/**
	 * @return recommended {@link BCryptPasswordEncoder}
	 */
	@Bean
	public PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
