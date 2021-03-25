package model.builders;

import java.time.LocalDateTime;
import java.util.List;

import model.persisted.QuestionPaper;

import view.enums.SkillLevel;

/**
 * This class utilises the builder pattern, and is used to build persisted Question Paper objects.
 *
 * @author Sam Barba
 */
public class QuestionPaperBuilder {

	private int id;

	private int subjectId;

	private String title;

	private String courseTitle;

	private String courseCode;

	private List<Integer> questionIds;

	private SkillLevel skillLevel;

	private int marks;

	private int timeRequiredMins;

	private LocalDateTime dateCreated;

	public QuestionPaperBuilder() {
	}

	public QuestionPaperBuilder withId(int id) {
		this.id = id;
		return this;
	}

	public QuestionPaperBuilder withSubjectId(int subjectId) {
		this.subjectId = subjectId;
		return this;
	}

	public QuestionPaperBuilder withTitle(String title) {
		this.title = title;
		return this;
	}

	public QuestionPaperBuilder withCourseTitle(String courseTitle) {
		this.courseTitle = courseTitle;
		return this;
	}

	public QuestionPaperBuilder withCourseCode(String courseCode) {
		this.courseCode = courseCode;
		return this;
	}

	public QuestionPaperBuilder withQuestionIds(List<Integer> questionIds) {
		this.questionIds = questionIds;
		return this;
	}

	public QuestionPaperBuilder withSkillLevel(SkillLevel skillLevel) {
		this.skillLevel = skillLevel;
		return this;
	}

	public QuestionPaperBuilder withMarks(int marks) {
		this.marks = marks;
		return this;
	}

	public QuestionPaperBuilder withTimeRequiredMins(int timeRequiredMins) {
		this.timeRequiredMins = timeRequiredMins;
		return this;
	}

	public QuestionPaperBuilder withDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public QuestionPaper build() {
		return new QuestionPaper(id, subjectId, title, courseTitle, courseCode, questionIds, skillLevel, marks,
			timeRequiredMins, dateCreated);
	}
}
