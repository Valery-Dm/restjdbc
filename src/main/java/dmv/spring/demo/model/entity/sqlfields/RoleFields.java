package dmv.spring.demo.model.entity.sqlfields;

import dmv.spring.demo.model.entity.Role;

/**
 * SQL Fields (or Columns) that ROLE table has.
 * Representation of {@link Role} entity.
 * @author dmv
 */
public enum RoleFields implements EntityFields {
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
