package view.enums;

/**
 * Represents the actions that a user can perform.
 *
 * @author Sam Barba
 */
public enum UserAction {

	ADD("Add"),
	ADD_NEW_QUESTION("Add new question"),
	ADD_NEW_SUBJECT("Add new subject"),
	ADD_NEW_USER("Add new user"),
	DELETE_CONFIRM_NO("No"),
	DELETE_CONFIRM_YES("Yes"),
	DELETE_QUESTION("Delete question"),
	DELETE_QUESTION_PAPER("Delete question paper"),
	DELETE_SUBJECT("Delete subject"),
	DELETE_USER("Delete user"),
	EXPORT("Export to .txt"),
	GENERATE("Generate"),
	GENERATE_QUESTION_PAPER("Generate question paper"),
	GO_ACADEMC_MATERIAL("Go to Academic Material"),
	LOG_IN("Log In"),
	TOGGLE_FILTER_PAPERS("Filter papers by subject(s)"),
	UPDATE_PASSWORD("Update password"),
	VIEW_MODIFY_ALL_QUESTIONS("View/modify all questions"),
	VIEW_QUESTION_PAPER("View/export question paper");

	private String strVal;

	UserAction(String strVal) {
		this.strVal = strVal;
	}

	public String getStrVal() {
		return strVal;
	}
}
