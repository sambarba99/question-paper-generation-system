package model.persisted;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import view.enums.BloomSkillLevel;

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

	private BloomSkillLevel skillLevel;

	private int marks;

	private int minutesRequired;

	private LocalDateTime dateCreated;

	public Question() {
		answers = new ArrayList<>();
	}

	public Question(int id, int subjectId, String statement, List<Answer> answers, BloomSkillLevel skillLevel,
		int marks, int minutesRequired, LocalDateTime dateCreated) {

		this.id = id;
		this.subjectId = subjectId;
		this.statement = statement;
		this.answers = answers;
		this.skillLevel = skillLevel;
		this.marks = marks;
		this.minutesRequired = minutesRequired;
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

	public BloomSkillLevel getSkillLevel() {
		return skillLevel;
	}

	public void setSkillLevel(BloomSkillLevel skillLevel) {
		this.skillLevel = skillLevel;
	}

	public int getMarks() {
		return marks;
	}

	public void setMarks(int marks) {
		this.marks = marks;
	}

	public int getMinutesRequired() {
		return minutesRequired;
	}

	public void setMinutesRequired(int minutesRequired) {
		this.minutesRequired = minutesRequired;
	}

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}
}
