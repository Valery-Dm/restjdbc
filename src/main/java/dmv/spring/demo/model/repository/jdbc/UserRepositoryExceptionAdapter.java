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
 * @author dmv
 */
@Repository
@Transactional
public class UserRepositoryExceptionAdapter implements UserRepository {

	private static final String USER_NULL = "User can't be 'null'";

	private static final String EMAIL_NULL = "Email can't be 'null'";

	private static final String ID_NULL = "User's id can't be 'null'";

	private static final Logger logger = getLogger(UserRepositoryExceptionAdapter.class);

    private final UserRepositoryJDBC userRepository;

    @Autowired
    public UserRepositoryExceptionAdapter(JdbcTemplate jdbcTemplate) {
    	userRepository = new UserRepositoryJDBC(jdbcTemplate);
    }

	@Override
	@Transactional(readOnly=true)
	public User findById(Long id) {
		Assert.notNull(id, ID_NULL);
		boolean debug = logger.isDebugEnabled();
		try {

			User user = userRepository.findById(id);
			if (debug)
				logger.debug("User " + id + " was found in DB");
			return user;

		} catch (EmptyResultDataAccessException e) {
			if (debug)
				logger.debug("User " + id + " was not found in DB");
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
		boolean debug = logger.isDebugEnabled();
		try {

			User user = userRepository.findByEmail(email);
			if (debug)
				logger.debug("User " + email + " was found in DB");
			return user;

		} catch (EmptyResultDataAccessException e) {
			if (debug)
				logger.debug("User " + email + " was not found in DB");
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
		boolean debug = logger.isDebugEnabled();
		try {

			User user = userRepository.getCredentials(email);
			if (debug)
				logger.debug("User " + email + " credentials was found in DB");
			return user;

		} catch (EmptyResultDataAccessException e) {
			if (debug)
				logger.debug("User " + email + " credentials was not found in DB");
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
		boolean debug = logger.isDebugEnabled();
		try {

			User createdUser = userRepository.create(user);
			if (debug) logger.info("User " + createdUser.getEmail() + " was added to DB");
			return createdUser;

		} catch(DuplicateKeyException e) {
			String msg = "User " + user.getEmail() + " already exists in DB";
			if (debug) logger.info(msg);
			throw new EntityAlreadyExistsException(msg);
		} catch (DataIntegrityViolationException e) {
			String msg = "User " + user.getEmail() + " has wrong information";
			if (debug) logger.info(msg);
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
		boolean debug = logger.isDebugEnabled();
		try {

			User updatedUser = userRepository.update(user);
			if (debug) logger.info("User " + updatedUser.getId() + " was updated in DB");
			return updatedUser;

		} catch (EmptyResultDataAccessException e) {
			String msg = "User " + user.getId() + " does not exist";
			if (debug) logger.info(msg);
			throw new EntityDoesNotExistException(msg);
		} catch (DataIntegrityViolationException e) {
			String msg = "User " + user.getId() + " brings wrong information for update";
			if (debug) logger.info(msg);
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
		boolean debug = logger.isDebugEnabled();
		boolean deleted = false;
		try {

			deleted = userRepository.delete(user);
			if (deleted && debug)
				logger.info("User " + user.getId() + " was removed from DB");

		} catch (Exception e) {
			String msg = "There was an attempt to remove " + user.getId()
					      + ", and something went wrong";
			logger.error(msg, e);
			throw new AccessDataBaseException(msg, e);
		}

		String msg = "User " + user.getId() + " does not exist";
		if (debug) logger.info(msg);
		throw new EntityDoesNotExistException(msg);
	}
}
