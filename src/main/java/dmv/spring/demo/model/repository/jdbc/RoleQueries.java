/**
 * 
 */
package dmv.spring.demo.model.repository.jdbc;

import static dmv.spring.demo.model.entity.RoleFields.FULL_NAME;
import static dmv.spring.demo.model.entity.RoleFields.ROLE_TABLE;
import static dmv.spring.demo.model.entity.RoleFields.SHORT_NAME;
import static dmv.spring.demo.model.entity.RoleUsersFields.ROLE_ID;
import static dmv.spring.demo.model.entity.RoleUsersFields.RU_TABLE;
import static dmv.spring.demo.model.entity.RoleUsersFields.USER_ID;
import static dmv.spring.demo.model.entity.UserFields.EMAIL;
import static dmv.spring.demo.model.entity.UserFields.FIRST_NAME;
import static dmv.spring.demo.model.entity.UserFields.ID;
import static dmv.spring.demo.model.entity.UserFields.LAST_NAME;
import static dmv.spring.demo.model.entity.UserFields.MIDDLE_NAME;
import static dmv.spring.demo.model.entity.UserFields.USER_TABLE;

/**
 * Queries to work with 'ROLE' table
 * @author user
 */
public enum RoleQueries {
	
	FIND_BY_SHORT_NAME("SELECT " 
			+ SHORT_NAME.getName() + ", " 
			+ FULL_NAME.getName() 
			+" FROM " + ROLE_TABLE.getName()
			+" WHERE " + SHORT_NAME.getName() 
			+ " = ?;"),

	SELECT_USERS_WITH_ROLE("SELECT " 
			+ EMAIL.getName() + ", " 
			+ FIRST_NAME.getName() + ", " 
			+ LAST_NAME.getName() + ", " 
			+ MIDDLE_NAME.getName() 
			+ " FROM " + USER_TABLE.getName()
			+ " WHERE " + ID.getName() 
			+ " IN ("
			+ " SELECT ru." + USER_ID.getName()
			+ " FROM " + RU_TABLE.getName() + " as ru "
			+ " WHERE ru." + ROLE_ID.getName() 
			+ " = ?);");

	private String query;

	private RoleQueries(String query) {
			this.query = query;
		}

	public String getQuery() {
		return query;
	}
}
