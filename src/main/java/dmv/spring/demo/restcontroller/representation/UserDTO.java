/**
 * 
 */
package dmv.spring.demo.restcontroller.representation;

import java.util.Set;

import org.springframework.hateoas.ResourceSupport;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;

/**
 * Data transfer object for {@link User} entity
 * @author user
 */
public class UserDTO extends ResourceSupport {

	private final Long id;
	private final String email;
	private final String firstName;
	private final String lastName;
	private final String middleName;
	private final Set<Role> roles;
	
	public UserDTO(Long id, String email, String firstName, 
			       String lastName, String middleName, Set<Role> roles) {
		this.id = id;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.middleName = middleName;
		this.roles = roles;
	}
	
	public UserDTO(User user) {
		this.id = user.getId();
		this.email = user.getEmail();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.middleName = user.getMiddleName();
		this.roles = user.getRoles();
	}

	public Long getUserId() {
		return id;
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

	public Set<Role> getRoles() {
		return roles;
	}
	
}
