package dmv.spring.demo.model.repository;


import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.exceptions.EntityAlreadyExistsException;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;

/**
 * The main purpose of this interface is to deal with
 * Credentials related queries: getting or updating.
 * @author dmv
 */
public interface CredentialsRepository {

	/**
	 * Find user by email address and return all details, including password.
	 * This is for authentication purposes.
	 * @param email User's identifier, unique email address
	 * @return User with password field
	 * @throws EntityDoesNotExistException if user can't be found
	 */
	User getCredentials(String email);
	
	/**
	 * Change existing user's password with the new one.
	 * Provided User object must contain correct existing password,
	 * it will be checked first. This method returns nothing on
	 * success or throws an exception on failure.
	 * <p> 
	 * This method won't generate new password in case if given
	 * is null or empty - the exception will be thrown instead.
	 * @param user User with existing password
	 * @param newPassword A new password to be set
	 * @throws EntityDoesNotExistException if user can't be found
	 * @throws IllegalArgumentException if provided user does not have correct details:
	 *                                  whether email or id or password is absent,
	 *                                  or given password is wrong for the user. 
	 *                                  If newPassword is null or empty.
	 */
	void changePassword(User user, String newPassword);
	
	/**
	 * Change existing user's email address. New email address
	 * supposed to be unique. This method returns nothing on
	 * success or throws an exception on failure.
	 * @param user Existing user
	 * @param newEmail New email address
	 * @throws EntityAlreadyExistsException if given email already exists
	 * @throws EntityDoesNotExistException if user can't be found
	 * @throws IllegalArgumentException if provided user does not have correct details:
	 *                                  whether email or id is absent, or the new email
	 *                                  is null or empty
	 */
	void changeEmail(User user, String newEmail);
}
