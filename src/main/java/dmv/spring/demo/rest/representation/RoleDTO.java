package dmv.spring.demo.rest.representation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.hateoas.ResourceSupport;

import dmv.spring.demo.model.entity.Role;

/**
 * Data Transfer Object for {@link Role} entity
 * @author dmv
 */
public class RoleDTO extends ResourceSupport {

	@NotNull
	@Size(min = 3, max=3)
	private final String shortName;

	@NotNull
	@Size(min = 3, max=50)
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
