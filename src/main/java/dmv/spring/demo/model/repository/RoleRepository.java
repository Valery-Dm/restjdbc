/**
 * 
 */
package dmv.spring.demo.model.repository;

import java.util.Set;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;

/**
 * User's {@link Role} offers its basic methods 
 * @author user
 */
public interface RoleRepository {

	/**
	 * Find and return Role object from database
	 * with given short name, will return null if
	 * none were found.
	 * @param shortName Special acronym 
	 * @return found Role object or null
	 */
	Role findByShortName(String shortName);

	/**
	 * Return all users that share given Role
	 * @return all users with specified role
	 * @throws IllegalArgumentException if argument is null
	 */
	Set<User> getUsers(Role role);

}
