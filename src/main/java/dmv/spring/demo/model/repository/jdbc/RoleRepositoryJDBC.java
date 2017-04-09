package dmv.spring.demo.model.repository.jdbc;

import static dmv.spring.demo.model.entity.sqlfields.RoleFields.FULL_NAME;
import static dmv.spring.demo.model.entity.sqlfields.RoleFields.SHORT_NAME;
import static dmv.spring.demo.model.entity.sqlfields.UserFields.EMAIL;
import static dmv.spring.demo.model.entity.sqlfields.UserFields.FIRST_NAME;
import static dmv.spring.demo.model.entity.sqlfields.UserFields.LAST_NAME;
import static dmv.spring.demo.model.entity.sqlfields.UserFields.MIDDLE_NAME;
import static dmv.spring.demo.model.repository.jdbc.sql.RoleQueriesSQL.ROLE_FIND_BY_SHORT_NAME;
import static dmv.spring.demo.model.repository.jdbc.sql.RoleQueriesSQL.ROLE_USERS_GET;
import static org.slf4j.LoggerFactory.getLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.exceptions.AccessDataBaseException;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;
import dmv.spring.demo.model.repository.RoleRepository;

/**
 * Querying table ROLE via JDBC according to
 * {@link RoleRepository} specification
 * @author dmv
 */
@Repository
@Transactional(readOnly=true)
public class RoleRepositoryJDBC implements RoleRepository {

	private static final String SHORTNAME_NULL = "Can't find Users with role that has shortname 'null'";

	private static final String USERS_WITH_ROLE_NULL = "Can't find Users with role 'null'";

	private static final String ROLE_NULL = "Can't find Role 'null'";

	private static final Logger logger = getLogger(RoleRepositoryJDBC.class);

	/*
	 * There are just three Roles available.
	 * Simple caching solution would suffice
	 */
	private Map<String, Role> cache = new HashMap<>();

	/* Standard javax.sql source here */
	private final DataSource dataSource;

	@Autowired
	public RoleRepositoryJDBC(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/*
	 * (non-Javadoc)
	 * @see dmv.spring.demo.model.repository.RoleRepository#findByShortName(java.lang.String)
	 */
	@Override
	public Role findByShortName(String shortName) {
		Assert.notNull(shortName, ROLE_NULL);
		shortName = shortName.toUpperCase();
		boolean debug = logger.isDebugEnabled();

		Role role = cache.get(shortName);
		if (role == null) {
			// Synchronization is not necessary for real-world scenarios.
			// And there is just a small improvement in synthetic tests.
			// But this is a demo application, so let it be.
			synchronized (cache) {
				role = cache.get(shortName);
				if (role == null) {
					try (Connection connection = getConnection();
							PreparedStatement statement =
									connection.prepareStatement(ROLE_FIND_BY_SHORT_NAME.getQuery())) {

						statement.setString(1, shortName);
						role = mapRole(statement.executeQuery());

					} catch (Exception e) {
						String msg = "There was a call findByShortName(" + shortName +
								"), and it was not successful";
						logger.error(msg, e);
						throw new AccessDataBaseException(msg, e);
					}
					if (role == null)
						throw new EntityDoesNotExistException("Role " + shortName + " does not exist");
					cache.put(shortName, role);
					if (debug) logger.debug("Role " + shortName + " has been added to cache");
				}
			}
		}
		if (debug) logger.debug("Role " + shortName + " has been supplied from findByShortName() method");
		return role.copy();
	}

	/*
	 * (non-Javadoc)
	 * @see dmv.spring.demo.model.repository.RoleRepository#getUsers(dmv.spring.demo.model.entity.Role)
	 */
	@Override
	public Set<User> getUsers(Role role) {
		Assert.notNull(role, USERS_WITH_ROLE_NULL);
		Assert.notNull(role.getShortName(), SHORTNAME_NULL);
		boolean debug = logger.isDebugEnabled();

		Set<User> users = new HashSet<>();
		try (Connection connection = getConnection();
			 PreparedStatement statement =
		    		 connection.prepareStatement(ROLE_USERS_GET.getQuery())) {

			statement.setString(1, role.getShortName());
			ResultSet resultSet = statement.executeQuery();
			collectUsers(users, resultSet);

		} catch (Exception e) {
			String msg = "There was a call for getUsers with role " + role +
					", and it was not successful";
			logger.error(msg, e);
			throw new AccessDataBaseException(msg, e);
		}
		if (debug) logger.debug("getUsers(role) successfully returned Set of users");
		return users;
	}

	private Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	private Role mapRole(ResultSet resultSet) throws SQLException {
		Role role = null;
		if (resultSet.next()) {
			role = new Role();
			role.setShortName(resultSet.getString(SHORT_NAME.getName()));
			role.setFullName(resultSet.getString(FULL_NAME.getName()));
		}
		return role;
	}

	private void collectUsers(Set<User> users, ResultSet resultSet) throws SQLException {
		User user;
		while (resultSet.next()) {
			user = new User();
			user.setId(resultSet.getLong(1));
			user.setEmail(resultSet.getString(EMAIL.getName()));
			user.setFirstName(resultSet.getString(FIRST_NAME.getName()));
			user.setLastName(resultSet.getString(LAST_NAME.getName()));
			user.setMiddleName(resultSet.getString(MIDDLE_NAME.getName()));
			users.add(user);
		}
	}

}
