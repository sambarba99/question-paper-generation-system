package view.enums;

/**
 * Represents the level of system notification.
 *
 * @author Sam Barba
 */
public enum SystemNotificationType {

	ERROR("Error Notification"),
	NEUTRAL("Notification"),
	SUCCESS("Success Notification");

	private String strVal;

	SystemNotificationType(String strVal) {
		this.strVal = strVal;
	}

	public String getStrVal() {
		return strVal;
	}
}
