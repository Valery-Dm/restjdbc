/**
 * 
 */
package dmv.spring.demo.rest.representation.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.rest.controller.UserRestController;
import dmv.spring.demo.rest.representation.UserDTO;

/**
 * @author user
 */
public class UserDTOAsm extends ResourceAssemblerSupport<User, UserDTO> {

	public UserDTOAsm() {
		super(UserRestController.class, UserDTO.class);
	}

	@Override
	public UserDTO toResource(User user) {
		UserDTO userDTO = new UserDTO(user);
		userDTO.add(linkTo(UserRestController.class)
				.slash(user.getId())
				.withSelfRel());
//		Link link = linkTo(methodOn(UserRestController.class)
//				.getRoleById(user.getId()))
//		.withRel("roles");
		return userDTO;
	}

}
