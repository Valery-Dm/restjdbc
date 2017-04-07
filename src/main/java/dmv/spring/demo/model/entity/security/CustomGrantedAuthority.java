package dmv.spring.demo.model.entity.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import dmv.spring.demo.model.entity.Role;

/**
 * Comparable {@link Role} wrapper with {@link GrantedAuthority} specification.
 * @author dmv
 */
public class CustomGrantedAuthority extends Role 
        implements GrantedAuthority, Comparable<CustomGrantedAuthority> {

	private static final long serialVersionUID = -8751896582717211594L;
	
	public CustomGrantedAuthority(Role role) {
		super(role);
		Assert.notNull(getShortName(), "Short name cannot be null");
	}

	// Natural ordering is needed for GrantedAuthorities collection.
	@Override
	public int compareTo(CustomGrantedAuthority other) {
		// shortName can't be null - this is enforced characteristic
		return getShortName().compareTo(other.getShortName());
	}

	@Override
	public String getAuthority() {
		return getShortName();
	}

}
