package dmv.spring.demo.model.repository.jdbc.sql;

/**
 * An enum, that is holding SQL queries as strings.
 * These are for User Credentials.
 * SQL native queries located on disk will be read up
 * into corresponding fields.
 * @author dmv
 */
public enum CredentialsQueriesSQL implements NativeSQLQueries {

	USER_GET_CREDENTIALS("user_get_credentials");

	private String query;

	private CredentialsQueriesSQL(String filename) {
		query = SQLResourceReader.READER.readSQLFile("user/" + filename);
	}

	@Override
	public String getQuery() {
		return query;
	}

}
