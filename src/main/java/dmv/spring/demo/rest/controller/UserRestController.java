package dmv.spring.demo.rest.controller;

import static org.springframework.web.util.UriUtils.decode;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.repository.RoleRepository;
import dmv.spring.demo.model.repository.UserRepository;
import dmv.spring.demo.rest.representation.UserDTO;
import dmv.spring.demo.rest.representation.assembler.UserDTOAsm;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * {@link UserRepository} Restful endpoints.
 * @author dmv
 */
@RestController
@RequestMapping(path="/rest/users", produces="application/json")
public class UserRestController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserDTOAsm userDTOAsm;

	@ApiOperation(value="Find user by id", notes="Usually these kind of links will be created by API for cross-referencing, but you can use it manually if you know the exact id of user which info-page you need to get. Password won't be returned by this query")
	@ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of user", response = UserDTO.class),
            @ApiResponse(code = 404, message = "User with given id was not found") })
	@GetMapping(path="/{userId}")
	public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
		User user = userRepository.findById(userId);
		return ResponseEntity.ok()
				             .body(userDTOAsm.toResource(user));
	}

	@ApiOperation(value="Find user by email address", notes="Specify url encoded email address for this query. Example (say, user address is some.user@mail.address): /rest/users/?email=some.user%40mail.address. Password won't be returned by this query")
	@ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of user", response = UserDTO.class),
            @ApiResponse(code = 404, message = "User with given email was not found") })
	@GetMapping
	public ResponseEntity<UserDTO> getUserByEmail(
			@ApiParam(value="Email address of an existing user", required=true, example="some.user@mail")
			@RequestParam @Valid String email) throws UnsupportedEncodingException {
		User user = userRepository.findByEmail(decode(email, "UTF-8"));
		return ResponseEntity.ok()
				             .body(userDTOAsm.toResource(user));
	}

	@ApiOperation(value="Create new user", notes="This endpoint accepts json-formatted object with user details. See Data Type below (tab 'Model') for additional information")
	@ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful creation of given user", response = UserDTO.class),
            @ApiResponse(code = 409, message = "User with the same email is already exists"),
            @ApiResponse(code = 400, message = "User details lack required field or are not in valid form")})
	@PostMapping
	public ResponseEntity<UserDTO> createUser(@RequestBody @Valid User user,
			                                  HttpServletRequest request,
                                              UriComponentsBuilder uriBuilder) throws URISyntaxException {
		// User with generated Id
		User created = userRepository.create(user);
		URI location = uriBuilder.path(request.getRequestURI() + "/{id}")
				                 .buildAndExpand(created.getId())
				                 .toUri();
		return ResponseEntity.created(location)
	                         .body(userDTOAsm.toResource(created));
	}

	@ApiOperation(value="Update existing user details", notes="Only firstName, lastName, middleName or userRoles will be updated via this query. It's not supposed for changing email and/or password. But you are still required to provide valid email address")
	@ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful update of user", response = UserDTO.class),
            @ApiResponse(code = 400, message = "User details lack required field or are not in valid form"),
            @ApiResponse(code = 404, message = "User with given id was not found, or one of User's role does not exist") })
	@PutMapping(path="/{userId}")
	public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId,
			                                  @RequestBody @Valid User user,
			                                  HttpServletRequest request) throws URISyntaxException {
		// Simple defense from forgery requests
		user.setId(userId);

		// It's a Domain Tier's responsibility to provide legal and well formed requests to the Persistence layer.
		getUserRoles(user);

		User updated = userRepository.update(user);

		String requestUrl = request.getRequestURL().toString();
		return ResponseEntity.created(new URI(requestUrl))
	                         .body(userDTOAsm.toResource(updated));
	}

	@ApiOperation(value="Delete existing user", notes="Specified user will be completely removed from database")
	@ApiResponses(value = {
	        @ApiResponse(code = 204, message = "Successful deletion of user", response = Void.class),
	        @ApiResponse(code = 404, message = "User with given id was not found") })
	@DeleteMapping(path="/{userId}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
		User user = userRepository.findById(userId);
		userRepository.delete(user);
		return ResponseEntity.noContent().build();
	}

	private void getUserRoles(User user) {
		// Here we are getting real Role objects from an appropriate resource
		Set<Role> received = user.getRoles();
		if (received == null || received.size() == 0) return;
		Set<Role> actual = new HashSet<>();
		received.forEach(role ->
		              actual.add(roleRepository.findByShortName(role.getShortName())));
		user.setRoles(actual);
	}
}
