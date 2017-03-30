/**
 * 
 */
package dmv.spring.demo.model.entity;

/**
 * @author user
 */
public enum RoleFields implements EntityFields {
	ROLE_TABLE("`users_demo`.`ROLE`"),
	SHORT_NAME("SHORT_NAME"),
	FULL_NAME("FULL_NAME");
	
	private String name;

	private RoleFields(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
