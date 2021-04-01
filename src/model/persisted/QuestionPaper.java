package model.persisted;

import java.time.LocalDateTime;
import java.util.List;

import view.enums.BloomSkillLevel;

/**
 * Represents a question paper.
 *
 * @author Sam Barba
 */
public class QuestionPaper {

	private int id;

	private int subjectId;

	private String title;

	private String courseTitle;

	private String courseCode;

	private List<Integer> questionIds;

	private BloomSkillLevel skillLevel;

	private int marks;

	private int minutesRequired;

	private LocalDateTime dateCreated;

	public QuestionPaper(int id, int subjectId, String title, String courseTitle, String courseCode,
		List<Integer> questionIds, BloomSkillLevel skillLevel, int marks, int minutesRequired,
		LocalDateTime dateCreated) {

		this.id = id;
		this.subjectId = subjectId;
		this.title = title;
		this.courseTitle = courseTitle;
		this.courseCode = courseCode;
		this.questionIds = questionIds;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCourseTitle() {
		return courseTitle;
	}

	public void setCourseTitle(String courseTitle) {
		this.courseTitle = courseTitle;
	}

	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

	public List<Integer> getQuestionIds() {
		return questionIds;
	}

	public void setQuestionIds(List<Integer> questionIds) {
		this.questionIds = questionIds;
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

	@Override
	public String toString() {
		return title + " (ID " + id + ")";
	}
}
