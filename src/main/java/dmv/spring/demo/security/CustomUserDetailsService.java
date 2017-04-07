package dmv.spring.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.entity.security.CustomUserDetails;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;
import dmv.spring.demo.model.repository.UserRepository;

/**
 * This service will read existing users from data source with
 * complete credentials (i.e. including Password field)
 * @author dmv
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepository;

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			User user = userRepository.getCredentials(username);
			return new CustomUserDetails(user);
		} catch (EntityDoesNotExistException e) {
			throw new UsernameNotFoundException(e.getLocalizedMessage());
		}
	}

}
