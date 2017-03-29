/**
 * 
 */
package dmv.spring.demo.model.repository.jdbc;

import static dmv.spring.demo.model.entity.UserFields.EMAIL;
import static dmv.spring.demo.model.entity.UserFields.FIRST_NAME;
import static dmv.spring.demo.model.entity.UserFields.LAST_NAME;
import static dmv.spring.demo.model.entity.UserFields.MIDDLE_NAME;
import static dmv.spring.demo.model.repository.jdbc.UserQueries.CREATE;
import static dmv.spring.demo.model.repository.jdbc.UserQueries.DELETE;
import static dmv.spring.demo.model.repository.jdbc.UserQueries.FIND_BY_EMAIL;
import static dmv.spring.demo.model.repository.jdbc.UserQueries.UPDARE;
import static org.slf4j.LoggerFactory.getLogger;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.entity.UserFields;
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
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	/* Spring helper class for JDBC queries */
	private final JdbcTemplate jdbcTemplate;

	@Autowired
    public UserRepositoryJDBC(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

	@Override
	public User findByEmail(String email) {
		if (email == null || email.length() == 0)
			return null;
		try {
			User user = jdbcTemplate.queryForObject(
					FIND_BY_EMAIL.getQuery(), USER_MAPPER, email);
			return user;
		} catch (Exception e) {
			logger.debug("There was a lookup for user " + email +
					", and it was not successful", e);
			return null;
		}
	}

	@Override
	public void create(User user) {
		Assert.notNull(user, "Can't create user 'null'");
		if (findByEmail(user.getEmail()) != null)
			throw new EntityAlreadyExistsException("User " + user.getEmail() + " already exists");
		if (jdbcTemplate.update(CREATE.getQuery(), 
					user.getEmail(), user.getFirstName(), 
					user.getLastName(), user.getMiddleName(),
					getHashedPassword(user)) > 0)
			logger.info(user.getEmail() + " was added to DB");
	}

	@Override
	public boolean update(User user) {
		Assert.notNull(user, "Can't update user 'null'");
		// TODO check for modified Roles and update only those
		User found = assertIfExisted(user);
		if (jdbcTemplate.update(UPDARE.getQuery(), user.getFirstName(), 
				user.getLastName(), user.getMiddleName(),
				getHashedPassword(user), user.getEmail()) > 0) {
			logger.info(user.getEmail() + " was updated in DB");
			return true;
		}
		return false;
	}

	@Override
	public boolean delete(User user) {
		Assert.notNull(user, "Can't delete user 'null'");
		// TODO remove corresponding Roles
		User found = assertIfExisted(user);
		if (jdbcTemplate.update(DELETE.getQuery(), 
				    user.getEmail()) > 0) {
			logger.info(user.getEmail() + " was removed from DB");
			return true;
		}
		return false;
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

	private User assertIfExisted(User user) {
		User found = findByEmail(user.getEmail());
		if (found == null)
			throw new EntityDoesNotExistException("User " + user.getEmail() + " does not exist");
		return user;
	}
	
	private static String get(ResultSet resultSet, UserFields field) throws SQLException {
		return resultSet.getString(field.getName());
	}

	private static final RowMapper<User> USER_MAPPER =
	        (resultSet, rowNum) -> {
	        	User user = new User();
	        	user.setEmail(get(resultSet, EMAIL));
	        	user.setFirstName(get(resultSet, FIRST_NAME));
	        	user.setLastName(get(resultSet, LAST_NAME));
	        	user.setMiddleName(get(resultSet, MIDDLE_NAME));
	        	return user;
	        };
}
