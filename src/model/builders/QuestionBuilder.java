package model.builders;

import java.time.LocalDateTime;
import java.util.List;

import model.persisted.Answer;
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

	private List<Answer> answers;

	private DifficultyLevel difficultyLevel;

	private int marks;

	private int timeRequiredMins;

	private LocalDateTime dateCreated;

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

	public QuestionBuilder withAnswers(List<Answer> answers) {
		this.answers = answers;
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

	public QuestionBuilder withDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public Question build() {
		return new Question(id, subjectId, statement, answers, difficultyLevel, marks, timeRequiredMins, dateCreated);
	}
}
