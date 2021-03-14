package view.enums;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import view.Constants;

import controller.SystemNotification;

/**
 * Represents question difficulty.
 *
 * @author Sam Barba
 */
public enum DifficultyLevel {

	EASY("EASY", 1),
	MEDIUM("MEDIUM", 2),
	DIFFICULT("DIFFICULT", 3);

	private String strVal;

	private int intVal;

	DifficultyLevel(String strVal, int intVal) {
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
	 * Retrieve DifficultyLevel given an int value. Throw an IllegalArgumentException if the int value doesn't exist.
	 * 
	 * @param intVal - the int value of the DifficultyLevel
	 * @return the DifficultyLevel with the specified int value
	 */
	public static DifficultyLevel getFromInt(int intVal) {
		List<DifficultyLevel> allDifficulties = new ArrayList<>(EnumSet.allOf(DifficultyLevel.class));
		DifficultyLevel difficultyLevel = allDifficulties.stream()
			.filter(difficultyLvl -> difficultyLvl.getIntVal() == intVal)
			.findFirst()
			.orElse(null);
		if (difficultyLevel != null) {
			return difficultyLevel;
		}
		SystemNotification.display(SystemNotificationType.ERROR,
			Constants.UNEXPECTED_ERROR + "Invalid Difficulty Level passed: " + intVal);
		throw new IllegalArgumentException("Invalid Difficulty Level passed: " + intVal);
	}
}
