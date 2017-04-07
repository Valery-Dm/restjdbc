package dmv.spring.demo.model.entity;

/**
 * SQL Fields (or Columns) that Role type has
 * @author dmv
 */
public enum RoleFields implements EntityFields {
	ROLE_TABLE("`users_demo`.`ROLE`"),
	SHORT_NAME("SHORT_NAME"),
	FULL_NAME("FULL_NAME");

	private String name;

	private RoleFields(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

}
