package view.enums;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import view.SystemNotification;
import view.utils.Constants;

/**
 * Represents question difficulty - values are in ascending order of difficulty, based on Bloom's taxonomy for
 * classifying educational objectives.
 *
 * @author Sam Barba
 */
public enum DifficultyLevel {

	KNOWLEDGE("KNOWLEDGE", 1),
	COMPREHENSION("COMPREHENSION", 2),
	APPLICATION("APPLICATION", 3),
	ANALYSIS("ANALYSIS", 4),
	SYNTHESIS("SYNTHESIS", 5),
	EVALUATION("EVALUATION", 6);

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
	 * Retrieve DifficultyLevel given a String value. Throw an IllegalArgumentException if the String value doesn't
	 * exist.
	 * 
	 * @param strVal - the String value of the DifficultyLevel
	 * @return the DifficultyLevel with the specified String value
	 */
	public static DifficultyLevel getFromStr(String strVal) {
		List<DifficultyLevel> allLevels = new ArrayList<>(EnumSet.allOf(DifficultyLevel.class));
		DifficultyLevel difficultyLevel = allLevels.stream()
			.filter(lvl -> lvl.getStrVal().equals(strVal))
			.findFirst()
			.orElse(null);
		if (difficultyLevel != null) {
			return difficultyLevel;
		}
		SystemNotification.display(SystemNotificationType.ERROR,
			Constants.UNEXPECTED_ERROR + "Invalid Difficulty Level string passed: " + strVal);
		throw new IllegalArgumentException("Invalid Difficulty Level string passed: " + strVal);
	}

	/**
	 * Retrieve DifficultyLevel given an int value. Throw an IllegalArgumentException if the int value doesn't exist.
	 * 
	 * @param intVal - the int value of the DifficultyLevel
	 * @return the DifficultyLevel with the specified int value
	 */
	public static DifficultyLevel getFromInt(int intVal) {
		List<DifficultyLevel> allLevels = new ArrayList<>(EnumSet.allOf(DifficultyLevel.class));
		DifficultyLevel difficultyLevel = allLevels.stream()
			.filter(lvl -> lvl.getIntVal() == intVal)
			.findFirst()
			.orElse(null);
		if (difficultyLevel != null) {
			return difficultyLevel;
		}
		SystemNotification.display(SystemNotificationType.ERROR,
			Constants.UNEXPECTED_ERROR + "Invalid Difficulty Level int passed: " + intVal);
		throw new IllegalArgumentException("Invalid Difficulty Level int passed: " + intVal);
	}
}
