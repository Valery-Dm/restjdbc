package dmv.spring.demo.model.repository.jdbc;

import static dmv.spring.demo.model.repository.jdbc.Mappers.ROLE_MAPPER;
import static dmv.spring.demo.model.repository.jdbc.Mappers.USER_AUTH_MAPPER;
import static dmv.spring.demo.model.repository.jdbc.sql.CredentialsQueriesSQL.USER_GET_CREDENTIALS;
import static dmv.spring.demo.util.MessageComposer.compose;
import static org.slf4j.LoggerFactory.getLogger;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.exceptions.EntityAlreadyExistsException;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;
import dmv.spring.demo.model.repository.CredentialsRepository;
import dmv.spring.demo.security.CredentialsService;

/**
 * JDBC implementation of {@link CredentialsRepository} interface.
 * <p>
 * All methods run within transactions.
 * @author dmv
 */
@Repository
@Transactional
public class CredentialsRepositoryJDBC implements CredentialsRepository {

	private final Logger logger = getLogger(getClass());

	@Autowired
	private DataSource dataSource;

	@Autowired
	private CredentialsService credentialsService;

	@Override
	public User getCredentials(String email) {
		Assert.notNull(email, "email can't be null");
		try (JdbcConnector connector =
				new JdbcConnector(dataSource, USER_GET_CREDENTIALS)) {

			connector.setRequiredString(1, email, "email");
			/*
			 * We are expecting next results:
			 * 0. User    fields with PASSWORD
			 * 1. Roles   table
			 */
			User user = connector.getObject(0, USER_AUTH_MAPPER);
			if (user == null)
				throwNotExist("User", "email", email);
			user.setRoles(connector.getObjects(1, ROLE_MAPPER));

			logger.debug("User with email {} was found in DB", email);
			return user;
		}
	}

	@Override
	public void changePassword(User user, String newPassword) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}

	@Override
	public void changeEmail(User user, String newEmail) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}


	private void throwNotExist(String entity, String paramName, Object param) {
		String msg = compose("{0} with {1} {2} does not exist", entity, paramName, param);
		logger.debug(msg);
		throw new EntityDoesNotExistException(msg);
	}

	private void throwAlreadyExists(String paramName, Object param) {
		String msg = compose("User with {0} {1} already exists", paramName, param);
		logger.debug(msg);
		throw new EntityAlreadyExistsException(msg);
	}

}
