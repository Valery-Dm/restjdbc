package dmv.spring.demo.rest.representation;

import static java.util.Collections.emptySet;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.hateoas.ResourceSupport;

import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.rest.representation.assembler.RoleLinkResourceAsm;

/**
 * Data transfer object for {@link User} entity
 * @author dmv
 */
public class UserDTO extends ResourceSupport {

	private final Long userId;
	private final String email;
	private final String firstName;
	private final String lastName;
	private final String middleName;
	private final String pasword;
	private final Set<RoleLinkResource> roles;

	public UserDTO(User user) {
		this.userId = user.getId();
		this.email = user.getEmail();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.middleName = user.getMiddleName();
		this.pasword = user.getPassword();
		if (user.getRoles() != null)
			roles = user.getRoles()
			    .stream()
			    .map(role -> new RoleLinkResourceAsm().toResource(role))
			    .collect(Collectors.toSet());
		else roles = emptySet();
	}

	public Long getUserId() {
		return userId;
	}

	public String getEmail() {
		return email;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public String getPasword() {
		return pasword;
	}

	public Set<RoleLinkResource> getRoles() {
		return roles;
	}

}
