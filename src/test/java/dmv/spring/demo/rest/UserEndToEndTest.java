package dmv.spring.demo.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import dmv.spring.demo.model.entity.User;

@Ignore

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@DirtiesContext
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, 
                properties={ "server.ssl.enabled=false", "logging.level.root=debug" })
@WithMockUser(username = "admin", authorities = { "ADM", "USR" })
public class UserEndToEndTest implements TestHelpers {

		@Autowired
		private TestRestTemplate restTemplate;

		@Test
		public void exampleTest() {
			restTemplate.getRestTemplate().setMessageConverters(getMessageConverters());
			User body = restTemplate.getForObject(buildURL(1L), User.class);
			System.out.println(body);
			assertThat(body).isEqualTo("Hello World");
		}
		
		private List<HttpMessageConverter<?>> getMessageConverters() {
		    List<HttpMessageConverter<?>> converters = 
		      new ArrayList<HttpMessageConverter<?>>();
		    converters.add(new MappingJackson2HttpMessageConverter());
		    return converters;
		}

	}
