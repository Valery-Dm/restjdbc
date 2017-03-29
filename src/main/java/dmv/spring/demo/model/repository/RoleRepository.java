/**
 * 
 */
package dmv.spring.demo.model.repository;

import java.util.Set;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;

/**
 * @author user
 *
 */
public interface RoleRepository {

	/**
	 * Return all users that share given Role
	 * @return all users with specified role
	 * @throws IllegalArgumentException if argument is null
	 */
	Set<User> getUsers(Role role);

}
