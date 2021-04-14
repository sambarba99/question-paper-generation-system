package view.enums;

import java.util.Arrays;

/**
 * Represents question skill level - values are based on Bloom's taxonomy for classifying
 * educational objectives.
 *
 * @author Sam Barba
 */
public enum BloomSkillLevel {

	KNOWLEDGE(1, "Knowledge"),
	COMPREHENSION(2, "Comprehension"),
	APPLICATION(3, "Application"),
	ANALYSIS(4, "Analysis"),
	SYNTHESIS(5, "Synthesis"),
	EVALUATION(6, "Evaluation");

	private int intVal;

	private String strVal;

	BloomSkillLevel(int intVal, String strVal) {
		this.intVal = intVal;
		this.strVal = strVal;
	}

	public int getIntVal() {
		return intVal;
	}

	@Override
	public String toString() {
		return strVal;
	}

	/**
	 * Retrieve SkillLevel given an int value. Throw an IllegalArgumentException if the int value
	 * doesn't exist.
	 * 
	 * @param intVal - the int value of the SkillLevel
	 * @return the SkillLevel with the specified int value
	 */
	public static BloomSkillLevel getFromInt(int intVal) {
		return Arrays.stream(values())
			.filter(lvl -> lvl.getIntVal() == intVal)
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Invalid Bloom Skill Level int passed: " + intVal));
	}

	/**
	 * Retrieve SkillLevel given a String value. Throw an IllegalArgumentException if the String
	 * value doesn't exist.
	 * 
	 * @param strVal - the String value of the SkillLevel
	 * @return the SkillLevel with the specified String value
	 */
	public static BloomSkillLevel getFromStr(String strVal) {
		return Arrays.stream(values())
			.filter(lvl -> lvl.toString().toUpperCase().equals(strVal.toUpperCase()))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Invalid Bloom Skill Level passed: " + strVal));
	}
}
