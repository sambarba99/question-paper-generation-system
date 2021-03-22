package view.enums;

import java.util.Arrays;

import view.utils.Constants;

/**
 * Represents question difficulty - values are in ascending order of difficulty, based on Bloom's taxonomy for
 * classifying educational objectives.
 *
 * @author Sam Barba
 */
public enum DifficultyLevel {

	KNOWLEDGE(1, "KNOWLEDGE"),
	COMPREHENSION(2, "COMPREHENSION"),
	APPLICATION(3, "APPLICATION"),
	ANALYSIS(4, "ANALYSIS"),
	SYNTHESIS(5, "SYNTHESIS"),
	EVALUATION(6, "EVALUATION");

	private int intVal;

	private String strVal;

	DifficultyLevel(int intVal, String strVal) {
		this.intVal = intVal;
		this.strVal = strVal;
	}

	public int getIntVal() {
		return intVal;
	}

	public String getStrVal() {
		return strVal;
	}

	public String getDisplayStr() {
		return intVal + " (" + strVal.toLowerCase() + ")";
	}

	/**
	 * Retrieve DifficultyLevel given an int value. Throw an IllegalArgumentException if the int value doesn't exist.
	 * 
	 * @param intVal - the int value of the DifficultyLevel
	 * @return the DifficultyLevel with the specified int value
	 */
	public static DifficultyLevel getFromInt(int intVal) {
		return Arrays.asList(values())
			.stream()
			.filter(lvl -> lvl.getIntVal() == intVal)
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Invalid Difficulty Level int passed: " + intVal));
	}

	/**
	 * Retrieve DifficultyLevel given a String value. Throw an IllegalArgumentException if the String value doesn't
	 * exist.
	 * 
	 * @param strVal - the String value of the DifficultyLevel
	 * @return the DifficultyLevel with the specified String value
	 */
	public static DifficultyLevel getFromStr(String strVal) {
		return Arrays.asList(values())
			.stream()
			.filter(lvl -> lvl.getStrVal().equals(strVal))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Invalid Difficulty Level string passed: " + strVal));
	}

	/**
	 * Get int value of selected difficulty level in ListView or CheckBox etc. from its display string, e.g. "1
	 * (knowledge)".
	 * 
	 * @return the int value of the selected difficulty level
	 */
	public static int getIntFromDisplayStr(String difficultyLvlDisplayStr) {
		String[] split = difficultyLvlDisplayStr.split(Constants.SPACE);
		return Integer.parseInt(split[0]);
	}
}
