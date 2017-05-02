package dmv.spring.demo.rest.representation.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.rest.controller.RoleRestController;
import dmv.spring.demo.rest.representation.RoleDTO;

/**
 * Assembles Data Transfer Object for {@link Role} entity that
 * will be sent over HTTP in JSON form
 * @author dmv
 */
@Component
public class RoleDTOAsm extends ResourceAssemblerSupport<Role, RoleDTO> {

	private static final String USERS = "users";

	public RoleDTOAsm() {
		super(RoleRestController.class, RoleDTO.class);
	}

	@Override
	public RoleDTO toResource(Role role) {
		RoleDTO roleDTO = new RoleDTO(role);
		ControllerLinkBuilder link =
				linkTo(RoleRestController.class)
				.slash(roleDTO.getShortName());
		roleDTO.add(link.withSelfRel());
		roleDTO.add(link.slash(USERS)
				.withRel(USERS));
		return roleDTO;
	}


}
