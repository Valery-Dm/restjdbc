/**
 * 
 */
package dmv.spring.demo.restcontroller;

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
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;
import dmv.spring.demo.model.repository.RoleRepository;
import dmv.spring.demo.restcontroller.representation.RoleDTO;
import dmv.spring.demo.restcontroller.representation.UserLinkResource;
import dmv.spring.demo.restcontroller.representation.assembler.RoleDTOAsm;
import dmv.spring.demo.restcontroller.representation.assembler.UserLinkResourceAsm;

/**
 * {@link RoleRepository} Restful endpoints. 
 * @author user
 */
@RestController
@RequestMapping("/rest/roles")
public class RoleRestController {

	@Autowired
	private RoleRepository roleRepository;
	
	@RequestMapping(path="/{shortName}", method = GET)
	public ResponseEntity<RoleDTO> getRole(@PathVariable String shortName) {
		
		Role role = roleRepository.findByShortName(shortName);
		
		return ResponseEntity.ok(new RoleDTOAsm().toResource(role));
	}
	
	@RequestMapping(path="/{shortName}/users", method = GET)
	public ResponseEntity<Resources<UserLinkResource>> 
	                    getUsers(@PathVariable String shortName, HttpServletRequest request) {
		
		Role role = roleRepository.findByShortName(shortName);
		Set<User> users = roleRepository.getUsers(role);
		
		if (users.isEmpty())
			throw new EntityDoesNotExistException("Role " + shortName + " has no users assigned to it");
		
		List<UserLinkResource> userLinks = users.stream()
		     .map(user -> new UserLinkResourceAsm().toResource(user))
		     .collect(Collectors.toList());
		
		Link link = new Link(request.getRequestURL().toString());
		
		Resources<UserLinkResource> resources = new Resources<>(userLinks, link);

		return ResponseEntity.ok(resources);
	}
}
