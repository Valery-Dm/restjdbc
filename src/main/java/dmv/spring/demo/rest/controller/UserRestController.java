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
import dmv.spring.demo.rest.controller.apidocs.UserRestApiDocs;
import dmv.spring.demo.rest.representation.UserDTO;
import dmv.spring.demo.rest.representation.assembler.UserDTOAsm;

/**
 * {@link UserRepository} Restful endpoints.
 * @author dmv
 */
@RestController
@RequestMapping(path="/rest/users", produces="application/json")
public class UserRestController implements UserRestApiDocs {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserDTOAsm userDTOAsm;

	@Override
	@GetMapping(path="/{userId}")
	public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
		User user = userRepository.findById(userId);
		return ResponseEntity.ok()
				             .body(userDTOAsm.toResource(user));
	}

	@Override
	@GetMapping
	public ResponseEntity<UserDTO> getUserByEmail(@RequestParam @Valid String email)
			                                      throws UnsupportedEncodingException {

		User user = userRepository.findByEmail(decode(email, "UTF-8"));
		return ResponseEntity.ok()
				             .body(userDTOAsm.toResource(user));
	}

	@Override
	@PostMapping
	public ResponseEntity<UserDTO> createUser(@RequestBody @Valid User user,
			                                  HttpServletRequest request,
                                              UriComponentsBuilder uriBuilder)
                                            		  throws URISyntaxException {
		// It's a Domain Tier's responsibility to provide legal and well formed requests to the Persistence layer.
		getUserRoles(user);
		// User with generated Id
		User created = userRepository.create(user);
		URI location = uriBuilder.path(request.getRequestURI() + "/{id}")
				                 .buildAndExpand(created.getId())
				                 .toUri();
		return ResponseEntity.created(location)
	                         .body(userDTOAsm.toResource(created));
	}

	@Override
	@PutMapping(path="/{userId}")
	public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId,
			                                  @RequestBody @Valid User user,
			                                  HttpServletRequest request)
			                                		  throws URISyntaxException {
		// Simple defense from forgery requests
		user.setId(userId);

		// It's a Domain Tier's responsibility to provide legal and well formed requests to the Persistence layer.
		getUserRoles(user);

		User updated = userRepository.update(user);

		String requestUrl = request.getRequestURL().toString();
		return ResponseEntity.created(new URI(requestUrl))
	                         .body(userDTOAsm.toResource(updated));
	}

	@Override
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
