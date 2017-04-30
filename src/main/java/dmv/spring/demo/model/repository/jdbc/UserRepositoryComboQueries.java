package dmv.spring.demo.model.repository.jdbc;

import static dmv.spring.demo.model.repository.jdbc.Mappers.ROLE_MAPPER;
import static dmv.spring.demo.model.repository.jdbc.Mappers.USER_MAPPER;
import static dmv.spring.demo.model.repository.jdbc.sql.UserQueriesSQL.*;
import static dmv.spring.demo.util.MessageComposer.compose;
import static org.slf4j.LoggerFactory.getLogger;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
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
import dmv.spring.demo.model.repository.RoleRepository;
import dmv.spring.demo.model.repository.UserRepository;

/**
 * This Repository implementation will use multi-statement SQL queries
 * that in turn may call Stored Procedures in order to create, update 
 * and get back several results within a single connection.
 * The actual {@link Connection} and DB interactions are managed by 
 * {@link JdbcConnector} helper class
 * <p>
 * All methods are executed within Transactions to rollback any changes
 * in case of abnormal termination.
 * @author dmv
 */
@Repository
@Transactional
public class UserRepositoryComboQueries implements UserRepository {
	
	/* It is prohibited to remove or alter the user with id 3.
	 * This is a 'least admin' protection for OpenShift container.
	 */
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
		// connector needs to be closed (same goes for all methods below)
		try (JdbcConnector connector = 
				new JdbcConnector(dataSource, USER_FIND_BY_ID_WITH_ROLES)) {

			connector.setLong(1, id);
			/*
			 * We are expecting next results:
			 * 0. User    fields without PASSWORD
			 * 1. Roles   table  
			 */
			User user = connector.getObject(0, USER_MAPPER);
			if (user == null)
				throwNotExist("User", "id", id);
			user.setRoles(connector.getObjects(1, ROLE_MAPPER));
	
			logger.debug("User with id {} was found in DB", id);
			return user;
		}
	}

	@Override
	public User findByEmail(String email) {
		Assert.notNull(email, "user email can't be null");
		try (JdbcConnector connector = 
				new JdbcConnector(dataSource, USER_FIND_BY_EMAIL_WITH_ROLES)) {

			connector.setRequiredString(1, email, "email");
			/*
			 * We are expecting next results:
			 * 0. User    fields without PASSWORD
			 * 1. Roles   table  
			 */
			User user = connector.getObject(0, USER_MAPPER);
			if (user == null)
				throwNotExist("User", "email", email);
			user.setRoles(connector.getObjects(1, ROLE_MAPPER));
	
			logger.debug("User with email {} was found in DB", email);
			return user;
		}
	}
	
	@Override
	public User create(User user) {
		// We are not altering input object, make shallow copy first.
		// Null check is in the constructor.
		User createdUser = new User(user);
		
		// The provided password won't be passed around, only
		// the generated one will be returned for user notification
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
			 * 2. Roles         table  
			 */
			createdUser.setId(connector.getLong(0, 1));
			long foundRoles = connector.getLong(1, 1);
			if (foundRoles != preparedRoles)
				throwIllegalArg("Some of given roles were not found in db");
			if (createdUser.getId() == 0) 
				throwAlreadyExists("email", createdUser.getEmail());
			if (foundRoles > 0) 
				createdUser.setRoles(connector.getObjects(2, ROLE_MAPPER));

			if (passwordWasProvided)
				createdUser.setPassword(null);
			
			logger.debug("User with email {} was created in DB", createdUser.getEmail());
			return createdUser;
		}
	}
	
	@Override
	public User update(User user) {
		check(user);
		if (isProtected(user)) return user;
		// We are not altering input object, make shallow copy first.
		User updatedUser = new User(user);
		
		try (JdbcConnector connector = 
				new JdbcConnector(dataSource, USER_UPDATE_WITH_ROLES)) {
			/*
			 * We are not changing user's EMAIL_ADRS and PASSWORD here.
			 * 
			 * This is 'by design' decision: these two fields have 
			 * somewhat essential meaning, and there must be two 
			 * separate methods specialized in just that.
			 * (Or even a separate class concerning the User Credentials
			 * would be a better choice)
			 */
			connector.setLong(1, updatedUser.getId());
			connector.setRequiredString(2, updatedUser.getFirstName(), "first name");
			connector.setRequiredString(3, updatedUser.getLastName(), "last name");
			connector.setString(4, updatedUser.getMiddleName());
			long preparedRoles = prepareRoles(connector, 5, updatedUser);
			
			/*
			 * We are expecting next results:
			 * 0. updatedUserId long
			 * 1. foundRoles    long
			 * 3. Roles         table  
			 */
			updatedUser.setId(connector.getLong(0, 1));
			long foundRoles = connector.getLong(1, 1);
			if (foundRoles != preparedRoles)
				throwIllegalArg("Some of given roles were not found in db");
			if (updatedUser.getId() == 0)
				throwNotExist("User", "id", updatedUser.getId());
			if (foundRoles > 0) 
				updatedUser.setRoles(connector.getObjects(2, ROLE_MAPPER));
			
			// we have no reason to pass around user's password
			updatedUser.setPassword(null);
			
			logger.debug("User with id {} was updated in DB", updatedUser.getId());
			return updatedUser;
		}
	}

	@Override
	public void delete(User user) {
		check(user);
		if (isProtected(user)) return;
		
		try (JdbcConnector connector = 
				new JdbcConnector(dataSource, USER_DELETE)) {
			
			connector.setLong(1, user.getId());
			// when no rows were affected means the user was not found
			if (connector.getUpdateCount() < 1)
				throwNotExist("User", "id", user.getId());
			
			logger.debug("User with id {} was removed from DB", user.getId());
		}
	}

	@Override
	public User getCredentials(String email) {
		Assert.notNull(email, "email can't be null");
		throw new UnsupportedOperationException();
	}
	
	
	/* Helper methods */
	
	private void throwIllegalArg(String msg) {
		logger.debug(msg);
		throw new IllegalArgumentException(msg);
	}
	
	private void throwIllegalArg(String entity, String paramName, Object param) {
		String msg = compose("{0} with {1} {2} does not exist", entity, paramName, param);
		logger.debug(msg);
		throw new IllegalArgumentException(msg);
	}

	private void throwNotExist(String entity, String paramName, Object param) {
		String msg = compose("{0} with {1} {2} does not exist", entity, paramName, param);
		logger.debug(msg);
		throw new EntityDoesNotExistException(msg);
	}

	private void throwAlreadyExists(String paramName, Object param) {
		String msg = compose("User with {0} {1} already exists", paramName, param);
		logger.debug(msg);
		throw new EntityAlreadyExistsException(msg);
	}

	private int prepareRoles(JdbcConnector connector, int pos, User user) {
		Set<Role> roles = user.getRoles();
		int rolesN = roles == null ? 0 : roles.size();
		connector.setLong(pos++, rolesN);
		if (rolesN > 0) {
			// building SQL SET, aka 'ADM,USR,DEV', to be passed
			// as a parameter into the Stored Procedure Call
			StringBuilder builder = new StringBuilder();
			roles.forEach(role -> builder.append(checkRoleName(role)).append(","));
			// remove last comma
			builder.replace(builder.length() - 1, builder.length(), "");
			connector.setString(pos, builder.toString());
		} else {
			connector.setString(pos, "");
		}
		return rolesN;
	}
	
	// early check for incorrect role's naming
	private String checkRoleName(Role role) {
		String shortName = role.getShortName();
		// It's not logical to return 404 on Create or Update User
		// operations if some of given Roles is unknown.
		// I think we should react with BadRequest status in this case.
		if (!RoleRepository.isValidShortName(shortName))
			throwIllegalArg("Role", "shortName", shortName);
		return shortName;
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
