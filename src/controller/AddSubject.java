package controller;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.dto.SubjectDTO;
import model.persisted.Subject;
import model.service.SubjectService;

import view.BoxMaker;
import view.ButtonMaker;
import view.Constants;
import view.enums.BoxType;
import view.enums.SystemNotificationType;
import view.enums.UserAction;

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
		Button btnAdd = ButtonMaker.getInstance().makeButton(100, Constants.BTN_HEIGHT, UserAction.ADD, action -> {
			String subjectTitle = SubjectDTO.getInstance().formatTitle(txtTitle.getText());
			addSubject(subjectTitle);
		});

		HBox hboxTitle = (HBox) BoxMaker.getInstance().makeBox(BoxType.HBOX, Pos.CENTER, 5, lblEnterTitle, txtTitle);

		FlowPane pane = new FlowPane();
		pane.getStyleClass().add("flow-pane");
		pane.getChildren().addAll(hboxTitle, btnAdd);

		Scene scene = new Scene(pane, 450, 150);
		scene.getStylesheets().add("style.css");
		stage.setScene(scene);
		stage.setTitle("Add New Subject");
		stage.setResizable(false);
		// so multiple instances of this window can't be opened
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
		return added;
	}

	/**
	 * Add a new subject.
	 * 
	 * @param subjectTitle - the title of the subject
	 */
	private static void addSubject(String subjectTitle) {
		if (subjectTitle.length() == 0) {
			SystemNotification.display(SystemNotificationType.ERROR, "Please enter the subject title.");
		} else {
			if (subjectTitle.matches(Constants.TITLE_REGEX)) {
				int subjectId = SubjectService.getInstance().getHighestSubjectId() + 1;
				Subject subject = new Subject(subjectId, subjectTitle);
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
