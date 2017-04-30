package dmv.spring.demo.model.repository;

import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.exceptions.AccessDataBaseException;
import dmv.spring.demo.model.exceptions.EntityAlreadyExistsException;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;

/**
 * CRUD operations for {@link User} entity.
 * For all methods where receiving the User object as a parameter
 * user id field is mandatory, otherwise that user can't be serviced.
 * @author dmv
 */
public interface UserRepository {

	/**
	 * Find user by its unique identifier.
	 * Will return existing user or throw an exception.
	 * @param id User's id
	 * @return User with given id
	 * @throws IllegalArgumentException if argument is null
	 * @throws EntityDoesNotExistException if user with the id can't be found
	 * @throws AccessDataBaseException if operation was unsuccessful
	 *                                     due to internal error
	 */
	User findById(Long id);

	/**
	 * Find user by e-mail address.
	 * Will return existing user or throw an exception.
	 * @param email email address
	 * @return User with given address
	 * @throws IllegalArgumentException if argument is null or empty
	 * @throws EntityDoesNotExistException if user with the email can't be found
	 * @throws AccessDataBaseException if operation was unsuccessful
	 *                                     due to internal error
	 */
	User findByEmail(String email);

	/**
	 * Store new user on persistence layer. Id will be generated and returned.
	 * <p>
	 * User constraints: fields 'email', 'first name' and 'last name'
	 * must be provided; 'email' must be unique.
	 * If password was not given it will be generated and returned with
	 * the created object, otherwise password field will be null (for safety reasons).
	 * <p>
	 * For user's roles the 'shortName' parameter is needed.
	 * If it is absent or the role with given name does not exist
	 * in database the {@link IllegalArgumentException} will be thrown.
	 * @param user A new user to persist
	 * @return persisted user on success
	 * @throws IllegalArgumentException if user or its id is null or user has
	 *                                  incomplete or wrong information
	 * @throws EntityAlreadyExistsException if user with the same
	 *                                      email already exists
	 * @throws AccessDataBaseException if operation was unsuccessful
	 *                                     due to internal error
	 */
	User create(User user);

	/**
	 * Update existing user (found by id) with given
	 * user details (password and email fields are omitted,
	 * these are different operations).
	 * <p>
	 * For user's roles the 'shortName' parameter is needed.
	 * If it is absent or the role with given name does not exist
	 * in database the {@link IllegalArgumentException} will be thrown.
	 * @param user An existing user with new details
	 * @return Update user (without a password)
	 * @throws IllegalArgumentException if user or its id is null or user has
	 *                                  incomplete or wrong information
	 * @throws EntityDoesNotExistException if user can't be found
	 * @throws AccessDataBaseException if operation was unsuccessful
	 *                                     due to internal error
	 */
	User update(User user);

	/**
	 * Delete existing user (found by id) from persistence layer
	 * @param user A user to be removed
	 * @throws IllegalArgumentException if user or its id is null
	 * @throws EntityDoesNotExistException if user can't be found
	 * @throws AccessDataBaseException if operation was unsuccessful
	 *                                     due to internal error
	 */
	void delete(User user);

	/**
	 * Delete existing user (found by id) from persistence layer
	 * @param id An id of user that needs to be removed
	 * @throws IllegalArgumentException if id is null
	 * @throws EntityDoesNotExistException if user can't be found
	 * @throws AccessDataBaseException if operation was unsuccessful
	 *                                     due to internal error
	 */
	void deleteById(Long id);

	/*
	 * getAllUsers() method and other great possibilities
	 * are not here because they were not required (say YAGNI)
	 */
}
