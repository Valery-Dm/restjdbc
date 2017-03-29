/**
 * 
 */
package dmv.spring.demo.model.entity;

import java.util.Set;

/**
 * @author user
 */
public class User {

	private String email;
	private String firstName;
	private String lastName;
	private String middleName;
	private String password;
	private Set<Role> roles;
	
	public String getEmail() {
		return email;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public String getPassword() {
		return password;
	}
	public Set<Role> getRoles() {
		return roles;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	@Override
	public String toString() {
		return "User [email=" + email + ", firstName=" + firstName + ", lastName=" + lastName + ", middleName="
				+ middleName + ", password=" + password + ", roles=" + roles + "]";
	}
	
}
