package dmv.spring.demo.model.repository.jdbc.sql;

/**
 * An enum, that is holding SQL queries as strings.
 * These are for User related queries.
 * SQL native queries located on disk will be read up
 * into corresponding fields.
 * @author dmv
 */
public enum UserQueriesSQL implements NativeSQLQueries {

	USER_DELETE("user_delete"),
	USER_FIND_BY_ID_WITH_ROLES("user_find_by_id_with_roles"),
	USER_FIND_BY_EMAIL_WITH_ROLES("user_find_by_email_with_roles"),
	USER_INSERT_WITH_ROLES("user_insert_with_roles"),
	USER_UPDATE_WITH_ROLES("user_update_with_roles");

	private String query;

	private UserQueriesSQL(String filename) {
		query = SQLResourceReader.READER.readSQLFile("user/" + filename);
	}

	@Override
	public String getQuery() {
		return query;
	}
}
