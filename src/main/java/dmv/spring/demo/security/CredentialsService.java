package dmv.spring.demo.security;

import dmv.spring.demo.model.entity.User;

/**
 * User credentials information and service methods
 * @author dmv
 */
public interface CredentialsService {

	/**
	 * It is prohibited to remove or alter the user with id 3.
	 * This is a 'least admin' protection for OpenShift container.
	 */
	static final int adminId = 3;

	/**
	 * Is this user protected from being removed or modified?
	 * @param user A user to check
	 * @return true if user is protected
	 */
	boolean isProtected(User user);

	/**
	 * Is user with given id protected from being removed or modified?
	 * @param userId A user id to check
	 * @return true if user with the id is protected
	 */
	boolean isProtected(Long userId);

	/**
	 * Return a string with hashed representation of password.
	 * <p>
	 * If provided user has no password, the random one will be
	 * generated and set into corresponding field.
	 * @param user A user whose password will be hashed
	 * @return hashed password
	 */
	String getHashedPassword(User user);

}
