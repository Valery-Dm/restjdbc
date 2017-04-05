package dmv.spring.demo.rest.representation.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.rest.controller.UserRestController;
import dmv.spring.demo.rest.representation.UserLinkResource;

/**
 * Assembles {@link UserLinkResource}
 * @author dmv
 */
@Component
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
