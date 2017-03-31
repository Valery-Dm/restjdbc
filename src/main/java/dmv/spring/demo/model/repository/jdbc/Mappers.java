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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import dmv.spring.demo.model.entity.EntityFields;
import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;

/**
 * @author user
 *
 */
public class Mappers {


	private static String get(ResultSet resultSet, EntityFields field) throws SQLException {
		return resultSet.getString(field.getName());
	}

	public static final RowMapper<Role> ROLE_MAPPER =
			(resultSet, rowNum) -> {
				Role role = new Role();
				role.setShortName(get(resultSet, SHORT_NAME));
				role.setFullName(get(resultSet, FULL_NAME));
				return role;
			};
	
	public static final RowMapper<User> USER_MAPPER =
	        (resultSet, rowNum) -> {
	        	User user = new User();
	        	user.setId(resultSet.getLong(1));
	        	user.setEmail(get(resultSet, EMAIL));
	        	user.setFirstName(get(resultSet, FIRST_NAME));
	        	user.setLastName(get(resultSet, LAST_NAME));
	        	user.setMiddleName(get(resultSet, MIDDLE_NAME));
	        	return user;
	        };
	        
}
