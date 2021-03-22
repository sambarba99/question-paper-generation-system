package view.enums;

import java.util.Arrays;

import view.SystemNotification;
import view.utils.Constants;

/**
 * Represents the privilege level of a user.
 *
 * @author Sam Barba
 */
public enum UserPrivilege {

	ADMIN,
	TUTOR;

	/**
	 * Retrieve UserPrivilege given a String value. Throw an IllegalArgumentException if the value doesn't exist.
	 * 
	 * @param strVal - the String value of the UserPrivilege
	 * @return privilege - the UserPrivilege with the specified String value
	 */
	public static UserPrivilege getFromStr(String strVal) {
		UserPrivilege privilege = Arrays.asList(values())
			.stream()
			.filter(uPrivilege -> uPrivilege.toString().equals(strVal))
			.findFirst()
			.orElse(null);
		if (privilege != null) {
			return privilege;
		}
		SystemNotification.display(SystemNotificationType.ERROR,
			Constants.UNEXPECTED_ERROR + "Invalid User Privilege String value passed: " + strVal);
		throw new IllegalArgumentException("Invalid User Privilege int value passed: " + strVal);
	}
}
