package dmv.spring.demo.model.entity;

import static dmv.spring.demo.model.repository.UserRepository.EMAIL_MIN;
import static dmv.spring.demo.model.repository.UserRepository.LONG_MAX;
import static dmv.spring.demo.model.repository.UserRepository.NOT_EMPTY;
import static dmv.spring.demo.model.repository.UserRepository.SHORT_MAX;

import java.io.Serializable;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;

import dmv.spring.demo.model.entity.apidocs.UserApiDocs;

/**
 * User entity POJO. This class has some Validation restrictions
 * on how many characters can be used to represent various fields,
 * although these constraints are not enforced inside mutator methods.
 * @author dmv
 */
public class User implements UserApiDocs, Serializable {

	private static final long serialVersionUID = 1L;

	@JsonIgnore
	private Long id;

	@NotNull
	@Size(min = EMAIL_MIN, max = LONG_MAX)
	@Email
	private String email;

	@NotNull
	@Size(min = NOT_EMPTY, max = SHORT_MAX)
	private String firstName;

	@NotNull
	@Size(min = NOT_EMPTY, max = LONG_MAX)
	private String lastName;

	@Size(max = SHORT_MAX)
	private String middleName;

	/* Prevent passwords from being serialized to disk */
	transient
	private String password;

	/*
	 * Roles are stored in a HashSet. There are certain problems exist with
	 * hashes serialization, and I have no intention to deal with them
	 * in this workflow. User roles could be skipped altogether.
	 */
	transient
	private Set<Role> roles;

	/**
	 * Default constructor creates an empty object
	 */
	public User() {}
	/**
	 * Creates a shallow copy instance of given User
	 * @param user A user to copy fields from
	 * @throws IllegalArgumentException if given argument is null
	 */
	public User(User user) {
		Assert.notNull(user, "User can't be null");
		id = user.getId();
		email = user.getEmail();
		firstName = user.getFirstName();
		lastName = user.getLastName();
		middleName = user.getMiddleName();
		password = user.getPassword();
		roles = user.getRoles();
	}
	public Long getId() {
		return id;
	}
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
	public void setId(Long id) {
		this.id = id;
	}
	@Override
	public void setEmail(String email) {
		this.email = email;
	}
	@Override
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	@Override
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	@Override
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	@Override
	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", email=" + email + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", middleName=" + middleName + ", password=" + password + ", roles=" + roles + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((middleName == null) ? 0 : middleName.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
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
		User other = (User) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (middleName == null) {
			if (other.middleName != null)
				return false;
		} else if (!middleName.equals(other.middleName))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (roles == null) {
			if (other.roles != null)
				return false;
		} else if (!roles.equals(other.roles))
			return false;
		return true;
	}
}
