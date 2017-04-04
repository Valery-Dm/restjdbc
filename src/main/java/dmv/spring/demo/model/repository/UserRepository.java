package dmv.spring.demo.model.repository;

import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.exceptions.AccessDataBaseException;
import dmv.spring.demo.model.exceptions.EntityAlreadyExistsException;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;

/**
 * CRUD operations for {@link User} entity
 * @author dmv
 */
public interface UserRepository {

	/**
	 * Find user by its unique identifier.
	 * Will return existing user or throw an exception.
	 * @param id User's id
	 * @return User with given id
	 * @throws IllegalArgumentException if argument is null
	 * @throws EntityDoesNotExistException if user can't be found
	 * @throws AccessDataBaseException if operation was unsuccessful
	 *                                     due to internal error
	 */
	User findById(Long id);

	/**
	 * Find user by e-mail address.
	 * Will return existing user or throw an exception.
	 * @param email email address
	 * @return User with given address
	 * @throws IllegalArgumentException if argument is null
	 * @throws EntityDoesNotExistException if user can't be found
	 * @throws AccessDataBaseException if operation was unsuccessful
	 *                                     due to internal error
	 */
	User findByEmail(String email);

	/**
	 * Will store new user on persistence layer.
	 * <p>
	 * User constraints: fields 'email', 'first name' and 'last name'
	 * must be provided; 'email' must be unique
	 * @param user A new user to persist
	 * @return persisted user on success
	 * @throws IllegalArgumentException if user is null or has incomplete or wrong information
	 * @throws EntityAlreadyExistsException if user with the same
	 *                                      email already exists
	 * @throws AccessDataBaseException if operation was unsuccessful
	 *                                     due to internal error
	 */
	User create(User user);

	/**
	 * Will update existing user (found by email) with given
	 * user details (password and email fields are omitted,
	 * these are different operations).
	 * @param user An existing user with new profile
	 * @return true update was successful
	 * @throws IllegalArgumentException if user is null or has incomplete or wrong information
	 * @throws EntityDoesNotExistException if user can't be found
	 * @throws AccessDataBaseException if operation was unsuccessful
	 *                                     due to internal error
	 */
	User update(User user);

	/**
	 * Will delete existing user (found by email) from persistence layer
	 * @param user An existing user
	 * @throws IllegalArgumentException if user is null
	 * @throws EntityDoesNotExistException if user can't be found
	 * @throws AccessDataBaseException if operation was unsuccessful
	 *                                     due to internal error
	 */
	void delete(User user);

	/*
	 * getAllUsers() method and other great possibilities
	 * are not here because they were not required (say YAGNI)
	 */
}
