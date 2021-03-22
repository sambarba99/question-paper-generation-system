package model.persisted;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import view.SystemNotification;
import view.enums.SystemNotificationType;
import view.enums.UserType;
import view.utils.Constants;
import view.utils.SecurityUtils;

/**
 * Represents a user.
 *
 * @author Sam Barba
 */
public class User {

	private String username;

	private String password;

	private UserType type;

	public User(String username, String password, UserType type) {
		this.username = username;
		this.password = password;
		this.type = type;
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

	public void encryptPassword() {
		try {
			setPassword(SecurityUtils.getInstance().sha512(password));
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + e.getClass().getName());
		}
	}

	@Override
	public String toString() {
		return username + " (" + type.toString() + ")";
	}
}
