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
    CHOOSE_EXPORT_DESTINATION("Choose export destination"),
    CONFIRM_NO("No"),
    CONFIRM_YES("Yes"),
    DELETE_QUESTION("Delete question(s)"),
    DELETE_QUESTION_PAPER("Delete question paper(s)"),
    DELETE_SUBJECT("Delete subject(s)"),
    DELETE_USER("Delete user"),
    EXIT_APPLICATION("Exit"),
    EXPORT("Export!"),
    GENERATE("Generate!"),
    GENERATE_QUESTION_PAPER("Generate question paper"),
    LOG_IN("Log In"),
    OPEN_ACADEMC_MATERIAL("Open Academic Material"),
    OPEN_QUESTION_MANAGEMENT("Open Question Management"),
    TOGGLE_FILTER_PAPERS("Filter papers by subject(s)"),
    UPDATE_PASSWORD("Update password"),
    VIEW_QUESTION_PAPER("View/export question paper");

    private String strVal;

    UserAction(String strVal) {
        this.strVal = strVal;
    }

    public String getStrVal() {
        return strVal;
    }
}
