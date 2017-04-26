package dmv.spring.demo.model.repository.jdbc;

import static dmv.spring.demo.model.repository.jdbc.Mappers.ROLE_MAPPER;
import static dmv.spring.demo.model.repository.jdbc.Mappers.USER_MAPPER;
import static dmv.spring.demo.model.repository.jdbc.sql.UserQueriesSQL.USER_DELETE;
import static dmv.spring.demo.model.repository.jdbc.sql.UserQueriesSQL.USER_FIND_BY_EMAIL;
import static dmv.spring.demo.model.repository.jdbc.sql.UserQueriesSQL.USER_INSERT_WITH_ROLES;
import static dmv.spring.demo.util.MessageComposer.compose;
import static org.slf4j.LoggerFactory.getLogger;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.exceptions.EntityAlreadyExistsException;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;
import dmv.spring.demo.model.repository.JdbcConnector;
import dmv.spring.demo.model.repository.RoleRepository;
import dmv.spring.demo.model.repository.UserRepository;

/**
 * @author dmv
 */
@Repository
@Transactional
public class UserRepositoryComboQueries implements UserRepository {
	
	/* It is prohibit to alter this admin */
	private static final int adminId = 3;
	
	private final Logger logger = getLogger(getClass());
	
	@Autowired
	private DataSource dataSource;
	

	/* For password auto generation */
	@Autowired
	private SecureRandom random;
	
	/* For password hashing */
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public User findById(Long id) {
		Assert.notNull(id, "user id can't be null");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User findByEmail(String email) {
		Assert.notNull(email, "user email can't be null");

		try (JdbcConnector connector = 
				new JdbcConnector(dataSource, USER_FIND_BY_EMAIL)) {

			connector.setRequiredString(1, email, "email");
			User user = connector.getObject(0, USER_MAPPER);
			if (user == null)
				throwNotExist("User", "email", email);
	
			return user;
		}
	}
	
	@Override
	public User create(User user) {
		// Null check is in the constructor
		User createdUser = new User(user);
		boolean passwordWasProvided = user.getPassword() != null &&
				                      user.getPassword().length() > 0;
		
		try (JdbcConnector connector = 
				new JdbcConnector(dataSource, USER_INSERT_WITH_ROLES)) {

			connector.setRequiredString(1, createdUser.getEmail(), "email");
			connector.setRequiredString(2, createdUser.getFirstName(), "first name");
			connector.setRequiredString(3, createdUser.getLastName(), "last name");
			connector.setString(4, createdUser.getMiddleName());
			connector.setString(5, getHashedPassword(createdUser));
			long preparedRoles = prepareRoles(connector, 6, createdUser);

			/*
			 * We are expecting next results:
			 * 0. createdUserId long
			 * 1. foundRoles    long
			 * 2. addedRoles    long
			 * 3. Roles         table  
			 */
			createdUser.setId(connector.getLong(0, 1));
			if (createdUser.getId() == 0) 
				throwAlreadyExists("email", createdUser.getEmail());
			long foundRoles = connector.getLong(1, 1);
			if (foundRoles != preparedRoles)
				throwNotExist("Some of given roles were not found in db");
			if (foundRoles > 0) {
				Set<Role> roles = connector.getObjects(3, ROLE_MAPPER);
				createdUser.setRoles(roles);
			}

			// The provided password won't be passed around
			if (passwordWasProvided)
				user.setPassword(null);
			
			logger.debug("User {} was created in DB", createdUser.getEmail());
			return createdUser;
		}
	}
	
	@Override
	public User update(User user) {
		check(user);
		if (isProtected(user)) return user;
		// TODO Auto-generated method stub
		User updatedUser = new User(user);
		
		logger.debug("User {} was updated in DB", updatedUser.getEmail());
		return null;
	}

	@Override
	public void delete(User user) {
		check(user);
		if (isProtected(user)) return;
		
		try (JdbcConnector connector = 
				new JdbcConnector(dataSource, USER_DELETE)) {
			
			connector.setLong(1, user.getId());
			int updateCount = connector.getUpdateCount();
			if (updateCount < 1)
				throwNotExist("User", "id", user.getId());
			
		}
		logger.debug("User {} was removed from DB", user.getEmail());
	}

	@Override
	public User getCredentials(String email) {
		Assert.notNull(email, "email can't be null");
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/* Helper methods */
	
	// early check for incorrect role's naming
	private String checkRoleName(Role role) {
		String shortName = role.getShortName();
		if (!RoleRepository.isValidShortName(shortName))
			throwNotExist("Role", "shortName", shortName);
		return shortName;
	}
	
	private void throwNotExist(String msg) {
		throw new EntityDoesNotExistException(msg);
	}

	private void throwNotExist(String entity, String paramName, Object param) {
		throw new EntityDoesNotExistException(
				compose("{0} with {1} {2} does not exist", entity, paramName, param));
	}

	private void throwAlreadyExists(String paramName, Object param) {
		throw new EntityAlreadyExistsException(
				compose("User with {0} {1} already exists", paramName, param));
	}

	private int prepareRoles(JdbcConnector connector, int pos, User user) {
		Set<Role> roles = user.getRoles();
		int rolesN = roles == null ? 0 : roles.size();
		connector.setLong(pos++, rolesN);
		if (rolesN > 0) {
			// building SQL SET, aka 'ADM,USR,DEV', to be passed
			// as a parameter into Stored Procedure call
			StringBuilder builder = new StringBuilder();
			roles.forEach(role -> builder.append(checkRoleName(role)).append(","));
			builder.replace(builder.length() - 1, builder.length(), "");
			connector.setString(pos, builder.toString());
		} else {
			connector.setString(pos, "");
		}
		return rolesN;
	}

	private void check(User user) {
		Assert.notNull(user, "user can't be null");
		Assert.notNull(user.getId(), "user id can't be null");
	}
	
	// OpenShift 'least admin' protection
	private boolean isProtected(User user) {
		return user.getId() == adminId;
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
