package dmv.spring.demo.model.repository.jdbc;

import static dmv.spring.demo.util.MessageComposer.compose;
import static java.sql.ResultSet.CONCUR_READ_ONLY;
import static java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE;
import static java.sql.Statement.KEEP_CURRENT_RESULT;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.exceptions.AccessDataBaseException;
import dmv.spring.demo.model.repository.jdbc.sql.QueryNativeSQL;

/**
 * Custom made manager for {@link Connection} and {@link PreparedStatement},
 * to be used in {@link Repository} classes. Reducing boilerplate code.
 * The Connection is a pooled Connection from {@link DataSourceUtils} class
 * <p> {@link SQLException} will be converted to {@link AccessDataBaseException}
 * at this level: opening and closing connections, setting parameters.
 * <p> It's not thread safe, and is expected to be created and then closed within a single method.
 * <p> It implements {@link AutoCloseable} interface and could be used with try-with-resources statement
 * @author dmv
 */
public class JdbcConnector implements AutoCloseable {

	/* predefined message for AccessDataBaseException exceptions */
	private static final String exMsg = "There was an attempt to {0} and it failed while {1}";

	/* this is not a Spring Component so we are using static logger */
	private static final Logger logger = LoggerFactory.getLogger(JdbcConnector.class);

	private DataSource dataSource;

	private Connection connection;

	private PreparedStatement preparedStatement;

	private AccessDataBaseException exception;

	/* what kind of operation we are in */
	private final String doJob;

	private List<ResultSet> results;

	/**
	 * Creates {@link Connection} and {@link PreparedStatement} objects from
	 * given dataSource, using provided query.
	 * <p> Don't forget to call {@link #close()} method in the end.
	 * @param dataSource the {@link DataSource} to be used
	 * @param query the {@link QueryNativeSQL} query
	 * @throws AccessDataBaseException if open connection and prepare statement 
	 *                                 operations were not successful
	 */
	public JdbcConnector(DataSource dataSource, QueryNativeSQL query) {
		doJob = query.toString();
		this.dataSource = dataSource;
		try {
			// will throw SQL Exceptions - exactly as we need here
			connection = DataSourceUtils.doGetConnection(dataSource);
			// The statement is scrollable up and down, and it won't keep db connection open
			preparedStatement = connection.prepareStatement(query.getQuery(),
									                   TYPE_SCROLL_INSENSITIVE,
									                   CONCUR_READ_ONLY);
		} catch (Exception e) {
			closeAfter(e, "openning connection");
		}
	}

	/**
	 * Close underlying statement and release connection
	 * @throws AccessDataBaseException if closing operations were not successful
	 */
	@Override
	public void close() {
		exception = new AccessDataBaseException();
		if (!isSuccessfullyClosed()) {
			String message = compose(exMsg, doJob, "closing connection");
			exception.setMessage(message);
			logger.error(message);
			throw exception;
		}
		logger.debug("connector {} was closed", doJob);
	}

	/**
	 * Returns the first update count after execution.
	 * <p> This method won't get or cache any results
	 * (i.e. Result Sets are not expected). Use {@link #getResults()}
	 * method instead if you need them.
	 * @return the first update count
	 * @throws AccessDataBaseException translates SQLExceptions
	 * @see PreparedStatement#getUpdateCount()
	 */
	public int getUpdateCount() {
		int count = -1;
		try {

			preparedStatement.execute();
			count = preparedStatement.getUpdateCount();

		} catch (Exception e) {
			closeAfter(e, "getting update count");
		}
		return count;
	}

	/**
	 * Execute statement and return a list with {@link ResultSet} objects
	 * @return A list with available results (with {@link RandomAccess RAM})
	 *         or empty list if there were Update Counts only
	 * @throws AccessDataBaseException translates SQLExceptions
	 */
	public List<ResultSet> getResults() {
		if (results != null) return results;
		results = new ArrayList<>();
		try {

			boolean hasNextRS = preparedStatement.execute();
			while (hasNextRS || preparedStatement.getUpdateCount() != -1) {
				// skipping updateCounts
				if (hasNextRS && preparedStatement.getResultSet().next())
					results.add(preparedStatement.getResultSet());
				// prevent Result Sets from being closed
				hasNextRS = preparedStatement.getMoreResults(KEEP_CURRENT_RESULT);
			}

		} catch (Exception e) {
			closeAfter(e, "getting results");
		}
		return results;
	}

	/**
	 * Gets long from resultIndex {@link ResultSet} at
	 * columnIndex position
	 * @param resultIndex The results list index (java - 0 based)
	 * @param columnIndex The column number (sql - 1 based)
	 * @return long result
	 * @throws AccessDataBaseException translates SQLExceptions,
	 *        and 'out of bound' errors  will also fall into this category
	 */
	public long getLong(int resultIndex, int columnIndex) {
		getResults();
		long result = 0;
		try {

			if (results.size() > resultIndex) {
				ResultSet resultSet = results.get(resultIndex);
				// we are expecting just a single row here
				resultSet.first();
				result = resultSet.getLong(columnIndex);
			}

		} catch (Exception e) {
			closeAfter(e, "getting long");
		}
		return result;
	}

	/**
	 * Retrieves an object from {@link ResultSet} found in a list of results
	 * at given index, using provided {@link RowMapper mapper}
	 * @param <T> Entity type is expected (like {@link Role} or {@link User})
	 * @param resultIndex list index (0 if expecting single result)
	 * @param mapper the {@link RowMapper mapper} to be used for objects creation
	 * @return a constructed object or null if none found
	 * @throws AccessDataBaseException translates SQLExceptions,
	 *         and 'out of bound' errors  will also fall into this category
	 */
	public <T> T getObject(int resultIndex, RowMapper<T> mapper) {
		getResults();
		try {

			if (results.size() > resultIndex)
				return mapper.mapRow(results.get(resultIndex), resultIndex);

		} catch (Exception e) {
			closeAfter(e, "getting object");
		}
		return null;
	}

	/**
	 * Retrieves a set of objects from {@link ResultSet results list}
	 * at given index, using provided {@link RowMapper mapper}
	 * @param <T> Entity type is expected (like {@link Role} or {@link User})
	 * @param resultIndex list index (0 if expecting single result)
	 * @param mapper the {@link RowMapper mapper} to be used for objects creation
	 * @return a set of constructed objects or empty set if none found
	 * @throws AccessDataBaseException translates SQLExceptions,
	 *         and 'out of bound' errors  will also fall into this category
	 */
	public <T> Set<T> getObjects(int resultIndex, RowMapper<T> mapper) {
		getResults();
		Set<T> set = new HashSet<>();
		try {

			if (results.size() > resultIndex) {
				ResultSet resultSet = results.get(resultIndex);
				int i = 1;
				do set.add(mapper.mapRow(resultSet, i++));
				while (resultSet.next());
			}

		} catch (Exception e) {
			closeAfter(e, "getting objects");
		}
		return set;
	}

	/**
	 * Set {@link Long} number at specified position
	 * @param pos The position is SQL 1-based index
	 * @param num a number to set
	 * @throws AccessDataBaseException translates
	 *        {@link PreparedStatement#setLong(int, long)} exception
	 */
	public void setLong(int pos, long num) {
		try {

			preparedStatement.setLong(pos, num);

		} catch (Exception e) {
			closeAfter(e, "setting long");
		}
	}

	/**
	 * Set String that can be null or empty to current statement at given position.
	 * @param pos The position is SQL 1-based index
	 * @param field the actual String to be set
	 * @throws AccessDataBaseException translates
	 *        {@link PreparedStatement#setString(int, String)} exception
	 */
	public void setString(int pos, String field) {
		try {

			preparedStatement.setString(pos, field);

		} catch (Exception e) {
			closeAfter(e, "setting string");
		}
	}

	/**
	 * Set String which is required for current statement at given position.
	 * @param pos The position is SQL 1-based index
	 * @param field the actual String to be set
	 * @param fieldName field name (for logging and error messages)
	 * @throws IllegalArgumentException if field is null or empty string
	 * @throws AccessDataBaseException translates
	 *        {@link PreparedStatement#setString(int, String)} exception
	 */
	public void setRequiredString(int pos, String field, String fieldName) {
		// if given field is null or empty the 'inner type' of exception will be issued
		if (field == null || field.length() == 0)
			throw new IllegalArgumentException(compose("{0} is not provided", fieldName));
		setString(pos, field);
	}

	// Close DB handlers when an exception was thrown
	private void closeAfter(Exception e, String stage) {
		String msg = compose(exMsg, doJob, stage);
		exception = new AccessDataBaseException(msg);
		exception.addSuppressed(e);

		isSuccessfullyClosed();

		logger.error(msg, exception);
		throw exception;
	}

	// 'exception' object must be instantiated before calling this method
	private boolean isSuccessfullyClosed() {
		boolean successful = true;
		if (preparedStatement != null) {
			try {

				preparedStatement.close();

			} catch (Exception e1) {
				exception.addSuppressed(e1);
				successful = false;
			}
		}
		// the Connection is pooled so we need to just release it
		try {

			DataSourceUtils.doReleaseConnection(connection, dataSource);

		} catch (Exception e2) {
			exception.addSuppressed(e2);
			successful = false;
		}
		return successful;
	}

}
