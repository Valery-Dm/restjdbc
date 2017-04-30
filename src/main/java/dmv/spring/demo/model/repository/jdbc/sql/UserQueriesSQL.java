package dmv.spring.demo.model.repository.jdbc.sql;

/**
 * An {@link Enum} holding SQL queries as strings.
 * SQL native queries located on disk will be read up
 * into corresponding fields.
 * @author dmv
 */
public enum UserQueriesSQL implements QueryNativeSQL {

	USER_DELETE("user_delete"),
	USER_GET_CREDENTIALS("user_get_credentials"),
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
