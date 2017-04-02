/**
 *
 */
package dmv.spring.demo.rest.controller;

import static org.springframework.web.util.UriUtils.decode;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.repository.UserRepository;
import dmv.spring.demo.rest.representation.UserDTO;
import dmv.spring.demo.rest.representation.assembler.UserDTOAsm;

/**
 * {@link UserRepository} Restful endpoints.
 * @author user
 */
@RestController
@RequestMapping("/rest/users")
public class UserRestController {


	@Autowired
	private UserRepository userRepository;

	@GetMapping(path="/{userId}")
	public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
		User user = userRepository.findById(userId);
		return ResponseEntity.ok()
				             .body(new UserDTOAsm().toResource(user));
	}

	@GetMapping
	public ResponseEntity<UserDTO> getUserByEmail(@RequestParam String email)
			                             throws UnsupportedEncodingException {
		User user = userRepository.findByEmail(decode(email, "UTF-8"));
		return ResponseEntity.ok()
				             .body(new UserDTOAsm().toResource(user));
	}

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
	                         .body(new UserDTOAsm().toResource(created));
	}

	@PutMapping(path="/{userId}")
	public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId,
			                                  @RequestBody @Valid User user,
			                                  HttpServletRequest request) throws URISyntaxException {
		// User fields: id, email and password - won't be updated by this request
		User updated = userRepository.update(user);
		String requestUrl = request.getRequestURL().toString();
		return ResponseEntity.created(new URI(requestUrl))
	                         .body(new UserDTOAsm().toResource(updated));
	}

	@DeleteMapping(path="/{userId}")
	public ResponseEntity<UserDTO> deleteUser(@PathVariable Long userId) {
		User user = userRepository.findById(userId);
		userRepository.delete(user);
		return ResponseEntity.noContent().build();
	}
}
