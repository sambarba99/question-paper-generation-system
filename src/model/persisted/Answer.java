package model.persisted;

/**
 * Represents an answer for a Question.
 *
 * @author Sam Barba
 */
public class Answer {

	private String value;

	private String letter; // A,B,C,D

	private boolean correct;

	public Answer(String value, String letter, boolean correct) {
		this.value = value;
		this.letter = letter;
		this.correct = correct;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String answerValue) {
		this.value = answerValue;
	}

	public String getLetter() {
		return letter;
	}

	public void setLetter(String letter) {
		this.letter = letter;
	}

	public boolean isCorrect() {
		return correct;
	}

	public void setCorrect(boolean correct) {
		this.correct = correct;
	}
}
