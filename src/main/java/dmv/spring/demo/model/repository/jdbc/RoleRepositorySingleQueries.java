package dmv.spring.demo.model.repository.jdbc;

import static dmv.spring.demo.model.repository.jdbc.Mappers.ROLE_MAPPER;
import static dmv.spring.demo.model.repository.jdbc.Mappers.USER_MAPPER;
import static dmv.spring.demo.model.repository.jdbc.sql.RoleQueriesSQL.ROLE_FIND_BY_SHORT_NAME;
import static dmv.spring.demo.model.repository.jdbc.sql.RoleQueriesSQL.ROLE_GET_USERS;
import static dmv.spring.demo.util.MessageComposer.compose;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import dmv.spring.demo.model.entity.Role;
import dmv.spring.demo.model.entity.User;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;
import dmv.spring.demo.model.repository.RoleRepository;

/**
 * Querying table ROLE via JDBC according to
 * {@link RoleRepository} specification.
 * <p>Single statement SQL queries are used
 * <p>Offers simple caching solution as for now we have
 * only three predefined {@link Role roles} and no mutators.
 * So, avoiding unnecessary DB querying.
 * @author dmv
 */
@Repository
@Transactional(readOnly=true)
public class RoleRepositorySingleQueries implements RoleRepository {

	private final Logger logger = getLogger(getClass());

	private final DataSource dataSource;
	
	/*
	 * There are just three Roles available.
	 * Simple caching solution would suffice.
	 */
	private final Map<String, Role> cache = new HashMap<>();
	
	@Autowired
	public RoleRepositorySingleQueries(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	
	@Override
	public Role findByShortName(String shortName) {
		if (!RoleRepository.isValidShortName(shortName))
			throwNotExist(shortName);
		Role role = cache.get(shortName);
		if (role == null) {
			/*
			 * Synchronization is not necessary for real-world scenarios.
			 * And there is just a small improvement in synthetic tests.
			 * But this is a Demo application, so let it be.
			 * Note, that special concurrent collection (like ConcurrentHashMap)
			 * is not needed that way (because it is redundant).
			 */
			synchronized (cache) {
				role = cache.get(shortName);
				if (role == null) {
					try (JdbcConnector connector = 
							new JdbcConnector(dataSource, ROLE_FIND_BY_SHORT_NAME)) {
						
						connector.setRequiredString(1, shortName, "short name");
						
						role = connector.getObject(0, ROLE_MAPPER);
						if (role == null)
							throwNotExist(shortName);
						
						cache.put(shortName, role);
						logger.debug("Role {} has been cached", shortName);
					}
				}
			}
		}
		logger.debug("Role {} has been supplied from findByShortName() method", shortName);
		// we need to detach connection to cached version
		return role.copy();
	}

	@Override
	public Set<User> getUsers(Role role) {
		Assert.notNull(role, "Role can't be null");
		String shortName = role.getShortName();
		if (!RoleRepository.isValidShortName(shortName))
			throwNotExist(shortName);
		
		try (JdbcConnector connector = 
				new JdbcConnector(dataSource, ROLE_GET_USERS)) {

			connector.setRequiredString(1, shortName, "short name");

			Set<User> users = connector.getObjects(0, USER_MAPPER);

			return users;
		}
	}

	private void throwNotExist(Object param) {
		String msg = compose("Role with short name {0} does not exist", param);
		logger.debug(msg);
		throw new EntityDoesNotExistException(msg);
	}
}
