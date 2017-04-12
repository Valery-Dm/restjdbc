package dmv.spring.demo.init;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

/*
 * It is important that DB user with granted authority ('restjdbc' in this case)
 * is already present in running instance of MySQL DataBase
 */
/**
 * Pre-populates DB with demo Data.
 *
 * @author dmv
 */
@Component
public class DatabasePopulator implements ApplicationRunner {

	@Autowired
	private DataSource dataSource;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScript(new ClassPathResource("/sql/db_init.sql"));
		populator.populate(dataSource.getConnection());
	}

}
