package dmv.spring.demo.model.repository.jdbc;

import static dmv.spring.demo.model.entity.RoleFields.FULL_NAME;
import static dmv.spring.demo.model.entity.RoleFields.SHORT_NAME;
import static dmv.spring.demo.model.entity.UserFields.EMAIL;
import static dmv.spring.demo.model.entity.UserFields.FIRST_NAME;
import static dmv.spring.demo.model.entity.UserFields.LAST_NAME;
import static dmv.spring.demo.model.entity.UserFields.MIDDLE_NAME;
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

	private final Logger logger = getLogger(getClass());

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

	@Override
	public Role findByShortName(String shortName) {
		Assert.notNull(shortName, "Can't find Role 'null'");
		shortName = shortName.toUpperCase();

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
						logger.debug(msg, e);
						throw new AccessDataBaseException(msg, e);
					}
					if (role == null)
						throw new EntityDoesNotExistException("Role " + shortName + " does not exist");
					cache.put(shortName, role);
				}
			}
		}
		return role.copy();
	}

	@Override
	public Set<User> getUsers(Role role) {
		Assert.notNull(role, "Can't find Users with role 'null'");
		Assert.notNull(role.getShortName(), "Can't find Users with role that has shortname 'null'");

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
			logger.debug(msg, e);
			throw new AccessDataBaseException(msg, e);
		}

		return users;
	}

	private Connection getConnection() throws SQLException {
//		return DataSourceUtils.getConnection(dataSource);
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
