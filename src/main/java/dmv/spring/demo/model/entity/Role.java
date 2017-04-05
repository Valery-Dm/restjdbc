package dmv.spring.demo.model.entity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Role entity POJO. Means what role user has:
 * administrator or user or whatever
 * @author dmv
 */
public class Role implements RoleApiDocs {

	@NotNull
	@Size(min=3, max=3)
	private String shortName;

	@JsonIgnore
	@NotNull
	@Size(max=50)
	private String fullName;
	/* Does not contain Set<User> by default */

	public Role() {}
	public Role(String shortName, String fullName) {
		this.shortName = shortName;
		this.fullName = fullName;
	}
	public String getShortName() {
		return shortName;
	}
	public String getFullName() {
		return fullName;
	}
	@Override
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	// For polymorphic defensive copying
	public Role copy() {
		return new Role(shortName, fullName);
	}
	@Override
	public String toString() {
		return "Role [shortName=" + shortName + ", fullName=" + fullName + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fullName == null) ? 0 : fullName.hashCode());
		result = prime * result + ((shortName == null) ? 0 : shortName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Role other = (Role) obj;
		if (fullName == null) {
			if (other.fullName != null)
				return false;
		} else if (!fullName.equals(other.fullName))
			return false;
		if (shortName == null) {
			if (other.shortName != null)
				return false;
		} else if (!shortName.equals(other.shortName))
			return false;
		return true;
	}
}
