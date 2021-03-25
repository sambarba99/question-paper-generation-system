package view.enums;

import java.util.Arrays;

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
		return Arrays.stream(values())
			.filter(uPriv -> uPriv.toString().equals(strVal))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Invalid privilege String value passed: " + strVal));
	}
}
