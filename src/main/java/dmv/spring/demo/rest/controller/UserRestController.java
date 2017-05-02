package dmv.spring.demo.rest.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.util.UriUtils.decode;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.repository.UserRepository;
import dmv.spring.demo.rest.controller.apidocs.UserRestApiDocs;
import dmv.spring.demo.rest.representation.UserDTO;
import dmv.spring.demo.rest.representation.assembler.UserDTOAsm;

/**
 * {@link UserRepository} Restful endpoints.
 * @author dmv
 */
@RestController
@RequestMapping(path="/rest/users", produces=APPLICATION_JSON_UTF8_VALUE)
public class UserRestController implements UserRestApiDocs {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserDTOAsm userDTOAsm;

	@Override
	@GetMapping(path="/{userId}")
	public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {

		User user = userRepository.findById(userId);

		return ResponseEntity.ok(userDTOAsm.toResource(user));
	}

	@Override
	@GetMapping
	public ResponseEntity<UserDTO> getUserByEmail(@RequestParam @NotNull String email)
			                                      throws UnsupportedEncodingException {

		User user = userRepository.findByEmail(decode(email, "UTF-8"));

		return ResponseEntity.ok(userDTOAsm.toResource(user));
	}

	@Override
	@PostMapping
	public ResponseEntity<UserDTO> createUser(@RequestBody @Valid User user,
			                                  HttpServletRequest request,
                                              UriComponentsBuilder uriBuilder)
                                            		  throws URISyntaxException {
		// User with generated Id, and possibly, generated password
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

		User updated = userRepository.update(user);

		String requestUrl = request.getRequestURL().toString();
		return ResponseEntity.created(new URI(requestUrl))
	                         .body(userDTOAsm.toResource(updated));
	}

	@Override
	@DeleteMapping(path="/{userId}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {

		userRepository.deleteById(userId);

		return ResponseEntity.noContent().build();
	}
}
