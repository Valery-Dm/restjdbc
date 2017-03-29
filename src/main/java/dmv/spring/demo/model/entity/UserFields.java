/**
 * 
 */
package dmv.spring.demo.model.entity;

/**
 * @author user
 *
 */
public enum UserFields {
	TABLE("`users_demo`.`USER`"), ID("ID"), EMAIL("EMAIL_ADRS"), 
	FIRST_NAME("FIRST_NAME"), LAST_NAME("LAST_NAME"), 
	MIDDLE_NAME("MIDDLE_NAME"), PASSWORD("PASSWORD");
	
	private String columnName;

	private UserFields(String columnName) {
		this.columnName = columnName;
	}

	public String getName() {
		return columnName;
	}
}
