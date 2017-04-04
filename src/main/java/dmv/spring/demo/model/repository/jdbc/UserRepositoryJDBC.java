package dmv.spring.demo.model.repository.jdbc;

import static dmv.spring.demo.model.repository.jdbc.Mappers.ROLE_MAPPER;
import static dmv.spring.demo.model.repository.jdbc.Mappers.USER_MAPPER;
import static dmv.spring.demo.model.repository.jdbc.sql.RoleQueriesSQL.ROLE_FIND_BY_SHORT_NAME;
import static dmv.spring.demo.model.repository.jdbc.sql.UserQueriesSQL.*;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;
import dmv.spring.demo.model.repository.UserRepository;

/**
 * Querying table USER via JDBC according to
 * {@link UserRepository} specification
 * @author dmv
 */
public class UserRepositoryJDBC {

	/* For password auto generation */
	private final SecureRandom random = new SecureRandom();
	/* For password hashing */
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	/* Spring helper class for JDBC queries */
	private final JdbcTemplate jdbcTemplate;

    public UserRepositoryJDBC(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
	 * @see UserRepositoryExceptionAdapter#findById(Long)
	 */
	public User findById(Long id) {
		User user = jdbcTemplate.queryForObject(USER_FIND_BY_ID.getQuery(),
				                                USER_MAPPER, id);
		populateUserRoles(user);
		return user;
	}

    /**
	 * @see UserRepositoryExceptionAdapter#findByEmail(String)
	 */
	public User findByEmail(String email) {
		User user = jdbcTemplate.queryForObject(USER_FIND_BY_EMAIL.getQuery(),
				                                USER_MAPPER, email);
		populateUserRoles(user);
		return user;
	}

    /**
	 * @see UserRepositoryExceptionAdapter#create(User)
	 */
	public User create(User user) {
		getRoles(user);
		jdbcTemplate.update(USER_CREATE.getQuery(),
				user.getEmail(), user.getFirstName(),
				user.getLastName(), user.getMiddleName(),
				getHashedPassword(user));
		// get auto-generated ID
		user.setId(jdbcTemplate.queryForObject(USER_GET_ID.getQuery(),
				Long.class, user.getEmail()));
		persistUserRoles(user.getRoles(), user.getId());
		return user;
	}

    /**
	 * @see UserRepositoryExceptionAdapter#update(User)
	 */
	public User update(User user) {
		getRoles(user);
		/*
		 * Because REST 'update' operation is exposed on rest/users/{userId}
		 * endpoint, therefore findById method is at high priority.
		 */
		User found;
		if (user.getId() != null)
			found = findById(user.getId());
		else
			found = findByEmail(user.getEmail());

		updateUserDetails(user, found);
		updateUserRoles(user, found);
		return found;
	}

    /**
	 * @see UserRepositoryExceptionAdapter#delete(User)
	 */
	public boolean delete(User user) {
		/* With Cascade deletion in ROLE_USERS table */
		return (jdbcTemplate.update(USER_DELETE.getQuery(), user.getEmail()) > 0);
	}

	/* Helper methods */

	/*
	 * The Role object received by REST API may contain wrong
	 * or incomplete information.
	 */
	private void getRoles(User user) {
		Set<Role> receivedRoles = user.getRoles();
		if (receivedRoles == null || receivedRoles.size() == 0)
			return;
		Set<Role> foundRoles = new HashSet<>(receivedRoles.size());
		// Populate user with complete Role objects from DB
		receivedRoles.forEach(role -> {
			try {
				foundRoles.add(
						jdbcTemplate.queryForObject(
								ROLE_FIND_BY_SHORT_NAME.getQuery(),
								ROLE_MAPPER, role.getShortName()));
			} catch (EmptyResultDataAccessException e) {
				throw new EntityDoesNotExistException("Role " + role.getShortName() + " does not exist");
			}
		});
		user.setRoles(foundRoles);
	}

	/*
	 * Get existing user roles from DB and set them to User object
	 */
	private void populateUserRoles(User user) {
		user.setRoles(new HashSet<>(jdbcTemplate.query(
				                    USER_ROLES_GET.getQuery(),
                                    ROLE_MAPPER, user.getId())));
	}

	/*
	 * Save user roles to DB
	 */
	private void persistUserRoles(Set<Role> roles, Long id) {
		if (roles == null || roles.size() == 0)
			return;
		StringBuilder builder = new StringBuilder(USER_ROLES_ADD.getQuery());
		roles.forEach(role ->
			builder.append(" ('")
			       .append(role.getShortName())
			       .append("', ")
			       .append(id)
			       .append("),")
		);
		builder.replace(builder.length() - 1, builder.length(), ";");
		jdbcTemplate.update(builder.toString());
	}

	/*
	 * Update user roles in DB.
	 * There are two operations in a row:
	 * delete existing user roles and then add replacements
	 */
	private void updateUserRoles(User user, User found) {
		Set<Role> existingRoles = found.getRoles();
		Set<Role> newRoles = user.getRoles();
		if (!existingRoles.equals(newRoles)) {
			deleteUserRoles(existingRoles, found.getId());
			persistUserRoles(newRoles, found.getId());
		}
		found.setRoles(newRoles);
	}

	private boolean updateUserDetails(User recieved, User found) {
		found.setFirstName(recieved.getFirstName());
		found.setLastName(recieved.getLastName());
		found.setMiddleName(recieved.getMiddleName());
		return jdbcTemplate.update(USER_UPDARE.getQuery(),
								found.getFirstName(), found.getLastName(),
								found.getMiddleName(), found.getEmail()) > 0;
	}

	private void deleteUserRoles(Set<Role> roles, Long id) {
		if (roles == null || roles.size() == 0) return;
		jdbcTemplate.update(USER_ROLES_DELETE.getQuery(), id);
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

}
