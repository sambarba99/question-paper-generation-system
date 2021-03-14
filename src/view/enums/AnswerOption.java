package view.enums;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import view.Constants;

import controller.SystemNotification;

/**
 * Represents an answer letter option.
 *
 * @author Sam Barba
 */
public enum AnswerOption {

	A("a)"),
	B("b)"),
	C("c)"),
	D("d)");

	private String displayStr;

	AnswerOption(String displayStr) {
		this.displayStr = displayStr;
	}

	public String getDisplayStr() {
		return displayStr;
	}

	/**
	 * Retrieve AnswerOption given a String value (A,B,C,D). Throw an IllegalArgumentException if the value doesn't
	 * exist.
	 * 
	 * @param strVal - the String value of the AnswerOption
	 * @return the AnswerOption with the specified String value
	 */
	public static AnswerOption getFromStr(String strVal) {
		List<AnswerOption> allOptions = new ArrayList<>(EnumSet.allOf(AnswerOption.class));
		AnswerOption answerOption = allOptions.stream()
			.filter(option -> option.toString().equals(strVal))
			.findFirst()
			.orElse(null);
		if (answerOption != null) {
			return answerOption;
		}
		SystemNotification.display(SystemNotificationType.ERROR,
			Constants.UNEXPECTED_ERROR + "Invalid Answer Option passed: " + strVal);
		throw new IllegalArgumentException("Invalid Answer Option passed: " + strVal);
	}
}
