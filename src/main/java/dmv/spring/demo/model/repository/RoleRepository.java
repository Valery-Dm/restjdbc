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
	 * Each Short Name identifier has exactly this length
	 */
	static final int SHORT_NAME_LENGTH = 3;

	/**
	 * This method provides quick pre-check for given Short Name
	 * without looking in DB for existing names. Just naswering questions:
	 * is it not null, has it proper length or does it consist of letters.
	 *
	 * @param shortName a Short name to check
	 * @return true if name is valid, false if name is null or
	 *              has wrong length or contains non-word characters
	 */
	static boolean isValidShortName(String shortName) {
		return shortName != null &&
			   shortName.length() == SHORT_NAME_LENGTH &&
			   !shortName.contains("\\W");
	}

	/**
	 * Find and return Role object with given short name (case insensitive).
	 * @param shortName Special acronym
	 * @return found Role object
	 * @throws IllegalArgumentException if argument is null
	 * @throws AccessDataBaseException for DB issues
	 * @throws EntityDoesNotExistException if Role with given
	 *                                     name was not found
	 */
	Role findByShortName(String shortName);

	/**
	 * Return all users that share given Role, or an empty Set if no such users.
	 * @param role Users with what role need to be found
	 * @return all users with specified Role or empty Set
	 * @throws IllegalArgumentException if argument is null
	 * @throws AccessDataBaseException for DB issues
	 */
	Set<User> getUsers(Role role);

}
