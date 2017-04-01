/**
 * 
 */
package dmv.spring.demo.rest.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	
	@RequestMapping(path="/{userId}", method = GET)
	public ResponseEntity<UserDTO> getRoleById(@PathVariable Long userId) {
		User user = userRepository.findById(userId);
		return ResponseEntity.ok()
				             .body(new UserDTOAsm().toResource(user));
	}
	
	@RequestMapping(method = GET)
	public ResponseEntity<UserDTO> getRoleByEmail(@RequestParam String email) {
		User user = userRepository.findByEmail(email);
		return ResponseEntity.ok()
				             .body(new UserDTOAsm().toResource(user));
	}
}
