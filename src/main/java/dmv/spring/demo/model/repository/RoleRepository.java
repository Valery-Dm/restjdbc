package dmv.spring.demo.model.repository;

import java.util.Set;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.exceptions.AccessDataBaseException;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;

/**
 * User's {@link Role} and its basic methods
 * @author dmv
 */
public interface RoleRepository {

	/**
	 * Find and return Role object from database
	 * with given short name.
	 * @param shortName Special acronym
	 * @return found Role object
	 * @throws IllegalArgumentException if argument is null
	 * @throws AccessDataBaseException for DB issues
	 * @throws EntityDoesNotExistException if Role with given
	 *                                     name was not found
	 */
	Role findByShortName(String shortName);

	/**
	 * Return all users that share given Role, or empty Set
	 * @return all users with specified Role or empty Set
	 * @throws IllegalArgumentException if argument is null
	 * @throws AccessDataBaseException for DB issues
	 */
	Set<User> getUsers(Role role);

}
