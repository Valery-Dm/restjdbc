/**
 * 
 */
package dmv.spring.demo.model.repository.jdbc;

import static dmv.spring.demo.model.entity.UserFields.*;


/**
 * Queries to work with 'USER' table
 * @author user
 */
public enum UserQueries {

	CREATE("INSERT INTO " + TABLE.getName() + " (" 
	        + EMAIL.getName() + ", " 
			+ FIRST_NAME.getName() + ", " 
			+ LAST_NAME.getName() + ", " 
			+ MIDDLE_NAME.getName() + ", " 
			+ PASSWORD.getName() 
			+ ") values (?, ?, ?, ?, ?)"),
	UPDARE("UPDATE " + TABLE.getName() + " SET "
			+ FIRST_NAME.getName() + " = ?, " 
			+ LAST_NAME.getName() + " = ?, " 
			+ MIDDLE_NAME.getName() + " = ?, " 
			+ PASSWORD.getName() + " = ? WHERE "
			+ EMAIL.getName() + " = ?"),
	DELETE("DELETE FROM " + TABLE.getName() +" WHERE "
			+ EMAIL.getName() + " = ?"),
	FIND_BY_EMAIL("SELECT "
			+ EMAIL.getName() + ", " 
			+ FIRST_NAME.getName() + ", " 
			+ LAST_NAME.getName() + ", " 
			+ MIDDLE_NAME.getName() +
			" FROM " + TABLE.getName() + " WHERE "
			+ EMAIL.getName() + " = ?");
	
	private String query;

	private UserQueries(String query) {
			this.query = query;
		}

	public String getQuery() {
		return query;
	}
}
