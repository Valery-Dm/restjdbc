package dmv.spring.demo.model.entity;

/**
 * SQL Fields (or Columns) that User type has
 * @author dmv
 */
public enum UserFields implements EntityFields {

	USER_TABLE("`users_demo`.`USER`"),
	ID("ID"), EMAIL("EMAIL_ADRS"),
	FIRST_NAME("FIRST_NAME"), LAST_NAME("LAST_NAME"),
	MIDDLE_NAME("MIDDLE_NAME"), PASSWORD("PASSWORD");

	private String name;

	private UserFields(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
