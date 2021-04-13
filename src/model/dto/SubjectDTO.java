package model.dto;

/**
 * This class is used to transform Subject object attributes in order to use in TableViews.
 *
 * @author Sam Barba
 */
public class SubjectDTO {

    private int id;

    private String title;

    private int numQuestions;

    private String dateCreated;

    public SubjectDTO() {
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

    public int getNumQuestions() {
        return numQuestions;
    }

    public void setNumQuestions(int numQuestions) {
        this.numQuestions = numQuestions;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}
