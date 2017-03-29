/**
 * 
 */
package dmv.spring.demo.model.entity;

/**
 * @author user
 */
public enum UserFields {
			
	USER_TABLE("`users_demo`.`USER`"), 
	ID("ID"), EMAIL("EMAIL_ADRS"), 
	FIRST_NAME("FIRST_NAME"), LAST_NAME("LAST_NAME"), 
	MIDDLE_NAME("MIDDLE_NAME"), PASSWORD("PASSWORD");
	
	private String name;

	private UserFields(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
