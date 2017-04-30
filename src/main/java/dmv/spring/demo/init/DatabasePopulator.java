package dmv.spring.demo.init;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

/**
 * Pre-populates DB with demo Data.
 * It is important that DB user with granted authority ('restjdbc' in this case)
 * is already present in running instance of MySQL DataBase
 * @author dmv
 */
@Component
public class DatabasePopulator implements ApplicationRunner {

	@Autowired
	private DataSource dataSource;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		// To create Stored Procedures properly
		populator.setSeparator("^;");
		populator.addScript(new ClassPathResource("/sql/db_init.sql"));
		populator.addScript(new ClassPathResource("/sql/db_populate.sql"));
		populator.addScript(new ClassPathResource("/sql/stored_procedures/create_user_with_roles_cp.sql"));
		populator.addScript(new ClassPathResource("/sql/stored_procedures/update_user_with_roles_cp.sql"));
		populator.populate(dataSource.getConnection());
	}

}
