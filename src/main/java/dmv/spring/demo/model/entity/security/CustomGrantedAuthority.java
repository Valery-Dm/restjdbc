package dmv.spring.demo.model.entity.security;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import dmv.spring.demo.model.entity.Role;

/**
 * Comparable {@link Role} wrapper with {@link GrantedAuthority} specification.
 * @author dmv
 */
public class CustomGrantedAuthority extends Role
        implements GrantedAuthority, Comparable<CustomGrantedAuthority> {

	private static final Logger logger = getLogger(CustomGrantedAuthority.class);

	private static final String SHORT_NAME_CANNOT_BE_NULL = "Short name cannot be null";

	private static final long serialVersionUID = -8751896582717211594L;

	/**
	 * Create GrantedAuthority object from minimally required field
	 * short name of some {@link Role}
	 * @param role A role's short name to grant an authority to
	 * @throws IllegalArgumentException if argument is null
	 */
	public CustomGrantedAuthority(String shortName) {
		Assert.notNull(shortName, SHORT_NAME_CANNOT_BE_NULL);
		setShortName(shortName);
		if (logger.isDebugEnabled())
			logger.debug("role " + shortName + " was passed to CustomGrantedAuthority constructor");
	}

	/**
	 * Create GrantedAuthority object from given {@link Role}
	 * @param role A role to grant an authority to
	 * @throws IllegalArgumentException if role's short name is null
	 */
	public CustomGrantedAuthority(Role role) {
		super(role);
		Assert.notNull(getShortName(), SHORT_NAME_CANNOT_BE_NULL);
		if (logger.isDebugEnabled())
			logger.debug(role + " was passed to CustomGrantedAuthority constructor");
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
