package dmv.spring.demo.model.repository.jdbc.sql;

/**
 * Common interface for native SQL queries
 * @author dmv
 */
public interface NativeSQLQueries {

	/**
	 * Return SQL query as a plain string
	 * @return SQL query
	 */
	String getQuery();
}
