package dmv.spring.demo.rest.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.repository.RoleRepository;
import dmv.spring.demo.rest.controller.apidocs.RoleRestApiDocs;
import dmv.spring.demo.rest.representation.RoleDTO;
import dmv.spring.demo.rest.representation.UserLinkResource;
import dmv.spring.demo.rest.representation.assembler.RoleDTOAsm;
import dmv.spring.demo.rest.representation.assembler.UserLinkResourceAsm;

/**
 * {@link RoleRepository} Restful endpoints.
 * @author dmv
 */
@RestController
@RequestMapping(path="/rest/roles", produces=APPLICATION_JSON_UTF8_VALUE)
public class RoleRestController implements RoleRestApiDocs {

	private final RoleRepository roleRepository;

	private final RoleDTOAsm roleDTOAsm;

	private final UserLinkResourceAsm userLinkAsm;

	@Autowired
	public RoleRestController(RoleRepository roleRepository,
			                  RoleDTOAsm roleDTOAsm,
			                  UserLinkResourceAsm userLinkAsm) {
		this.roleRepository = roleRepository;
		this.roleDTOAsm = roleDTOAsm;
		this.userLinkAsm = userLinkAsm;
	}

	@Override
	@RequestMapping(path="/{shortName}", method = GET)
	public ResponseEntity<RoleDTO> getRole(@PathVariable String shortName) {

		Role role = roleRepository.findByShortName(shortName);

		return ResponseEntity.ok(roleDTOAsm.toResource(role));
	}

	@Override
	@RequestMapping(path="/{shortName}/users", method = GET)
	public ResponseEntity<Resources<UserLinkResource>> getUsers(
			    		              @PathVariable String shortName,
			    		              HttpServletRequest request) {

		Role role = roleRepository.findByShortName(shortName);
		Set<User> users = roleRepository.getUsers(role);

		if (users.isEmpty())
			return ResponseEntity.noContent().build();

		List<UserLinkResource> userLinks = users.stream()
		     .map(user -> userLinkAsm.toResource(user))
		     .collect(Collectors.toList());
		Link link = new Link(request.getRequestURL().toString());
		Resources<UserLinkResource> resources = new Resources<>(userLinks, link);

		return ResponseEntity.ok(resources);
	}
}
