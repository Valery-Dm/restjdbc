/**
 * 
 */
package dmv.spring.demo.model.repository.jdbc;

/**
 * @author user
 *
 */
public enum RoleQueries {

	SELECT_USERS_WITH_ROLE("SELECT * FROM ROLE_USERS WHERE ROLE_ID=?");

	private String query;

	private RoleQueries(String query) {
			this.query = query;
		}

	public String getQuery() {
		return query;
	}
}
