package dmv.spring.demo.model.entity.apidocs;

import io.swagger.annotations.ApiModelProperty;

/**
 * Swagger (API Documentation) annotations
 * @author dmv
 */
public interface RoleApiDocs {

	@ApiModelProperty(value="Short name for users role. There are 3 available now: ADM, USR, DEV", required=true, example="USR")
	void setShortName(String shortName);

}