package model.enums;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import interfacecontroller.SystemNotification;

import utils.Constants;

/**
 * Represents the type of user which determines their privilege.
 *
 * @author Sam Barba
 */
public enum UserType {

	ADMIN("ADMIN", 0),
	TUTOR("TUTOR", 1);

	private String strVal;

	private int intVal;

	UserType(String strVal, int intVal) {
		this.strVal = strVal;
		this.intVal = intVal;
	}

	public String getStrVal() {
		return strVal;
	}

	public int getIntVal() {
		return intVal;
	}

	/**
	 * Retrieve UserType given an int value. Throw an IllegalArgumentException if the int value doesn't exist.
	 * 
	 * @param intVal - the int value of the UserType
	 * @return userType - the UserType with the specified int value
	 */
	public static UserType getFromInt(int intVal) {
		List<UserType> allUserTypes = new ArrayList<>(EnumSet.allOf(UserType.class));
		UserType userType = allUserTypes.stream().filter(uType -> uType.getIntVal() == intVal).findFirst().orElse(null);
		if (userType != null) {
			return userType;
		}
		SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + "Invalid User Type int value passed: " + intVal);
		throw new IllegalArgumentException("Invalid User Type int value passed: " + intVal);
	}
}
