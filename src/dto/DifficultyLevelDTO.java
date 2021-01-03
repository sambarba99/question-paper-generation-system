package dto;

import javafx.scene.control.ChoiceBox;

import model.enums.DifficultyLevel;

/**
 * This class is a singleton which contains methods relating to difficulty level choice boxes.
 *
 * @author Sam Barba
 */
public class DifficultyLevelDTO {

	private static DifficultyLevelDTO instance;

	/**
	 * Get enum value of selected difficulty in a ChoiceBox. The index of the selected item corresponds to the
	 * difficulty level's int value - 1.
	 * 
	 * @param cbDifficulty - the ChoiceBox of difficulty levels
	 * @return enum of selected user action
	 */
	public DifficultyLevel getSelectedDifficulty(ChoiceBox cbDifficulty) {
		int difficultyLevelSelected = cbDifficulty.getSelectionModel().getSelectedIndex() + 1;
		return DifficultyLevel.getFromInt(difficultyLevelSelected);
	}

	public synchronized static DifficultyLevelDTO getInstance() {
		if (instance == null) {
			instance = new DifficultyLevelDTO();
		}
		return instance;
	}

	private DifficultyLevelDTO() {
	}
}
