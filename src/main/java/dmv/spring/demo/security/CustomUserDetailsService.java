package dmv.spring.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.entity.security.CustomUserDetails;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;
import dmv.spring.demo.model.repository.CredentialsRepository;

/**
 * This service will read existing users from data source with
 * complete credentials (i.e. including Password field)
 * @author dmv
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private CredentialsRepository credentialsRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			User user = credentialsRepository.getCredentials(username);
			return new CustomUserDetails(user);
		} catch (EntityDoesNotExistException e) {
			throw new UsernameNotFoundException(e.getLocalizedMessage());
		}
	}

}
