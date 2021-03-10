package model.persisted;

import java.util.List;

import view.enums.DifficultyLevel;

/**
 * Represents a question.
 * 
 * @author Sam Barba
 */
public class Question {

	private int id;

	private int subjectId;

	private String statement;

	private List<String> answerOptions;

	private String correctAnswerOption;

	private DifficultyLevel difficultyLevel;

	private int marks;

	private int timeRequiredMins;

	public Question(int id, int subjectId, String statement, List<String> answerOptions, String correctAnswerOption,
		DifficultyLevel difficultyLevel, int marks, int timeRequiredMins) {
		this.id = id;
		this.subjectId = subjectId;
		this.statement = statement;
		this.answerOptions = answerOptions;
		this.correctAnswerOption = correctAnswerOption;
		this.difficultyLevel = difficultyLevel;
		this.marks = marks;
		this.timeRequiredMins = timeRequiredMins;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(int subjectId) {
		this.subjectId = subjectId;
	}

	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}

	public List<String> getAnswerOptions() {
		return answerOptions;
	}

	public void setAnswerOptions(List<String> answerOptions) {
		this.answerOptions = answerOptions;
	}

	public String getCorrectAnswerOption() {
		return correctAnswerOption;
	}

	public void setCorrectAnswerOption(String correctAnswerOption) {
		this.correctAnswerOption = correctAnswerOption;
	}

	public DifficultyLevel getDifficultyLevel() {
		return difficultyLevel;
	}

	public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
		this.difficultyLevel = difficultyLevel;
	}

	public int getMarks() {
		return marks;
	}

	public void setMarks(int marks) {
		this.marks = marks;
	}

	public int getTimeRequiredMins() {
		return timeRequiredMins;
	}

	public void setTimeRequiredMins(int timeRequiredMins) {
		this.timeRequiredMins = timeRequiredMins;
	}
}
