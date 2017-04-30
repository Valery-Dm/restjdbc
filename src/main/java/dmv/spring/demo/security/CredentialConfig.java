package dmv.spring.demo.security;

import java.security.SecureRandom;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.security.CustomGrantedAuthority;

/**
 * Manage beans that work with user passwords
 * @author dmv
 */
@Configuration
public class CredentialConfig {
	
	@Bean
	@Scope("prototype")
	@Lazy(true)
	public GrantedAuthority getGrantedAuthority(Role role) {
		return new CustomGrantedAuthority(role);
	}

	@Bean
	public SecureRandom getSecureRandom() {
		return new SecureRandom();
	}

	@Bean
	public PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
