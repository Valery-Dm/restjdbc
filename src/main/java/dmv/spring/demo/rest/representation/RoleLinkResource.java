package dmv.spring.demo.rest.representation;

import org.springframework.hateoas.ResourceSupport;

import dmv.spring.demo.model.entity.Role;

/**
 * {@link Role} representation with full name and self link only.
 * To be used in Lists
 * @author dmv
 */
public class RoleLinkResource extends ResourceSupport {

	private final String fullName;

	public RoleLinkResource(String fullName) {
		super();
		this.fullName = fullName;
	}

	public String getFullName() {
		return fullName;
	}
}
