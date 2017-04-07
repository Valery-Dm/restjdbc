package dmv.spring.demo.model.entity.security;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;

/**
 * Creates a wrapper for internal {@link User} object to conform to
 * to {@link UserDetails} interface to be used within Authentication Context.
 * @author dmv
 */
public class CustomUserDetails extends User implements UserDetails {

	private static final long serialVersionUID = 5004823702176912214L;
	
	private final Collection<? extends GrantedAuthority> authorities;
	
	/**
	 * Create new instance of {@link UserDetails} object with
	 * given User details. Maps {@link Role} to {@link GrantedAuthority}
	 * @param user User object
	 */
	public CustomUserDetails(User user) {
		super(user);
		// must return sorted collection and never null
		authorities = user.getRoles() == null 
				                ? Collections.emptyList() 
								: user.getRoles()
								      .stream()
								      .map(role -> new CustomGrantedAuthority(role))
								      .sorted()
								      .collect(Collectors.toList());
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#getAuthorities()
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#getUsername()
	 */
	@Override
	public String getUsername() {
		return getEmail() == null ? "" : getEmail();
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonExpired()
	 */
	@Override
	public boolean isAccountNonExpired() {
		// this functionality is not implemented
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonLocked()
	 */
	@Override
	public boolean isAccountNonLocked() {
		// this functionality is not implemented
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#isCredentialsNonExpired()
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		// this functionality is not implemented
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		// this functionality is not implemented
		return true;
	}
}
