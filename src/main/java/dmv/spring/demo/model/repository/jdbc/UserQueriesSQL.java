package dmv.spring.demo.model.repository.jdbc;

import static java.lang.String.join;
import static java.nio.file.Files.readAllLines;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;

/**
 * SQL native queries located on disk will be read up
 * into corresponding fields.
 * @author user
 */
public enum UserQueriesSQL {
	
	USER_CREATE("user_create"),
	USER_UPDARE("user_update"),
	USER_DELETE("user_delete"),
	USER_FIND_BY_EMAIL("user_find_by_email"),
	USER_GET_ID("user_get_id"),
	USER_ROLES_GET("user_roles_get"),
	USER_ROLES_DELETE("user_roles_delete"),
	USER_ROLES_ADD("user_roles_add");
	
	private String query;
	private Logger logger = getLogger(getClass());

	private UserQueriesSQL(String filename) {
		try {
			Path path = ParentDir.path.resolve(filename + ".sql");
			query = join(" ", readAllLines(path)).replaceAll("\\s+", " ");
		} catch (IOException e) {
			String msg = "Can't read SQL file " + filename;
			logger.error(msg, e);
			throw new RuntimeException(msg, e);
		}
	}

	public String getQuery() {
		return query;
	}
	
	private static class ParentDir {
		static final Path path = Paths.get("src/main/resources/sql/");
	}
}
