/**
 *
 */
package dmv.spring.demo.model.repository.jdbc;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.exceptions.AccessDataBaseException;
import dmv.spring.demo.model.exceptions.EntityAlreadyExistsException;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;
import dmv.spring.demo.model.repository.UserRepository;

/**
 * Catching variety of {@link DataAccessException} or common
 * errors, logging them and convert into corresponding exceptions
 * of {@link UserRepository} interface. All methods are wrapped
 * within {@link Transactional} interface
 * @author user
 */
@Repository
@Transactional
public class UserRepositoryExceptionAdapter implements UserRepository {

    private final Logger logger;
    private final UserRepositoryJDBC userRepository;

    @Autowired
    public UserRepositoryExceptionAdapter(JdbcTemplate jdbcTemplate) {
    	userRepository = new UserRepositoryJDBC(jdbcTemplate);
    	logger = getLogger(getClass());
    }

	@Override
	@Transactional(readOnly=true)
	public User findById(Long id) {
		Assert.notNull(id, "Can't find user with id 'null'");
		try {
			return userRepository.findById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new EntityDoesNotExistException("User with id " + id + " does not exist");
		} catch (Exception e) {
			String msg = "There was a lookup for user with id " + id
					+ ", and it was not successful";
			logger.debug(msg, e);
			throw new AccessDataBaseException(msg, e);
		}
	}

	@Override
	@Transactional(readOnly=true)
	public User findByEmail(String email) {
		Assert.notNull(email, "Email can't be 'null'");
		try {
			return userRepository.findByEmail(email);
		} catch (EmptyResultDataAccessException e) {
			throw new EntityDoesNotExistException("User " + email + " does not exist");
		} catch (Exception e) {
			String msg = "There was a lookup for user " + email
					+ ", and it was not successful";
			logger.debug(msg, e);
			throw new AccessDataBaseException(msg, e);
		}
	}

	@Override
	public User create(User user) {
		Assert.notNull(user, "Can't create user 'null'");
		try {
			User createdUser = userRepository.create(user);
			logger.info(createdUser.getEmail() + " was added to DB");
			return createdUser;
		} catch(DuplicateKeyException e) {
			throw new EntityAlreadyExistsException(user.getEmail() + " already exists");
		} catch (DataIntegrityViolationException e) {
			throw new IllegalArgumentException(user.getEmail() + " has wrong information", e);
		} catch (Exception e) {
			String msg = "There was an attempt to insert " + user.getEmail()
					+ ", and something went wrong";
			logger.debug(msg, e);
			throw new AccessDataBaseException(msg, e);
		}
	}

	@Override
	public User update(User user) {
		Assert.notNull(user, "Can't update user 'null'");
		try {
			User updatedUser = userRepository.update(user);
			logger.info(updatedUser.getEmail() + " was updated in DB");
			return updatedUser;
		} catch (EmptyResultDataAccessException e) {
			throw new EntityDoesNotExistException(user + " does not exist");
		} catch (DataIntegrityViolationException e) {
			throw new IllegalArgumentException(user + " brings wrong information for update", e);
		} catch (Exception e) {
			String msg = "There was an attempt to update " + user.getEmail()
					      + ", and something went wrong";
			logger.debug(msg, e);
			throw new AccessDataBaseException(msg, e);
		}
	}

	@Override
	public void delete(User user) {
		Assert.notNull(user, "Can't delete user 'null'");
		boolean deleted = false;
		try {
			deleted = userRepository.delete(user);
		} catch (Exception e) {
			String msg = "There was an attempt to remove " + user
					      + ", and something went wrong";
			logger.debug(msg, e);
			throw new AccessDataBaseException(msg, e);
		}
		if (!deleted)
			throw new EntityDoesNotExistException(user + " does not exist");
		logger.info(user + " was removed from DB");
	}
}
