package dmv.spring.demo.model.repository.jdbc;

import static org.slf4j.LoggerFactory.getLogger;

import java.security.SecureRandom;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
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
 * @author dmv
 */
@Repository
@Transactional
public class UserRepositoryExceptionAdapter implements UserRepository {

	private static final String USER_NULL = "User can't be 'null'";

	private static final String EMAIL_NULL = "Email can't be 'null'";

	private static final String ID_NULL = "User's id can't be 'null'";

	private final Logger logger = getLogger(UserRepositoryExceptionAdapter.class);

    private final UserRepositoryJDBC userRepository;

    @Autowired
    public UserRepositoryExceptionAdapter(JdbcTemplate jdbcTemplate,
                                          PasswordEncoder passwordEncoder,
                                          SecureRandom secureRandom) {
    	userRepository = new UserRepositoryJDBC(jdbcTemplate, passwordEncoder, secureRandom);
    }

	@Override
	@Transactional(readOnly=true)
	public User findById(Long id) {
		Assert.notNull(id, ID_NULL);
		try {

			User user = userRepository.findById(id);
			logger.debug("User {} was found in DB", id);
			return user;

		} catch (EmptyResultDataAccessException e) {
			logger.debug("User {} was not found in DB", id);
			throw new EntityDoesNotExistException("User with id " + id + " does not exist");
		} catch (Exception e) {
			String msg = "There was a lookup for user with id " + id
					+ ", and it was not successful";
			logger.error(msg, e);
			throw new AccessDataBaseException(msg, e);
		}
	}

	@Override
	@Transactional(readOnly=true)
	public User findByEmail(String email) {
		Assert.notNull(email, EMAIL_NULL);
		try {

			User user = userRepository.findByEmail(email);
			logger.debug("User {} was found in DB", email);
			return user;

		} catch (EmptyResultDataAccessException e) {
			logger.debug("User {} was not found in DB", email);
			throw new EntityDoesNotExistException("User " + email + " does not exist");
		} catch (Exception e) {
			String msg = "There was a lookup for user " + email
					+ ", and it was not successful";
			logger.error(msg, e);
			throw new AccessDataBaseException(msg, e);
		}
	}

	@Override
	@Transactional(readOnly=true)
	public User getCredentials(String email) {
		Assert.notNull(email, EMAIL_NULL);
		try {

			User user = userRepository.getCredentials(email);
			logger.debug("User {} credentials was found in DB", email);
			return user;

		} catch (EmptyResultDataAccessException e) {
			logger.debug("User {} credentials was not found in DB", email);
			throw new EntityDoesNotExistException("User " + email + " does not exist");
		} catch (Exception e) {
			String msg = "There was a lookup for user " + email
					+ ", and it was not successful";
			logger.error(msg, e);
			throw new AccessDataBaseException(msg, e);
		}
	}

	@Override
	public User create(User user) {
		Assert.notNull(user, USER_NULL);
		try {

			User createdUser = userRepository.create(user);
			logger.debug("User {} was added to DB", createdUser.getEmail());
			return createdUser;

		} catch(DuplicateKeyException e) {
			String msg = "User " + user.getEmail() + " already exists in DB";
			logger.debug(msg);
			throw new EntityAlreadyExistsException(msg);
		} catch (DataIntegrityViolationException e) {
			String msg = "User " + user.getEmail() + " has wrong information";
			logger.debug(msg);
			throw new IllegalArgumentException(msg, e);
		} catch (Exception e) {
			String msg = "There was an attempt to insert " + user.getEmail()
					+ ", and something went wrong";
			logger.error(msg, e);
			throw new AccessDataBaseException(msg, e);
		}
	}

	@Override
	public User update(User user) {
		Assert.notNull(user, USER_NULL);
		Assert.notNull(user.getId(), ID_NULL);
		try {

			User updatedUser = userRepository.update(user);
			logger.debug("User {} was updated in DB", updatedUser.getId());
			return updatedUser;

		} catch (EmptyResultDataAccessException e) {
			String msg = "User " + user.getId() + " does not exist";
			logger.debug(msg);
			throw new EntityDoesNotExistException(msg);
		} catch (DataIntegrityViolationException e) {
			String msg = "User " + user.getId() + " brings wrong information for update";
			logger.debug(msg);
			throw new IllegalArgumentException(msg, e);
		} catch (Exception e) {
			String msg = "There was an attempt to update " + user.getId()
					      + ", and something went wrong";
			logger.error(msg, e);
			throw new AccessDataBaseException(msg, e);
		}
	}

	@Override
	public void delete(User user) {
		Assert.notNull(user, USER_NULL);
		Assert.notNull(user.getId(), ID_NULL);
		boolean deleted = false;
		try {

			deleted = userRepository.delete(user);

		} catch (Exception e) {
			String msg = "There was an attempt to remove " + user.getId()
					      + ", and something went wrong";
			logger.error(msg, e);
			throw new AccessDataBaseException(msg, e);
		}
		if (!deleted) {
			String msg = "User " + user.getId() + " does not exist";
			logger.debug(msg);
			throw new EntityDoesNotExistException(msg);
		}
		logger.debug("User {} was removed from DB", user.getId());
	}
}
