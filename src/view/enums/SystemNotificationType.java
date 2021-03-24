package view.enums;

/**
 * Represents the type of system notification.
 *
 * @author Sam Barba
 */
public enum SystemNotificationType {

	ERROR("Error"),
	NEUTRAL("Notification"),
	SUCCESS("Success!"),
	CONFIRM_DELETION("Delete?"),
	CONFIRM_EXIT_APPLICATION("Exit application?");

	private String strVal;

	SystemNotificationType(String strVal) {
		this.strVal = strVal;
	}

	public String getStrVal() {
		return strVal;
	}
}
