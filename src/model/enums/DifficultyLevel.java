package model.enums;

/**
 * Difficulty of a question
 *
 * @author Sam Barba
 *
 */
public enum DifficultyLevel {

	EASY(1),
	MEDIUM(2),
	DIFFICULT(3);

	private int numVal;

	DifficultyLevel(int numVal) {
		this.numVal = numVal;
	}

	public int getNumVal() {
		return numVal;
	}

}
