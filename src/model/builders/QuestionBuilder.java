package model.builders;

import java.util.List;

import model.persisted.Question;

import view.enums.DifficultyLevel;

/**
 * This class utilises the builder pattern, and is used to build persisted Question objects.
 *
 * @author Sam Barba
 */
public class QuestionBuilder {

	private int id;

	private int subjectId;

	private String statement;

	private List<String> answerOptions;

	private String correctAnswerOption;

	private DifficultyLevel difficultyLevel;

	private int marks;

	private int timeRequiredMins;

	public QuestionBuilder() {
	}

	public QuestionBuilder withId(int id) {
		this.id = id;
		return this;
	}

	public QuestionBuilder withSubjectId(int subjectId) {
		this.subjectId = subjectId;
		return this;
	}

	public QuestionBuilder withStatement(String statement) {
		this.statement = statement;
		return this;
	}

	public QuestionBuilder withAnswerOptions(List<String> answerOptions) {
		this.answerOptions = answerOptions;
		return this;
	}

	public QuestionBuilder withCorrectAnswerOptions(String correctAnswerOption) {
		this.correctAnswerOption = correctAnswerOption;
		return this;
	}

	public QuestionBuilder withDifficultyLevel(DifficultyLevel difficultyLevel) {
		this.difficultyLevel = difficultyLevel;
		return this;
	}

	public QuestionBuilder withMarks(int marks) {
		this.marks = marks;
		return this;
	}

	public QuestionBuilder withTimeRequiredMins(int timeRequiredMins) {
		this.timeRequiredMins = timeRequiredMins;
		return this;
	}

	public Question build() {
		return new Question(id, subjectId, statement, answerOptions, correctAnswerOption, difficultyLevel, marks,
			timeRequiredMins);
	}
}
