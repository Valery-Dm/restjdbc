package dmv.spring.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class RestjdbcApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestjdbcApplication.class, args);
	}
}
