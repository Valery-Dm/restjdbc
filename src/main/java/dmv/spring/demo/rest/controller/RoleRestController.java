package dmv.spring.demo.rest.controller;

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
import dmv.spring.demo.rest.representation.RoleDTO;
import dmv.spring.demo.rest.representation.UserLinkResource;
import dmv.spring.demo.rest.representation.assembler.RoleDTOAsm;
import dmv.spring.demo.rest.representation.assembler.UserLinkResourceAsm;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * {@link RoleRepository} Restful endpoints.
 * @author dmv
 */
@RestController
@RequestMapping(path="/rest/roles", produces="application/json")
public class RoleRestController {

	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private RoleDTOAsm roleDTOAsm;
	
	@Autowired
	private UserLinkResourceAsm userLinkAsm;

	@ApiOperation(value="Find role by its short name", notes="The short name is a special (and unique) acronym for each role.")
	@ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of role", response = RoleDTO.class),
            @ApiResponse(code = 404, message = "Role with given name does not exist") })
	@RequestMapping(path="/{shortName}", method = GET)
	public ResponseEntity<RoleDTO> getRole(
			@ApiParam(value="short role's name: ADM for Administrator, USR for User, DEV for Developer etc.", required=true)
			@PathVariable String shortName) {
		Role role = roleRepository.findByShortName(shortName);
		return ResponseEntity.ok(roleDTOAsm.toResource(role));
	}

	@ApiOperation(value="Find users with given role", notes="Returns a list of users that have specified role")
	@ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of users", response = Resources.class),
            @ApiResponse(code = 204, message = "Users with given role were not found") })
	@RequestMapping(path="/{shortName}/users", method = GET)
	public ResponseEntity<Resources<UserLinkResource>> getUsers(
    		@ApiParam(value="short role's name: ADM for Administrator, USR for User, DEV for Developer etc.", required=true)
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
