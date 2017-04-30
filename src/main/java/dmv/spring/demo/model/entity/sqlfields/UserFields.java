package dmv.spring.demo.model.entity.sqlfields;

import dmv.spring.demo.model.entity.User;

/**
 * SQL Fields (or Columns) that USER table has.
 * Representation of {@link User} entity
 * @author dmv
 */
public enum UserFields implements EntityFields {

	ID("ID"),
	EMAIL("EMAIL_ADRS"),
	FIRST_NAME("FIRST_NAME"),
	LAST_NAME("LAST_NAME"),
	MIDDLE_NAME("MIDDLE_NAME"),
	PASSWORD("PASSWORD");

	private String name;

	private UserFields(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
