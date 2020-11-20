package model.enums;

public enum TutorAction {

	ADD_SUBJECT("Add new subject"),
	DELETE_SUBJECT("Delete subject"),
	TOGGLE_FILTER_PAPERS("Toggle filter papers by subject(s)"),
	VIEW_MODIFY_ALL_QUESTIONS("View/modify all questions"),
	GENERATE_QUESTION_PAPER("Generate question paper"),
	VIEW_QUESTION_PAPER("View/export question paper"),
	DELETE_QUESTION_PAPER("Delete question paper"),
	CHANGE_PASSWORD("Change password"),
	NONE("");

	private String strVal;

	TutorAction(String strVal) {
		this.strVal = strVal;
	}

	public String getStrVal() {
		return strVal;
	}
}
