package dmv.spring.demo.model.repository.jdbc;

/**
 * Queries to work with 'ROLE' table
 * @author dmv
 */
public enum RoleQueriesSQL {

	ROLE_FIND_BY_SHORT_NAME("role_find_by_shortname"),
	ROLE_USERS_GET("role_users_get");

	private String query;

	private RoleQueriesSQL(String filename) {
		query= SQLResourceReader.readSQLFile(filename);
	}

	public String getQuery() {
		return query;
	}
}
