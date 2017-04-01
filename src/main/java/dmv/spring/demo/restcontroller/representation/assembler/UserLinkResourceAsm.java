/**
 * 
 */
package dmv.spring.demo.restcontroller.representation.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.restcontroller.UserRestController;
import dmv.spring.demo.restcontroller.representation.UserLinkResource;

/**
 * @author user
 *
 */
public class UserLinkResourceAsm extends ResourceAssemblerSupport<User, UserLinkResource> {

	public UserLinkResourceAsm() {
		super(UserRestController.class, UserLinkResource.class);
	}

	@Override
	public UserLinkResource toResource(User user) {
		UserLinkResource userLink = new UserLinkResource(user.getEmail());
		userLink.add(linkTo(UserRestController.class)
				.slash(user.getId()).withSelfRel());
		return userLink;
	}

}
