package dto;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javafx.scene.control.ChoiceBox;

import model.enums.DifficultyLevel;

public class DifficultyLevelDTO {

	public DifficultyLevelDTO() {
	}

	/**
	 * Get enum value of selected difficulty in ChoiceBox
	 * 
	 * @param cbDifficulty - the ChoiceBox of difficulty levels
	 * @return enum of selected tutor action
	 */
	public DifficultyLevel getSelectedDifficulty(ChoiceBox cbDifficulty) {
		String difficultySelected = cbDifficulty.getSelectionModel().getSelectedItem().toString();
		List<DifficultyLevel> allDifficultyLevels = new ArrayList<>(EnumSet.allOf(DifficultyLevel.class));
		return allDifficultyLevels.stream().filter(d -> d.toString().equals(difficultySelected)).findFirst()
				.orElse(null);
	}
}
