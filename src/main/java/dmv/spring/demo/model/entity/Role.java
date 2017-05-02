package dmv.spring.demo.model.entity;

import static dmv.spring.demo.model.repository.RoleRepository.FULL_NAME_MAX_LENGTH;
import static dmv.spring.demo.model.repository.RoleRepository.SHORT_NAME_LENGTH;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;

import dmv.spring.demo.model.entity.apidocs.RoleApiDocs;

/**
 * Role entity POJO. What system access rights some user has:
 * Administrator or User or whatever.
 * <p>
 * This object has Validation restrictions on how many characters can be used
 * to represent the Short and the Full names. Although these constraints are
 * not checked upon setting and not guaranteed upon getting those fields.
 * @author dmv
 */
public class Role implements RoleApiDocs, Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull
	@Size(min=SHORT_NAME_LENGTH, max=SHORT_NAME_LENGTH)
	private String shortName;

	@JsonIgnore
	@NotNull
	@Size(max=FULL_NAME_MAX_LENGTH)
	private String fullName;
	/* Does not contain Set<User> by default */

	/**
	 * Default constructor. Creates empty object
	 */
	public Role() {}
	/**
	 * Copies over fields from given Role
	 * @param role A Role to copy from
	 * @throws IllegalArgumentException if argument is null
	 */
	public Role(Role role) {
		Assert.notNull(role, "Role can't be null");
		setShortName(role.getShortName());
		setFullName(role.getFullName());
	}
	/**
	 * Creates new Role with given fields (can be null or empty)
	 * @param shortName Short name of Role
	 * @param fullName Full name of Role
	 */
	public Role(String shortName, String fullName) {
		setShortName(shortName);
		setFullName(fullName);
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
	/**
	 * For polymorphic defensive copying.
	 * Creates a new instance with the same fields
	 * @return a copy of this object
	 */
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
