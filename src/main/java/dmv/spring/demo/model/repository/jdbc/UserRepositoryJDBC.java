package dmv.spring.demo.model.repository.jdbc;

import static dmv.spring.demo.model.repository.jdbc.Mappers.ROLE_MAPPER;
import static dmv.spring.demo.model.repository.jdbc.Mappers.USER_MAPPER;
import static dmv.spring.demo.model.repository.jdbc.sql.UserQueriesSQL.*;
import static dmv.spring.demo.util.MessageComposer.compose;
import static org.slf4j.LoggerFactory.getLogger;

import java.sql.Connection;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.exceptions.EntityAlreadyExistsException;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;
import dmv.spring.demo.model.repository.RoleRepository;
import dmv.spring.demo.model.repository.UserRepository;
import dmv.spring.demo.security.CredentialsService;

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
public class UserRepositoryJDBC implements UserRepository {

	private final Logger logger = getLogger(getClass());

	private final DataSource dataSource;

	private final CredentialsService credentialsService;

	@Autowired
	public UserRepositoryJDBC(DataSource dataSource, CredentialsService credentialsService) {
		this.dataSource = dataSource;
		this.credentialsService = credentialsService;
	}

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
		// We are not altering input object, so make shallow copy first.
		// Null check is in the constructor.
		// If id exists it will be replaced by in-DB generated one
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
			connector.setString(5, credentialsService.getHashedPassword(createdUser));
			long preparedRoles = prepareRoles(connector, 6, createdUser);

			if (!getResults(createdUser, connector, preparedRoles))
				throwAlreadyExists("email", createdUser.getEmail());

			// clear given password as promised
			if (passwordWasProvided)
				createdUser.setPassword(null);

			logger.debug("User with email {} was created in DB", createdUser.getEmail());
			return createdUser;
		}
	}

	@Override
	public User update(User user) {
		Assert.notNull(user, "user can't be null");
		Assert.notNull(user.getId(), "user id can't be null");
		if (credentialsService.isProtected(user)) return user;
		// We are not altering input object, so make shallow copy first.
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

			if (!getResults(updatedUser, connector, preparedRoles))
				throwNotExist("User", "id", updatedUser.getId());

			// To avoid confusion replace possibly malformed email address
			// with actually existing one (email was not changed in this method,
			// and the user should receive original email back from DB)
			updatedUser.setEmail(connector.getString(3, 1));

			// we have no reason to pass around user's password
			updatedUser.setPassword(null);

			logger.debug("User with id {} was updated in DB", updatedUser.getId());
			return updatedUser;
		}
	}

	@Override
	public void delete(User user) {
		Assert.notNull(user, "user can't be null");
		deleteById(user.getId());
	}

	@Override
	public void deleteById(Long id) {
		Assert.notNull(id, "id can't be null");
		if (credentialsService.isProtected(id)) return;

		try (JdbcConnector connector =
				new JdbcConnector(dataSource, USER_DELETE)) {

			connector.setLong(1, id);
			// when no rows were affected means the user was not found
			if (connector.getUpdateCount() < 1)
				throwNotExist("User", "id", id);

			logger.debug("User with id {} was removed from DB", id);
		}
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

	// return true if result were successfully retrieved, false if userId returned as 0
	private boolean getResults(User updatedUser, JdbcConnector connector, long preparedRoles) {
		/*
		 * We are expecting next results:
		 * 0. updatedUserId long
		 * 1. foundRoles    long
		 * 3. Roles         table
		 */
		updatedUser.setId(connector.getLong(0, 1));
		long foundRoles = connector.getLong(1, 1);
		// this check takes precedence as more specific case
		if (foundRoles != preparedRoles)
			throwIllegalArg("Some of given roles were not found in db");
		// this one is more general
		if (updatedUser.getId() == 0)
			return false;
		if (foundRoles > 0)
			updatedUser.setRoles(connector.getObjects(2, ROLE_MAPPER));
		return true;
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
		// operation if some of given Roles is unknown.
		// I think we should react with BadRequest status in this case.
		// So, throwing an IllegalArgumentException will do the trick.
		if (!RoleRepository.isValidShortName(shortName))
			throwIllegalArg("Role", "shortName", shortName);
		return shortName;
	}

}
