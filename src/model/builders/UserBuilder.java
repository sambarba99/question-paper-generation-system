package model.builders;

import java.time.LocalDateTime;

import model.persisted.User;

import view.enums.UserType;

/**
 * This class utilises the builder pattern, and is used to build persisted User objects.
 *
 * @author Sam Barba
 */
public class UserBuilder {

	private String username;

	private String password;

	private UserType type;

	private LocalDateTime dateCreated;

	public UserBuilder() {
	}

	public UserBuilder withUsername(String username) {
		this.username = username;
		return this;
	}

	public UserBuilder withPassword(String password) {
		this.password = password;
		return this;
	}

	public UserBuilder withType(UserType type) {
		this.type = type;
		return this;
	}

	public UserBuilder withDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public User build() {
		return new User(username, password, type, dateCreated);
	}
}
