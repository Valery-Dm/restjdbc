/**
 * 
 */
package dmv.spring.demo.restcontroller.representation.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.restcontroller.RoleRestController;
import dmv.spring.demo.restcontroller.representation.RoleDTO;

/**
 * Assembles representational object for {@link Role} entity
 * @author user
 */
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
