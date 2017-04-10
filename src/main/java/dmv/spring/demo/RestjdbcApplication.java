package dmv.spring.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication(exclude = {
		ErrorMvcAutoConfiguration.class,
		RepositoryRestMvcAutoConfiguration.class })
@EnableTransactionManagement
@EnableHypermediaSupport(type = {HypermediaType.HAL})
@PropertySource("classpath:UserRestController.properties")
public class RestjdbcApplication {


	public static void main(String[] args) {
		SpringApplication.run(RestjdbcApplication.class, args);
	}

}
