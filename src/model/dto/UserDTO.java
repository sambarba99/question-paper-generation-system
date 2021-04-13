package model.dto;

/**
 * This class is used to transform User object attributes in order to use in TableViews.
 *
 * @author Sam Barba
 */
public class UserDTO {

    private String username;

    private String privilege;

    private String dateCreated;

    public UserDTO() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}
