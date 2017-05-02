package dmv.spring.demo.model.repository.jdbc.sql;

import dmv.spring.demo.model.repository.RoleRepository;

/**
 * Queries to work with 'ROLE' table through the
 * {@link RoleRepository} interface
 * @author dmv
 */
public enum RoleQueriesSQL implements QueryNativeSQL {

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
