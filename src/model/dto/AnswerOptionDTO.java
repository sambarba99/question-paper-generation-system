package model.dto;

import javafx.scene.control.ChoiceBox;

import view.enums.AnswerOption;

/**
 * This class is a singleton which contains methods relating to Answer Option choice boxes.
 *
 * @author Sam Barba
 */
public class AnswerOptionDTO {

	private static AnswerOptionDTO instance;

	/**
	 * Get enum value of selected answer option (A/B/C/D) in a ChoiceBox.
	 * 
	 * @param cbAnswerOptions - the ChoiceBox of answer options
	 * @return enum corresponding to selected answer option
	 */
	public AnswerOption getSelectedAnswerOption(ChoiceBox cbAnswerOptions) {
		String answerSelected = cbAnswerOptions.getSelectionModel().getSelectedItem().toString();
		return AnswerOption.getFromStr(answerSelected);
	}

	public synchronized static AnswerOptionDTO getInstance() {
		if (instance == null) {
			instance = new AnswerOptionDTO();
		}
		return instance;
	}

	private AnswerOptionDTO() {
	}
}
