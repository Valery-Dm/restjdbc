/**
 *
 */
package dmv.spring.demo.rest.representation;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.rest.controller.RoleRestController;

/**
 * Data Transfer Object for {@link Role} entity
 * @author user
 */
public class RoleDTO extends ResourceSupport {

	@NotNull
	@Size(max=3)
	private final String shortName;

	@NotNull
	@Size(max=50)
	private final String fullName;

	@JsonCreator
	public RoleDTO(@JsonProperty("shortname") String shortName,
			       @JsonProperty("fullname") String fullName) {
		this.shortName = shortName;
		this.fullName = fullName;
		createLinks(shortName);
	}

	public RoleDTO(Role role) {
		shortName = role.getShortName();
		fullName = role.getFullName();
		//createLinks(shortName);
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

	private void createLinks(String shortName) {
		ControllerLinkBuilder link =
				linkTo(RoleRestController.class)
				.slash(shortName);
		add(link.withSelfRel());
		add(link.slash("users")
				.withRel("role:users"));
	}

}
