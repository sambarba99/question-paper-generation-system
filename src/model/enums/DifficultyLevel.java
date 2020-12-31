package model.enums;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import interfacecontroller.SystemNotification;

import utils.Constants;

/**
 * Represents question difficulty.
 *
 * @author Sam Barba
 */
public enum DifficultyLevel {

	EASY(1),
	MEDIUM(2),
	DIFFICULT(3);

	private int intLevelVal;

	DifficultyLevel(int intLevelVal) {
		this.intLevelVal = intLevelVal;
	}

	public int getIntLevelVal() {
		return intLevelVal;
	}

	public static DifficultyLevel getFromInt(int intLevelVal) {
		List<DifficultyLevel> allDifficulties = new ArrayList<>(EnumSet.allOf(DifficultyLevel.class));
		DifficultyLevel difficulty = allDifficulties.stream().filter(diff -> diff.getIntLevelVal() == intLevelVal)
				.findFirst().orElse(null);
		if (difficulty != null) {
			return difficulty;
		}
		SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + "Invalid Difficulty Level passed: " + intLevelVal);
		throw new IllegalArgumentException("Invalid Difficulty Level passed: " + intLevelVal);
	}
}
