package dmv.spring.demo.model.repository.jdbc.sql;

/**
 * An enum, that is holding SQL queries as strings.
 * These are for Role related queries.
 * SQL native queries located on disk will be read up
 * into corresponding fields.
 * @author dmv
 */
public enum RoleQueriesSQL implements NativeSQLQueries {

	ROLE_FIND_BY_SHORT_NAME("role_find_by_shortname"),
	ROLE_GET_USERS("role_get_users");

	private String query;

	private RoleQueriesSQL(String filename) {
		query= SQLResourceReader.READER.readSQLFile("role/" + filename);
	}

	@Override
	public String getQuery() {
		return query;
	}
}
