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
import static dmv.spring.demo.model.entity.UserFields.*;
import static dmv.spring.demo.model.entity.UserFields.FIRST_NAME;
import static dmv.spring.demo.model.entity.UserFields.LAST_NAME;
import static dmv.spring.demo.model.entity.UserFields.MIDDLE_NAME;
import static dmv.spring.demo.model.entity.UserFields.PASSWORD;
import static dmv.spring.demo.model.entity.UserFields.USER_TABLE;


/**
 * Queries to work with 'USER' table
 * @author user
 */
public enum UserQueries {

	CREATE("INSERT INTO " + USER_TABLE.getName() + " (" 
	        + EMAIL.getName() + ", " 
			+ FIRST_NAME.getName() + ", " 
			+ LAST_NAME.getName() + ", " 
			+ MIDDLE_NAME.getName() + ", " 
			+ PASSWORD.getName() 
			+ ") VALUES (?, ?, ?, ?, ?);"),
	UPDARE("UPDATE " + USER_TABLE.getName() + " SET "
			+ FIRST_NAME.getName() + " = ?, " 
			+ LAST_NAME.getName() + " = ?, " 
			+ MIDDLE_NAME.getName() + " = ? WHERE "
			+ EMAIL.getName() + " = ?;"),
	DELETE("DELETE FROM " + USER_TABLE.getName() +" WHERE "
			+ EMAIL.getName() + " = ?;"),
	FIND_BY_EMAIL("SELECT "
			+ ID.getName() + ", "
			+ EMAIL.getName() + ", " 
			+ FIRST_NAME.getName() + ", " 
			+ LAST_NAME.getName() + ", " 
			+ MIDDLE_NAME.getName() 
			+ " FROM " + USER_TABLE.getName() + " WHERE "
			+ EMAIL.getName() + " = ?;"),
	GET_ID("SELECT "
			+ ID.getName() 
			+ " FROM " + USER_TABLE.getName() + " WHERE "
			+ EMAIL.getName() + " = ?;"),
	FIND_USER_ROLES("SELECT "
			+ SHORT_NAME.getName() + ", "
			+ FULL_NAME.getName()
			+ " FROM " + ROLE_TABLE.getName()
			+ " WHERE " + SHORT_NAME.getName()
			+ " IN ("
			+ " SELECT " + ROLE_ID.getName()
			+ " FROM " + RU_TABLE.getName()
			+ " WHERE " + USER_ID.getName()
			+ " = ?);");
	
	private String query;

	private UserQueries(String query) {
			this.query = query;
		}

	public String getQuery() {
		return query;
	}
}
