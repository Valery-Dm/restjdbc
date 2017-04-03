package dmv.spring.demo.model.repository.jdbc.sql;

import static java.lang.String.join;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;

/**
 * Helper class that reads SQL resources
 * @author dmv
 */
public class SQLResourceReader {
	
	public static final SQLResourceReader READER = new SQLResourceReader();
	
	private static final Logger logger = getLogger(SQLResourceReader.class);

	public String readSQLFile(String filename) {
		String query = null;
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(
						getClass().getResourceAsStream("/sql/" + filename + ".sql")))) {
			List<String> lines = reader.lines().collect(Collectors.toList());
			query = join(" ", lines).replaceAll("\\s+", " ");
		} catch (IOException e) {
			String msg = "Can't read SQL file " + filename;
			logger.error(msg, e);
			throw new RuntimeException(msg, e);
		}
		return query;
	}
}
