package model.persisted;

import java.time.LocalDateTime;
import java.util.List;

import view.enums.SkillLevel;
import view.utils.Constants;

/**
 * Represents a question.
 * 
 * @author Sam Barba
 */
public class Question {

	private int id;

	private int subjectId;

	private String statement;

	private List<Answer> answers;

	private SkillLevel skillLevel;

	private int marks;

	private int timeRequiredMins;

	private LocalDateTime dateCreated;

	public Question(int id, int subjectId, String statement, List<Answer> answers, SkillLevel skillLevel, int marks,
		int timeRequiredMins, LocalDateTime dateCreated) {

		this.id = id;
		this.subjectId = subjectId;
		this.statement = statement;
		this.answers = answers;
		this.skillLevel = skillLevel;
		this.marks = marks;
		this.timeRequiredMins = timeRequiredMins;
		this.dateCreated = dateCreated;
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

	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}

	public SkillLevel getSkillLevel() {
		return skillLevel;
	}

	public void setSkillLevel(SkillLevel skillLevel) {
		this.skillLevel = skillLevel;
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

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Override
	public String toString() {
		StringBuilder answersBld = new StringBuilder();
		for (Answer answer : answers) {
			answersBld.append("'" + answer.getValue() + "'");
			if (answer.isCorrect()) {
				answersBld.append(" <- CORRECT");
			}
			answersBld.append(Constants.NEWLINE);
		}

		return "QUESTION: id=" + id + ", subjectId=" + subjectId + "," + Constants.NEWLINE + "statement='" + statement
			+ "'," + Constants.NEWLINE + "answers=" + Constants.NEWLINE + answersBld.toString() + "skillLevel="
			+ skillLevel.getStrVal() + ", marks=" + marks + ", timeRequiredMins=" + timeRequiredMins + Constants.NEWLINE
			+ "dateCreated=" + Constants.DATE_FORMATTER.format(dateCreated);
	}
}
