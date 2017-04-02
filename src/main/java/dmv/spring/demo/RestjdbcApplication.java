package dmv.spring.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableHypermediaSupport(type = {HypermediaType.HAL})
public class RestjdbcApplication {


	public static void main(String[] args) {
		SpringApplication.run(RestjdbcApplication.class, args);
	}
}
