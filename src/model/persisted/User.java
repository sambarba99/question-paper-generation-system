package model.persisted;

import java.time.LocalDateTime;

import view.enums.UserPrivilege;

/**
 * Represents a user.
 *
 * @author Sam Barba
 */
public class User {

	private String username;

	private String password;

	private UserPrivilege privilege;

	private LocalDateTime dateCreated;

	public User(String username, String password, UserPrivilege privilege, LocalDateTime dateCreated) {
		this.username = username;
		this.password = password;
		this.privilege = privilege;
		this.dateCreated = dateCreated;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserPrivilege getPrivilege() {
		return privilege;
	}

	public void setPrivilege(UserPrivilege privilege) {
		this.privilege = privilege;
	}

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Override
	public String toString() {
		return username + " (" + privilege.toString() + ")";
	}
}
