package dmv.spring.demo.model.repository.jdbc;

import static dmv.spring.demo.model.entity.sqlfields.RoleFields.FULL_NAME;
import static dmv.spring.demo.model.entity.sqlfields.RoleFields.SHORT_NAME;
import static dmv.spring.demo.model.entity.sqlfields.UserFields.*;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.entity.sqlfields.EntityFields;

/**
 * This Mappers are for {@link ResultSet} fields to corresponding Entity fields
 * mapping
 *
 * @author dmv
 */
public class Mappers {

	private static String get(ResultSet resultSet, EntityFields field) throws SQLException {
		String result = resultSet.getString(field.getName());
		return result == null || result.length() == 0 ? null : result;
	}

	/**
	 * Map {@link Role} fields
	 */
	public static final RowMapper<Role> ROLE_MAPPER = (resultSet, rowNum) -> {
		Role role = new Role();
		role.setShortName(get(resultSet, SHORT_NAME));
		role.setFullName(get(resultSet, FULL_NAME));
		return role;
	};

	/**
	 * Map {@link User} fields without Password
	 */
	public static final RowMapper<User> USER_MAPPER = (resultSet, rowNum) -> {
		User user = new User();
		user.setId(resultSet.getLong(1));
		user.setEmail(get(resultSet, EMAIL));
		user.setFirstName(get(resultSet, FIRST_NAME));
		user.setLastName(get(resultSet, LAST_NAME));
		user.setMiddleName(get(resultSet, MIDDLE_NAME));
		return user;
	};

	/**
	 * Map {@link User} fields with Password
	 */
	public static final RowMapper<User> USER_AUTH_MAPPER = (resultSet, rowNum) -> {
		User user = new User();
		user.setId(resultSet.getLong(1));
		user.setEmail(get(resultSet, EMAIL));
		user.setFirstName(get(resultSet, FIRST_NAME));
		user.setLastName(get(resultSet, LAST_NAME));
		user.setMiddleName(get(resultSet, MIDDLE_NAME));
		user.setPassword(get(resultSet, PASSWORD));
		return user;
	};

}
