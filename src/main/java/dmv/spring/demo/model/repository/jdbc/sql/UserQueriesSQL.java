package dmv.spring.demo.model.repository.jdbc.sql;

/**
 * SQL native queries located on disk will be read up
 * into corresponding fields.
 * @author dmv
 */
public enum UserQueriesSQL {

	USER_CREATE("user_create"),
	USER_UPDARE("user_update"),
	USER_DELETE("user_delete"),
	USER_FIND_BY_EMAIL("user_find_by_email"),
	USER_FIND_BY_ID("user_find_by_id"),
	USER_GET_ID("user_get_id"),
	USER_ROLES_GET("user_roles_get"),
	USER_ROLES_DELETE("user_roles_delete"),
	USER_ROLES_ADD("user_roles_add"),
	USER_GET_CREDENTIALS("user_get_credentials");

	private String query;

	private UserQueriesSQL(String filename) {
		query= SQLResourceReader.READER.readSQLFile("user/" + filename);
	}

	public String getQuery() {
		return query;
	}
}
