package model.builders;

import model.persisted.Answer;

/**
 * This class utilises the builder pattern, and is used to build Answer objects for Questions.
 *
 * @author Sam Barba
 */
public class AnswerBuilder {

	private String value;

	private String letter; // A,B,C,D

	private boolean correct;

	public AnswerBuilder() {
	}

	public AnswerBuilder withValue(String value) {
		this.value = value;
		return this;
	}

	public AnswerBuilder withLetter(String letter) {
		this.letter = letter;
		return this;
	}

	public AnswerBuilder withIsCorrect(boolean correct) {
		this.correct = correct;
		return this;
	}

	public Answer build() {
		return new Answer(value, letter, correct);
	}
}
