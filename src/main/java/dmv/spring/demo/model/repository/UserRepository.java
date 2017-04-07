package dmv.spring.demo.model.repository;

import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.exceptions.AccessDataBaseException;
import dmv.spring.demo.model.exceptions.EntityAlreadyExistsException;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;

/**
 * CRUD operations for {@link User} entity.
 * For all methods where receiving the User object as a parameter
 * user id field is mandatory, otherwise that user can't be treated.
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
	 * Will store new user on persistence layer. Id will be generated and returned.
	 * <p>
	 * User constraints: fields 'email', 'first name' and 'last name'
	 * must be provided; 'email' must be unique.
	 * If password was not given it will be generated and returned with
	 * the created object.
	 * <p>
	 * For user's roles only 'shortName' parameter is needed.
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
	 * Will update existing user (found by id) with given
	 * user details (password and email fields are omitted,
	 * these are different operations).
	 * <p>
	 * For user's roles only 'shortName' parameter is needed.
	 * If it is absent or the role with given name does not exist
	 * in database the {@link IllegalArgumentException} will be thrown.
	 * @param user An existing user with new profile
	 * @return true update was successful
	 * @throws IllegalArgumentException if user or its id is null or user has
	 *                                  incomplete or wrong information
	 * @throws EntityDoesNotExistException if user can't be found
	 * @throws AccessDataBaseException if operation was unsuccessful
	 *                                     due to internal error
	 */
	User update(User user);

	/**
	 * Will delete existing user (found by id) from persistence layer
	 * @param user An existing user
	 * @throws IllegalArgumentException if user or its id is null
	 * @throws EntityDoesNotExistException if user can't be found
	 * @throws AccessDataBaseException if operation was unsuccessful
	 *                                     due to internal error
	 */
	void delete(User user);

	/**
	 * Find user by email address and return with that email and password
	 * fields only. For authentication purposes.
	 * @param email User's identifier, unique email address
	 * @param password what to compare against User's password
	 * @return User with email and password if exists
	 * @throws EntityDoesNotExistException if user can't be found
	 */
	User getCredentials(String email);

	/*
	 * getAllUsers() method and other great possibilities
	 * are not here because they were not required (say YAGNI)
	 */
}
