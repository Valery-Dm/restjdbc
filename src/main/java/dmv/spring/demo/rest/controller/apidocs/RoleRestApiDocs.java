package dmv.spring.demo.rest.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;

import dmv.spring.demo.rest.representation.RoleDTO;
import dmv.spring.demo.rest.representation.UserLinkResource;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Swagger (API Documentation) annotations
 * @author dmv
 */
public interface RoleRestApiDocs {

	@ApiOperation(value="Find role by its short name", notes="The short name is a special (and unique) acronym for each role.")
	@ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of role", response = RoleDTO.class),
            @ApiResponse(code = 404, message = "Role with given name does not exist") })
	ResponseEntity<RoleDTO> getRole(
			@ApiParam(value="short role's name: ADM for Administrator, USR for User, DEV for Developer etc.", required=true)
	        String shortName);

	@ApiOperation(value="Find users with given role", notes="Returns a list of users that have specified role")
	@ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of users", response = Resources.class),
            @ApiResponse(code = 204, message = "Users with given role were not found"),
            @ApiResponse(code = 404, message = "Role with given name does not exist") })
	ResponseEntity<Resources<UserLinkResource>> getUsers(
			@ApiParam(value="short role's name: ADM for Administrator, USR for User, DEV for Developer etc.", required=true)
	        String shortName,
	        HttpServletRequest request);

}