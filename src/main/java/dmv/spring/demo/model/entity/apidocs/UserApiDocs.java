package dmv.spring.demo.model.entity.apidocs;

import java.util.Set;

import dmv.spring.demo.model.entity.Role;
import io.swagger.annotations.ApiModelProperty;

/**
 * Swagger API Documentation.
 * Each method is annotated with Springfox annotations (Swagger generator)
 * @author dmv
 */
public interface UserApiDocs {

	@ApiModelProperty(value="Unique e-mail address (min=5,max=70)", required=true, example="some@mail.com")
	void setEmail(String email);

	@ApiModelProperty(value="first name (min=1,max=45)", required=true, example="Ivan")
	void setFirstName(String firstName);

	@ApiModelProperty(value="last name (min=1,max=70)", required=true, example="Grozny")
	void setLastName(String lastName);

	@ApiModelProperty(value="middle name (max=45)", example="Vasilievich")
	void setMiddleName(String middleName);

	@ApiModelProperty(value="password will be generated if not given", example="pa$$word")
	void setPassword(String password);

	@ApiModelProperty(value="user roles as list")
	void setRoles(Set<Role> roles);

}