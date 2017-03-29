/**
 * 
 */
package dmv.spring.demo.model.repository;

import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.exceptions.EntityAlreadyExistsException;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;

/**
 * CRUD operations for {@link User} entity
 * @author user
 */
public interface UserRepository {

	/**
	 * Find by Primary Key, e-mail address in this case.
	 * Will return existed user or null if not found.
	 * The argument can be null or empty.
	 * @param email email address
	 * @return User with given address or null
	 */
	User findByEmail(String email);
	
	/**
	 * Will store given user on persistence layer if 
	 * none exists.
	 * @param user A new user to create
	 * @throws EntityAlreadyExistsException if user with the same
	 *                                      email already exists
	 */
	void create(User user);
	
	/**
	 * Will update existed user (found by email) with given
	 * given constraints
	 * @param user A user with new info
	 * @return true update was successful
	 * @throws EntityDoesNotExistException if user can't be found 
	 */
	boolean update(User user);
	
	/**
	 * Will delete existed user (found by email) from persistence layer
	 * @param user A user with new info
	 * @return true update was successful
	 * @throws EntityDoesNotExistException if user can't be found 
	 */
	boolean delete(User user);
	
	/*
	 * getAllUsers() method and other great possibilities 
	 * are not here because they were not required (say YAGNI)
	 */
}
