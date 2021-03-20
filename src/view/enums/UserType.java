package view.enums;

import java.util.Arrays;

import view.SystemNotification;
import view.utils.Constants;

/**
 * Represents the type of user which determines their privilege.
 *
 * @author Sam Barba
 */
public enum UserType {

	ADMIN,
	TUTOR;

	/**
	 * Retrieve UserType given a String value. Throw an IllegalArgumentException if the value doesn't exist.
	 * 
	 * @param strVal - the String value of the UserType
	 * @return userType - the UserType with the specified String value
	 */
	public static UserType getFromStr(String strVal) {
		UserType userType = Arrays.asList(values())
			.stream()
			.filter(uType -> uType.toString().equals(strVal))
			.findFirst()
			.orElse(null);
		if (userType != null) {
			return userType;
		}
		SystemNotification.display(SystemNotificationType.ERROR,
			Constants.UNEXPECTED_ERROR + "Invalid User Type String value passed: " + strVal);
		throw new IllegalArgumentException("Invalid User Type int value passed: " + strVal);
	}
}
