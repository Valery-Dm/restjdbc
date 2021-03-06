package dmv.spring.demo.apidocs;
import static springfox.documentation.schema.AlternateTypeRules.newRule;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import com.fasterxml.classmate.TypeResolver;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger (API documenting tool) configuration
 *
 * @author dmv
 */
@Configuration
@EnableSwagger2
@PropertySource("classpath:/apidocs/swagger-config.properties")
public class SwaggerConfig {

    @Value("${swagger.api.info.title}")
    private String title;

    @Value("${swagger.api.info.description}")
    private String description;

    @Value("${swagger.api.info.version}")
    private String version;

    @Value("${swagger.api.info.license.info}")
    private String licenseInfo;

    @Value("${swagger.api.info.license.url}")
    private String licenseUrl;

    @Value("${swagger.api.info.terms}")
    private String terms;

    @Value("${swagger.api.info.contact.name}")
    private String contactName;

    @Value("${swagger.api.info.contact.url}")
    private String contactUrl;

    @Value("${swagger.api.info.contact.email}")
    private String contactEmail;

    @Autowired
    private TypeResolver typeResolver;

	@Bean
	public Docket restJdbcApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
    				.apis(RequestHandlerSelectors.any())
    				.paths(PathSelectors.any())
    				.build()
    			.apiInfo(getApiInfo())
				.pathMapping("/")
				.directModelSubstitute(LocalDate.class, String.class)
				.genericModelSubstitutes(ResponseEntity.class)
				.alternateTypeRules(newRule(
						typeResolver.resolve(DeferredResult.class,
						typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
						typeResolver.resolve(WildcardType.class)))
				.useDefaultResponseMessages(false)
				.enableUrlTemplating(false);
	}

	@Bean
	UiConfiguration uiConfig() {
		return new UiConfiguration(
				null, // url
				"none", // docExpansion => none | list
				"alpha", // apiSorter => alpha
				"schema", // defaultModelRendering => schema
				UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS,
				false, // enableJsonEditor => true | false
				true, // showRequestHeaders => true | false
				60000L); // requestTimeout => in milliseconds, defaults to null
							// (uses jquery xh timeout)
	}

	private ApiInfo getApiInfo() {
        return new ApiInfo(
                title,
                description,
                version,
                terms,
                new Contact(contactName, contactUrl, contactEmail),
                licenseInfo,
                licenseUrl
        );
    }

}
