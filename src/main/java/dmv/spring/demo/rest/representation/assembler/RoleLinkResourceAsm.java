/**
 * 
 */
package dmv.spring.demo.rest.representation.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.rest.controller.RoleRestController;
import dmv.spring.demo.rest.representation.RoleLinkResource;

/**
 * Assembles {@link RoleLinkResource}
 * @author user
 */
public class RoleLinkResourceAsm extends ResourceAssemblerSupport<Role, RoleLinkResource> {

	public RoleLinkResourceAsm() {
		super(RoleRestController.class, RoleLinkResource.class);
	}

	@Override
	public RoleLinkResource toResource(Role role) {
		RoleLinkResource resource = new RoleLinkResource(role.getFullName());
		resource.add(linkTo(RoleRestController.class)
				.slash(role.getShortName())
				.withSelfRel());
		return resource;
	}

}
