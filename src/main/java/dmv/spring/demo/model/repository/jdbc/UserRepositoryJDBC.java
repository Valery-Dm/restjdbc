/**
 * 
 */
package dmv.spring.demo.model.repository.jdbc;

import static dmv.spring.demo.model.repository.jdbc.Mappers.ROLE_MAPPER;
import static dmv.spring.demo.model.repository.jdbc.Mappers.USER_MAPPER;
import static dmv.spring.demo.model.repository.jdbc.UserQueries.CREATE;
import static dmv.spring.demo.model.repository.jdbc.UserQueries.DELETE;
import static dmv.spring.demo.model.repository.jdbc.UserQueries.FIND_BY_EMAIL;
import static dmv.spring.demo.model.repository.jdbc.UserQueries.FIND_USER_ROLES;
import static dmv.spring.demo.model.repository.jdbc.UserQueries.GET_ID;
import static dmv.spring.demo.model.repository.jdbc.UserQueries.UPDARE;
import static org.slf4j.LoggerFactory.getLogger;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.exceptions.AccessDataBaseException;
import dmv.spring.demo.model.exceptions.EntityAlreadyExistsException;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;
import dmv.spring.demo.model.repository.UserRepository;

/**
 * Querying table USER via JDBC according to
 * {@link UserRepository} specification
 * @author user
 */
@Repository
@Transactional
public class UserRepositoryJDBC implements UserRepository {

    private final Logger logger = getLogger(getClass());
	
	/* For password auto generation */
	private final SecureRandom random = new SecureRandom();
	/* For password hashing */
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	/* Spring helper class for JDBC queries */
	private final JdbcTemplate jdbcTemplate;

	@Autowired
    public UserRepositoryJDBC(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

	@Override
	public User findByEmail(String email) {
		Assert.notNull(email, "Email can't be 'null'");
		User user = null;
		try {
			
			user = jdbcTemplate.queryForObject(FIND_BY_EMAIL.getQuery(), 
					                           USER_MAPPER, email);
			populateRoles(jdbcTemplate.query(FIND_USER_ROLES.getQuery(), 
					                         ROLE_MAPPER, user.getId()), user);
			
		} catch (EmptyResultDataAccessException e) {
			throw new EntityDoesNotExistException("User " + email + " does not exist");
		} catch (Exception e) {
			String msg = "There was a lookup for user " + email
					+ ", and it was not successful";
			processException(e, msg);
		}
		return user;
	}

	@Override
	public User create(User user) {
		Assert.notNull(user, "Can't create user 'null'");
		try {
			
			jdbcTemplate.update(CREATE.getQuery(), 
					user.getEmail(), user.getFirstName(), 
					user.getLastName(), user.getMiddleName(),
					getHashedPassword(user));
			// get auto-generated ID
			user.setId(jdbcTemplate.queryForObject(GET_ID.getQuery(), 
						                           Long.class, user.getEmail()));
			addUserRoles(user);
			logger.info(user + " was added to DB");
			
		} catch(DuplicateKeyException e) {
			throw new EntityAlreadyExistsException(user + " already exists");
		} catch (DataIntegrityViolationException e) {
			throw new IllegalArgumentException(user + " has wrong information", e);
		} catch (Exception e) {
			String msg = "There was an attempt to insert " + user
					+ ", and something went wrong";
			processException(e, msg);
		}
		return user;
	}

	@Override
	public User update(User user) {
		Assert.notNull(user, "Can't update user 'null'");
		User found = findByEmail(user.getEmail());
		try {
			updateUserDetails(user);
			updateUserRoles(user, found);
			logger.info(user + " was updated in DB");
		} catch (DataIntegrityViolationException e) {
			throw new IllegalArgumentException(user + " brings wrong information for update", e);
		} catch (Exception e) {
			String msg = "There was an attempt to update " + user
					      + ", and something went wrong";
			processException(e, msg);
		}
		return user;
	}

	@Override
	public void delete(User user) {
		Assert.notNull(user, "Can't delete user 'null'");
		try {
			
			/* With Cascade deletion in ROLE_USERS table */
			if (jdbcTemplate.update(DELETE.getQuery(), user.getEmail()) > 0) {
				logger.info(user + " was removed from DB");
				return;
			}
			
		} catch (Exception e) {
			String msg = "There was an attempt to remove " + user
					      + ", and something went wrong";
			processException(e, msg);
		}
		throw new EntityDoesNotExistException(user + " does not exist");
	}

	private void populateRoles(List<Role> query, User user) {
		Set<Role> roles = new HashSet<>();
		query.forEach(roles::add);
		user.setRoles(roles);
	}

	private void addUserRoles(User user) {
		Set<Role> roles = user.getRoles();
		if (roles == null || roles.size() == 0)
			return;
		String query = "INSERT INTO `users_demo`.`ROLE_USERS` (ROLE_ID, USER_ID) VALUES";
		StringBuilder builder = new StringBuilder(query);
		roles.forEach(role -> 
			builder.append(" ('")
			       .append(role.getShortName())
			       .append("', ")
			       .append(user.getId())
			       .append("), ")
		);
		builder.replace(builder.length() - 2, builder.length(), ";");
		jdbcTemplate.update(builder.toString());
	}

	private boolean updateUserRoles(User user, User found) {
		Set<Role> existingRoles = found.getRoles();
		Set<Role> newRoles = user.getRoles();
		if (!existingRoles.equals(newRoles)) {
			deleteRoles(existingRoles, found.getId());
			addUserRoles(user);
			return true;
		}
		return false;
	}

	private boolean updateUserDetails(User user) {
		return jdbcTemplate.update(UPDARE.getQuery(), 
				       user.getFirstName(), user.getLastName(), 
					   user.getMiddleName(), user.getEmail()) > 0;
	}

	private void deleteRoles(Set<Role> roles, Long id) {
		if (roles == null || roles.size() == 0) return;
		String query = "DELETE FROM `users_demo`.`ROLE_USERS` WHERE USER_ID = ?";
		jdbcTemplate.update(query, id);
	}

	private String getHashedPassword(User user) {
		String password = user.getPassword();
		if (password == null || password.length() == 0) {
			password = generatePassword();
			user.setPassword(password);
		}
		String hashedPassword = passwordEncoder.encode(password);
		return hashedPassword;
	}

	private String generatePassword() {
		return new BigInteger(130, random).toString(32);
	}

	private void processException(Exception e, String msg) {
		logger.debug(msg, e);
		throw new AccessDataBaseException(msg, e);
	}
}
