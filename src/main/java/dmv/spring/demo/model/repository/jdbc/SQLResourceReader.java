package dmv.spring.demo.model.repository.jdbc;

import static java.lang.String.join;
import static java.nio.file.Files.readAllLines;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;

/**
 * Helper class that reads SQL resources
 * @author dmv
 */
public class SQLResourceReader {

	private static final Path dir = Paths.get("src/main/resources/sql/");

	private static final Logger logger = getLogger(SQLResourceReader.class);

	public static String readSQLFile(String filename) {
		String query = null;
		try {
			Path path = dir.resolve(filename + ".sql");
			query = join(" ", readAllLines(path)).replaceAll("\\s+", " ");
		} catch (IOException e) {
			String msg = "Can't read SQL file " + filename;
			logger.error(msg, e);
			throw new RuntimeException(msg, e);
		}
		return query;
	}
}
