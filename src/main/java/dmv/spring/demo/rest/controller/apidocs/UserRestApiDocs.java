package dmv.spring.demo.rest.controller.apidocs;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.rest.representation.UserDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Swagger API Documentation.
 * Each method is annotated with Springfox annotations (Swagger generator)
 * @author dmv
 */
public interface UserRestApiDocs {

	@ApiOperation(value="Find user by id", notes="Usually these kind of links will be created by API for cross-referencing, but you can use it manually if you know the exact id of user which info-page you need to get. Password won't be returned by this query")
	@ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of user", response = UserDTO.class),
            @ApiResponse(code = 401, message = "Unauthenticated"),
    		@ApiResponse(code = 403, message = "Unauthorised"),
            @ApiResponse(code = 404, message = "User with given id was not found") })
	ResponseEntity<UserDTO> getUserById(Long userId);

	@ApiOperation(value="Find user by email address", notes="Specify url encoded email address for this query. Example (say, user address is some.user@mail.address): /rest/users/?email=some.user%40mail.address. Password won't be returned by this query")
	@ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of user", response = UserDTO.class),
            @ApiResponse(code = 401, message = "Unauthenticated"),
    		@ApiResponse(code = 403, message = "Unauthorised"),
            @ApiResponse(code = 404, message = "User with given email was not found") })
	ResponseEntity<UserDTO> getUserByEmail(
			@ApiParam(value="Email address of an existing user", required=true, example="some.user@mail")
			String email) throws UnsupportedEncodingException;

	@ApiOperation(value="Create new user", notes="This endpoint accepts json-formatted object with user details. See Data Type below (tab 'Model') for additional information")
	@ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful creation of given user", response = UserDTO.class),
            @ApiResponse(code = 401, message = "Unauthenticated"),
    		@ApiResponse(code = 403, message = "Unauthorised"),
            @ApiResponse(code = 409, message = "User with the same email is already exists"),
            @ApiResponse(code = 400, message = "User details lack required field or are not in valid form")})
	ResponseEntity<UserDTO> createUser(User user, HttpServletRequest request, UriComponentsBuilder uriBuilder)
			throws URISyntaxException;

	@ApiOperation(value="Update existing user details", notes="Only firstName, lastName, middleName or userRoles will be updated via this query. It's not supposed for changing email and/or password. But you are still required to provide valid email address")
	@ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful update of user", response = UserDTO.class),
            @ApiResponse(code = 400, message = "User details lack required field or are not in valid form"),
            @ApiResponse(code = 401, message = "Unauthenticated"),
    		@ApiResponse(code = 403, message = "Unauthorised"),
            @ApiResponse(code = 404, message = "User with given id was not found, or one of User's role does not exist") })
	ResponseEntity<UserDTO> updateUser(Long userId, User user, HttpServletRequest request) throws URISyntaxException;

	@ApiOperation(value="Delete existing user", notes="Specified user will be completely removed from database")
	@ApiResponses(value = {
	        @ApiResponse(code = 204, message = "Successful deletion of user", response = Void.class),
            @ApiResponse(code = 401, message = "Unauthenticated"),
    		@ApiResponse(code = 403, message = "Unauthorised"),
	        @ApiResponse(code = 404, message = "User with given id was not found") })
	ResponseEntity<Void> deleteUser(Long userId);

}