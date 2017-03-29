/**
 * 
 */
package dmv.spring.demo.model.repository;

import static dmv.spring.demo.model.entity.UserFields.EMAIL;
import static dmv.spring.demo.model.entity.UserFields.FIRST_NAME;
import static dmv.spring.demo.model.entity.UserFields.LAST_NAME;
import static dmv.spring.demo.model.entity.UserFields.MIDDLE_NAME;
import static dmv.spring.demo.model.repository.jdbc.UserQueries.CREATE;
import static dmv.spring.demo.model.repository.jdbc.UserQueries.DELETE;
import static dmv.spring.demo.model.repository.jdbc.UserQueries.FIND_BY_EMAIL;
import static dmv.spring.demo.model.repository.jdbc.UserQueries.UPDARE;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import dmv.spring.demo.model.entity.User;

/**
 * @author user
 *
 */
@Component
public class UserRepositoryImpl implements UserRepository {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/* For password auto generation */
	private final SecureRandom random = new SecureRandom();
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	private final JdbcTemplate jdbcTemplate;

	@Autowired
    public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

	@Override
	@Transactional
	public User findByEmail(String email) {
		if (email == null || email.length() == 0)
			return null;
//		String fname = jdbcTemplate.queryForObject("select FIRST_NAME from USER where EMAIL_ADRS=?", String.class, email);
//		System.out.println("++++++++++++" + fname);
		User user = jdbcTemplate.queryForObject(FIND_BY_EMAIL.getQuery(), USER_MAPPER, email);
		return user;
	}

	@Override
	public void create(User user) {
		Assert.notNull(user, "Can't create user 'null'");
		// TODO check if user does not exist
		if (jdbcTemplate.update(CREATE.getQuery(), 
					user.getEmail(), user.getFirstName(), 
					user.getLastName(), user.getMiddleName(),
					getPassword(user)) > 0)
			logger.info(user.getEmail() + " was added to DB");
	}

	private String getPassword(User user) {
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

	@Override
	public boolean update(User user) {
		Assert.notNull(user, "Can't update user 'null'");
		// TODO explicitly check if user exists
		if (jdbcTemplate.update(UPDARE.getQuery(), user.getFirstName(), 
				user.getLastName(), user.getMiddleName(),
				getPassword(user), user.getEmail()) > 0) {
			logger.info(user.getEmail() + " was updated in DB");
			return true;
		}
		return false;
	}

	@Override
	public boolean delete(User user) {
		Assert.notNull(user, "Can't delete user 'null'");
		if (jdbcTemplate.update(DELETE.getQuery(), 
				    user.getEmail()) > 0) {
			logger.info(user.getEmail() + " was removed from DB");
			return true;
		}
		return false;
	}
	
	private static final RowMapper<User> USER_MAPPER =
	        (resultSet, rowNum) -> {
	        	User user = null;
	        	while (resultSet.next()) {
	        		user = new User();
	        		user.setEmail(resultSet.getString(EMAIL.getName()));
	        		user.setFirstName(resultSet.getString(FIRST_NAME.getName()));
	        		user.setLastName(resultSet.getString(LAST_NAME.getName()));
	        		user.setMiddleName(resultSet.getString(MIDDLE_NAME.getName()));
	        		//user.setPassword(resultSet.getString(PASSWORD.getName()));
	        	}
	        	return user;
	        };
}
