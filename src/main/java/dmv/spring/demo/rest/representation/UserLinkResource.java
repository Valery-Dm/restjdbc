/**
 * 
 */
package dmv.spring.demo.rest.representation;

import org.springframework.hateoas.ResourceSupport;

import dmv.spring.demo.model.entity.User;

/**
 * {@link User} representation with email field and self link only.
 * To be used in Lists
 * @author user
 */
public class UserLinkResource extends ResourceSupport {

	private final String email;

	public UserLinkResource(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}
}
