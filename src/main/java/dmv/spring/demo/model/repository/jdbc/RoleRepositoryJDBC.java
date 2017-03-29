/**
 * 
 */
package dmv.spring.demo.model.repository.jdbc;

import static dmv.spring.demo.model.entity.UserFields.*;
import static dmv.spring.demo.model.repository.jdbc.RoleQueries.SELECT_USERS_WITH_ROLE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.util.Assert;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.repository.RoleRepository;

/**
 * @author user
 *
 */
public class RoleRepositoryJDBC implements RoleRepository {
	
	//TODO add logger
	
	private DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}


	/* (non-Javadoc)
	 * @see dmv.spring.demo.model.repository.RoleRepository#getUsers()
	 */
	@Override
	public Set<User> getUsers(Role role) {
		Assert.notNull(role, "Can't find Users with role 'null'");
		Set<User> users = new HashSet<>();
		try (Connection connection = dataSource.getConnection();
		     PreparedStatement statement = 
		    		 connection.prepareStatement(SELECT_USERS_WITH_ROLE.getQuery())) {
			statement.setString(1, role.getShortName());
			ResultSet resultSet = statement.executeQuery();
			collect(users, resultSet);
		} catch (SQLException e) {
			// TODO: handle exception
		} catch (Exception e) {
			// TODO: handle exception
		}
		return users;
	}

	private void collect(Set<User> users, ResultSet resultSet) throws SQLException {
		User user;
		while (resultSet.next()) {
			user = new User();
			user.setEmail(resultSet.getString(EMAIL.getName()));
			user.setFirstName(resultSet.getString(FIRST_NAME.getName()));
			user.setLastName(resultSet.getString(LAST_NAME.getName()));
			user.setMiddleName(resultSet.getString(MIDDLE_NAME.getName()));
			user.setPassword(resultSet.getString(PASSWORD.getName()));
			users.add(user);
		}
	}

}
