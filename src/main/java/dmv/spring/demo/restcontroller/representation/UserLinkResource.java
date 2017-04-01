/**
 * 
 */
package dmv.spring.demo.restcontroller.representation;

import org.springframework.hateoas.ResourceSupport;

/**
 * @author user
 *
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
