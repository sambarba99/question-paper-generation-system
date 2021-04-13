package controller;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.builders.SubjectBuilder;
import model.persisted.Subject;
import model.service.SubjectService;

import view.SystemNotification;
import view.builders.ButtonBuilder;
import view.builders.PaneBuilder;
import view.enums.BoxType;
import view.enums.SystemNotificationType;
import view.enums.UserAction;
import view.utils.Constants;
import view.utils.StringFormatter;

/**
 * Allows user to add a new subject.
 *
 * @author Sam Barba
 */
public class AddSubject {

    private static Stage stage;

    private static boolean added;

    /**
     * Add a new subject.
     * 
     * @return whether or not a subject has been added successfully
     */
    public static boolean addSubject() {
        stage = new Stage();
        added = false;

        Label lblEnterTitle = new Label("Enter the subject title:");
        TextField txtTitle = new TextField();
        Button btnAdd = new ButtonBuilder()
            .withWidth(100)
            .withUserAction(UserAction.ADD)
            .withActionEvent(e -> {
                formatTitleAndAddSubject(txtTitle.getText());
            })
            .build();

        HBox hboxTitle = (HBox) new PaneBuilder()
            .withBoxType(BoxType.HBOX)
            .withAlignment(Pos.CENTER)
            .withSpacing(5)
            .withNodes(lblEnterTitle, txtTitle)
            .build();
        VBox vboxMain = (VBox) new PaneBuilder()
            .withBoxType(BoxType.VBOX)
            .withAlignment(Pos.CENTER)
            .withSpacing(20)
            .withNodes(hboxTitle, btnAdd)
            .build();

        Scene scene = new Scene(vboxMain, 450, 150);
        scene.getStylesheets().add(Constants.CSS_STYLE_PATH);
        stage.setScene(scene);
        stage.setTitle("Add New Subject");
        stage.setResizable(false);
        // so multiple instances of this window can't be opened
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        return added;
    }

    /**
     * Verify user-entered title and add the subject.
     * 
     * @param subjectTitle - the title of the subject
     */
    private static void formatTitleAndAddSubject(String subjectTitle) {
        subjectTitle = StringFormatter.formatTitle(subjectTitle);

        if (subjectTitle.length() == 0) {
            SystemNotification.display(SystemNotificationType.ERROR, "Please enter the subject title.");
        } else {
            if (subjectTitle.matches(Constants.TITLE_REGEX)) {
                int id = SubjectService.getInstance().getNewSubjectId();
                Subject subject = new SubjectBuilder().withId(id).withTitle(subjectTitle).build();
                SubjectService.getInstance().addSubject(subject);
                added = true;
                stage.close();
            } else {
                SystemNotification.display(SystemNotificationType.ERROR,
                    "Title must be only alphanumeric, and no repeating spaces.");
            }
        }
    }
}
