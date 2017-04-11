package dmv.spring.demo.security;

import java.security.SecureRandom;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Manage beans that work with user passwords
 * @author dmv
 */
@Configuration
public class CredentialConfig {

	@Bean
	public SecureRandom getSecureRandom() {
		return new SecureRandom();
	}

	@Bean
	public PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
