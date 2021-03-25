package model.dto;

/**
 * This class is used to transform Question object attributes in order to use in TableViews.
 *
 * @author Sam Barba
 */
public class QuestionDTO {

	private int id;

	private String subjectTitle;

	private String statement;

	private String skillLevel;

	private int marks;

	private int timeRequiredMins;

	private String dateCreated;

	public QuestionDTO() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSubjectTitle() {
		return subjectTitle;
	}

	public void setSubjectTitle(String subjectTitle) {
		this.subjectTitle = subjectTitle;
	}

	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}

	public String getSkillLevel() {
		return skillLevel;
	}

	public void setSkillLevel(String skillLevel) {
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

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}
}
