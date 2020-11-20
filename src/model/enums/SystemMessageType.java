package model.enums;

public enum SystemMessageType {

	ERROR("Error Message"),
	NEUTRAL("Message"),
	SUCCESS("Success Message");

	private String strVal;

	SystemMessageType(String strVal) {
		this.strVal = strVal;
	}

	public String getStrVal() {
		return strVal;
	}
}
