package view.enums;

import java.util.Arrays;

/**
 * Represents question skill level - values are based on Bloom's taxonomy for classifying
 * educational objectives.
 *
 * @author Sam Barba
 */
public enum BloomSkillLevel {

    KNOWLEDGE(1, "KNOWLEDGE"),
    COMPREHENSION(2, "COMPREHENSION"),
    APPLICATION(3, "APPLICATION"),
    ANALYSIS(4, "ANALYSIS"),
    SYNTHESIS(5, "SYNTHESIS"),
    EVALUATION(6, "EVALUATION");

    private int intVal;

    private String strVal;

    BloomSkillLevel(int intVal, String strVal) {
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
            .orElseThrow(() -> new IllegalArgumentException("Invalid Skill Level int passed: " + intVal));
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
            .filter(lvl -> lvl.getStrVal().equals(strVal))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid Skill Level string passed: " + strVal));
    }

    /**
     * Get int value of selected skill level in ListView or CheckBox etc. from its display string,
     * e.g. "1 (knowledge)".
     * 
     * @return the int value of the selected skill level
     */
    public static int getIntFromDisplayStr(String skillLvlDisplayStr) {
        String[] split = skillLvlDisplayStr.split(" ");
        return Integer.parseInt(split[0]);
    }
}
