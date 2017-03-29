/**
 * 
 */
package dmv.spring.demo.model.repository.jdbc;

import static dmv.spring.demo.model.entity.RoleFields.FULL_NAME;
import static dmv.spring.demo.model.entity.RoleFields.SHORT_NAME;
import static dmv.spring.demo.model.entity.UserFields.EMAIL;
import static dmv.spring.demo.model.entity.UserFields.FIRST_NAME;
import static dmv.spring.demo.model.entity.UserFields.LAST_NAME;
import static dmv.spring.demo.model.entity.UserFields.MIDDLE_NAME;
import static dmv.spring.demo.model.repository.jdbc.RoleQueries.FIND_BY_SHORT_NAME;
import static dmv.spring.demo.model.repository.jdbc.RoleQueries.SELECT_USERS_WITH_ROLE;
import static org.slf4j.LoggerFactory.getLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.repository.RoleRepository;

/**
 * Querying table ROLE via JDBC according to
 * {@link RoleRepository} specification
 * @author user
 */
@Repository
@Transactional(readOnly=true)
public class RoleRepositoryJDBC implements RoleRepository {
	
	private final Logger logger = getLogger(getClass());
	
	/* Standard javax.sql source here */
	private final DataSource dataSource;

	@Autowired
	public RoleRepositoryJDBC(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Role findByShortName(String shortName) {
		if (shortName == null || shortName.length() == 0)
			return null;
		Role role = null;
		/* Pooled connection must not be closed */
		Connection connection = getConnection();
		try (PreparedStatement statement = 
			    		 connection.prepareStatement(FIND_BY_SHORT_NAME.getQuery())) {
			statement.setString(1, shortName);
			ResultSet resultSet = statement.executeQuery();
			role = mapRole(resultSet);
		} catch (Exception e) {
			logger.debug("There was a call findByShortName " + shortName +
					", and it was not successful", e);
		} 
		return role;
	}

	@Override
	public Set<User> getUsers(Role role) {
		Assert.notNull(role, "Can't find Users with role 'null'");
		Set<User> users = new HashSet<>();
		Connection connection = getConnection();
	    try (PreparedStatement statement = 
		    		 connection.prepareStatement(SELECT_USERS_WITH_ROLE.getQuery())) {
			statement.setString(1, role.getShortName());
			ResultSet resultSet = statement.executeQuery();
			collect(users, resultSet);
		} catch (Exception e) {
			logger.debug("There was a call for getUsers with role " + role +
					", and it was not successful", e);
		} 
		return users;
	}

	private Connection getConnection() {
		return DataSourceUtils.getConnection(dataSource);
	}

	private Role mapRole(ResultSet resultSet) throws SQLException {
		Role role = new Role();
		if (resultSet.next()) {
			role.setShortName(resultSet.getString(SHORT_NAME.getName()));
			role.setFullName(resultSet.getString(FULL_NAME.getName()));
		}
		return role;
	}

	private void collect(Set<User> users, ResultSet resultSet) throws SQLException {
		User user;
		while (resultSet.next()) {
			user = new User();
			user.setEmail(resultSet.getString(EMAIL.getName()));
			user.setFirstName(resultSet.getString(FIRST_NAME.getName()));
			user.setLastName(resultSet.getString(LAST_NAME.getName()));
			user.setMiddleName(resultSet.getString(MIDDLE_NAME.getName()));
			users.add(user);
		}
	}

}
