package model.persisted;

/**
 * Represents an answer for a Question.
 *
 * @author Sam Barba
 */
public class Answer {

	private String value;

	private boolean correct;

	public Answer(String value, boolean correct) {
		this.value = value;
		this.correct = correct;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String answerValue) {
		this.value = answerValue;
	}

	public boolean isCorrect() {
		return correct;
	}

	public void setCorrect(boolean correct) {
		this.correct = correct;
	}
}
