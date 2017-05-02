package dmv.spring.demo.model.entity.security;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;

/**
 * Creates a wrapper for internal {@link User} object to conform
 * to {@link UserDetails} interface, to be used within Authentication Context.
 * @author dmv
 */
public class CustomUserDetails extends User implements UserDetails {

	private final Logger logger = getLogger(CustomUserDetails.class);

	private static final long serialVersionUID = 5004823702176912214L;

	private final Collection<? extends GrantedAuthority> authorities;

	/**
	 * Create new instance of {@link UserDetails} object with
	 * given User details. Maps {@link Role} to {@link GrantedAuthority}
	 * @param user User object
	 * @throws IllegalArgumentException if argument is null
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
		logger.debug("{} was passed to CustomUserDetails constructor", user);
	}

	/**
	 * Create new instance of {@link UserDetails} object with
	 * given User details. Maps {@link Role} to {@link GrantedAuthority}
	 * @param email user's email
	 * @param password user's password
	 * @param roleName {@link Role} short name
	 * @throws IllegalArgumentException if either of arguments is null
	 */
	public CustomUserDetails(String email, String password, String roleName) {
		Assert.noNullElements(new Object[]{email, password, roleName},
				             "Null arguments are not supported here");
		setEmail(email);
		setPassword(password);
		/*
		 * This constructor is for testing purposes so it's perfectly fine
		 * not to propagate Set<Role> collection further, will just create
		 * GrantedAuthorities at this level.
		 */
		authorities = Arrays.asList(new CustomGrantedAuthority(roleName));
		logger.debug("User {} was passed to CustomUserDetails constructor", email);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getUsername() {
		return getEmail() == null ? "" : getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		// this functionality is not implemented
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// this functionality is not implemented
		return true;
	}

	@Override
	public boolean isEnabled() {
		// this functionality is not implemented
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// this functionality is not implemented
		return true;
	}
}
