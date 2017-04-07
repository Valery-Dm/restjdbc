package dmv.spring.demo.model.entity.sqlfields;

/**
 * 'ROLE_USERS' table and column names
 * @author dmv
 */
public enum RoleUsersFields {
	RU_TABLE("`users_demo`.`ROLE_USERS`"),
	ROLE_ID("ROLE_ID"),
	USER_ID("USER_ID");

	private String name;

	private RoleUsersFields(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
