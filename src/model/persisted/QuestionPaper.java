package model.persisted;

import java.util.List;

import view.enums.DifficultyLevel;

/**
 * Represents a question paper (the GA uses the subject ID, difficulty level, marks, and time required to calculate
 * paper fitness).
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

	private DifficultyLevel difficultyLevel;

	private int marks;

	private int timeRequiredMins;

	public QuestionPaper(int id, int subjectId, String title, String courseTitle, String courseCode,
		List<Integer> questionIds, DifficultyLevel difficultyLevel, int marks, int timeRequiredMins) {
		this.id = id;
		this.subjectId = subjectId;
		this.title = title;
		this.courseTitle = courseTitle;
		this.courseCode = courseCode;
		this.questionIds = questionIds;
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

	public void addQuestionId(int questionId) {
		this.questionIds.add(questionId);
	}

	public void removeQuestionId(int questionId) {
		this.questionIds.remove(questionIds.indexOf(questionId));
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
