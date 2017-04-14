package dmv.spring.demo.model.repository.jdbc;

import static dmv.spring.demo.model.repository.jdbc.Mappers.ROLE_MAPPER;
import static dmv.spring.demo.model.repository.jdbc.Mappers.USER_AUTH_MAPPER;
import static dmv.spring.demo.model.repository.jdbc.Mappers.USER_MAPPER;
import static dmv.spring.demo.model.repository.jdbc.sql.UserQueriesSQL.*;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.repository.UserRepository;

/**
 * Querying table USER via JDBC according to
 * {@link UserRepository} specification.
 * This class is not a Repository by itself.
 * Methods of this class are wrapped within
 * {@link UserRepositoryExceptionAdapter} right now.
 * @author dmv
 */
public class UserRepositoryJDBC {
    
    /* It is prohibit to alter this admin */
    private static final int adminId = 3;

	/* For password auto generation */
	private final SecureRandom random;
	/* For password hashing */
	private final PasswordEncoder passwordEncoder;
	/* Spring helper class for JDBC queries */
	private final JdbcTemplate jdbcTemplate;

    public UserRepositoryJDBC(JdbcTemplate jdbcTemplate,
    		                  PasswordEncoder passwordEncoder,
    		                  SecureRandom random) {
		this.jdbcTemplate = jdbcTemplate;
		this.passwordEncoder = passwordEncoder;
		this.random = random;
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
	 * @see UserRepositoryExceptionAdapter#getCredentials(String)
	 */
    public User getCredentials(String email) {
		User user = jdbcTemplate.queryForObject(USER_GET_CREDENTIALS.getQuery(),
				                                USER_AUTH_MAPPER, email);
		populateUserRoles(user);
		return user;
	}

	/**
	 * @see UserRepositoryExceptionAdapter#create(User)
	 */
	public User create(User user) {
		jdbcTemplate.update(USER_CREATE.getQuery(),
				user.getEmail(), user.getFirstName(),
				user.getLastName(), user.getMiddleName(),
				getHashedPassword(user));
		// get auto-generated ID
		user.setId(jdbcTemplate.queryForObject(USER_GET_ID.getQuery(),
				Long.class, user.getEmail()));
		persistUserRoles(user.getRoles(), user.getId());
		populateUserRoles(user);
		return user;
	}

    /**
	 * @see UserRepositoryExceptionAdapter#update(User)
	 */
	public User update(User user) {
	    if (user.getId() == adminId) return user;
		/*
		 * Because REST 'update' operation is exposed on rest/users/{userId}
		 * endpoint, therefore findById method is at high priority.
		 */
		User found = findById(user.getId());
		updateUserDetails(user, found);
		updateUserRoles(user, found);
		populateUserRoles(found);
		return found;
	}

    /**
	 * @see UserRepositoryExceptionAdapter#delete(User)
	 */
	public boolean delete(User user) {
        if (user.getId() == adminId) return false;
		/*
		 * Because REST 'delete' operation is exposed on rest/users/{userId}
		 * endpoint, therefore user.getId() is used for identification
		 */
		/* With Cascade deletion in ROLE_USERS table */
		return (jdbcTemplate.update(USER_DELETE.getQuery(), user.getId()) > 0);
	}




	/* Helper methods */

	/*
	 * Get existing user roles from DB and set them to User object
	 * that will be returned as found or created
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
		// update fields for the answer
		found.setFirstName(recieved.getFirstName());
		found.setLastName(recieved.getLastName());
		found.setMiddleName(recieved.getMiddleName());
		return jdbcTemplate.update(USER_UPDARE.getQuery(),
								found.getFirstName(), found.getLastName(),
								found.getMiddleName(), found.getId()) > 0;
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
		return getHashedPassword(password);
	}

	private String getHashedPassword(String password) {
		return passwordEncoder.encode(password);
	}

	private String generatePassword() {
		return new BigInteger(130, random).toString(32);
	}

}
