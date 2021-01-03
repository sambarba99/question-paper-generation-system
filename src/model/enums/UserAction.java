package model.enums;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import interfacecontroller.SystemNotification;

import utils.Constants;

/**
 * Represents the actions that a user can perform.
 *
 * @author Sam Barba
 */
public enum UserAction {

	ADD_SUBJECT("Add new subject", 0),
	DELETE_SUBJECT("Delete subject", 1),
	TOGGLE_FILTER_PAPERS("Toggle filter papers by subject(s)", 2),
	VIEW_MODIFY_ALL_QUESTIONS("View/modify all questions", 3),
	GENERATE_QUESTION_PAPER("Generate question paper", 4),
	VIEW_QUESTION_PAPER("View/export question paper", 5),
	DELETE_QUESTION_PAPER("Delete question paper", 6),
	UPDATE_PASSWORD("Update password", 7),
	NONE("", 8);

	private String strVal;

	private int intVal;

	UserAction(String strVal, int intVal) {
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
	 * Retrieve UserAction given an int value. Throw an IllegalArgumentException if the int value doesn't exist.
	 * 
	 * @param intVal - the int value of the UserAction
	 * @return action - the UserAction with the specified int value
	 */
	public static UserAction getFromInt(int intVal) {
		List<UserAction> allActions = new ArrayList<>(EnumSet.allOf(UserAction.class));
		UserAction userAction = allActions.stream().filter(userAct -> userAct.getIntVal() == intVal).findFirst()
				.orElse(null);
		if (userAction != null) {
			return userAction;
		}
		SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + "Invalid User Action int value passed: " + intVal);
		throw new IllegalArgumentException("Invalid User Action int value passed: " + intVal);
	}
}
