package dmv.spring.demo.model.entity.sqlfields;

/**
 * 'ROLE_USERS' table - column names
 * @author dmv
 */
public enum RoleUsersFields implements EntityFields {
	ROLE_ID("ROLE_ID"),
	USER_ID("USER_ID");

	private String name;

	private RoleUsersFields(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

}
