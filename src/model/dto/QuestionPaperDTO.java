package model.dto;

/**
 * This class is used to transform QuestionPaper object attributes in order to use in TableViews.
 *
 * @author Sam Barba
 */
public class QuestionPaperDTO {

    private int id;

    private String title;

    private String subjectTitle;

    private String course;

    private String dateCreated;

    public QuestionPaperDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubjectTitle() {
        return subjectTitle;
    }

    public void setSubjectTitle(String subjectTitle) {
        this.subjectTitle = subjectTitle;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}
