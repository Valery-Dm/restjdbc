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
	
	/**
	 * Create GrantedAuthority object from minimally required field
	 * short name of some {@link Role}
	 * @param role A role's short name to grant an authority to
	 * @throws IllegalArgumentException if argument is null
	 */
	public CustomGrantedAuthority(String shortName) {
		Assert.notNull(shortName, "Short name cannot be null");
		setShortName(shortName);
	}

	/**
	 * Create GrantedAuthority object from given {@link Role}
	 * @param role A role to grant an authority to
	 * @throws IllegalArgumentException if role's short name is null
	 */
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
