/**
 * 
 */
package dmv.spring.demo.model.entity;

import java.util.Set;

/**
 * @author user
 */
public class Role {

	private String shortName;
	private String fullName;
	/* Does not contain Set<User> by default */
	
	public String getShortName() {
		return shortName;
	}
	public String getFullName() {
		return fullName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}
