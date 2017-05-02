package dmv.spring.demo.rest.representation;

import org.springframework.hateoas.ResourceSupport;

import dmv.spring.demo.model.entity.Role;

/**
 * Data Transfer Object for {@link Role} entity
 * @author dmv
 */
public class RoleDTO extends ResourceSupport {

	private final String shortName;

	private final String fullName;

	public RoleDTO(Role role) {
		shortName = role.getShortName();
		fullName = role.getFullName();
	}

	public String getShortName() {
		return shortName;
	}

	public String getFullName() {
		return fullName;
	}

	@Override
	public String toString() {
		return "RoleDTO [shortName=" + shortName + ", fullName=" + fullName + "]";
	}

}
