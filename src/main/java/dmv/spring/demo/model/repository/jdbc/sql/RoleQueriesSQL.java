package dmv.spring.demo.model.repository.jdbc.sql;

/**
 * Queries to work with 'ROLE' table
 * @author dmv
 */
public enum RoleQueriesSQL implements QueryNativeSQL {

	ROLE_FIND_BY_SHORT_NAME("role_find_by_shortname"),
	ROLE_USERS_GET("role_users_get");

	private String query;

	private RoleQueriesSQL(String filename) {
		query= SQLResourceReader.READER.readSQLFile("role/" + filename);
	}

	@Override
	public String getQuery() {
		return query;
	}
}
