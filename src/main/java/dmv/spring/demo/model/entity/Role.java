/**
 * 
 */
package dmv.spring.demo.model.entity;

import java.util.Set;

/**
 * @author user
 *
 */
public class Role {

	private String shortName;
	private String fullName;
	private Set<User> users;
	
	public String getShortName() {
		return shortName;
	}
	public String getFullName() {
		return fullName;
	}
	public Set<User> getUsers() {
		return users;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public void setUsers(Set<User> users) {
		this.users = users;
	}
}
