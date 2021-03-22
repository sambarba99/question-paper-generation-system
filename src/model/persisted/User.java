package model.persisted;

import java.time.LocalDateTime;

import view.enums.UserType;

/**
 * Represents a user.
 *
 * @author Sam Barba
 */
public class User {

	private String username;

	private String password;

	private UserType type;

	private LocalDateTime dateCreated;

	public User(String username, String password, UserType type, LocalDateTime dateCreated) {
		this.username = username;
		this.password = password;
		this.type = type;
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

	public UserType getType() {
		return type;
	}

	public void setType(UserType type) {
		this.type = type;
	}

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Override
	public String toString() {
		return username + " (" + type.toString() + ")";
	}
}
