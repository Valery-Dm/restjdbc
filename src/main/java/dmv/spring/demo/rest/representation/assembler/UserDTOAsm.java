package dmv.spring.demo.rest.representation.assembler;

import static java.util.Collections.emptySet;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.rest.controller.UserRestController;
import dmv.spring.demo.rest.representation.RoleLinkResource;
import dmv.spring.demo.rest.representation.UserDTO;

/**
 * Assembles {@link UserDTO}
 * @author dmv
 */
@Component
public class UserDTOAsm extends ResourceAssemblerSupport<User, UserDTO> {

	@Autowired
	private RoleLinkResourceAsm roleLinkAsm;

	public UserDTOAsm() {
		super(UserRestController.class, UserDTO.class);
	}

	@Override
	public UserDTO toResource(User user) {
		Set<RoleLinkResource> roles  = emptySet();
		if (user.getRoles() != null)
			roles = user.getRoles()
			    .stream()
			    .map(role -> roleLinkAsm.toResource(role))
			    .collect(Collectors.toSet());
		UserDTO userDTO = new UserDTO(user, roles);
		userDTO.add(linkTo(UserRestController.class)
				.slash(user.getId())
				.withSelfRel());
		return userDTO;
	}

}
