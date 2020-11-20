package interfaceviews;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import dao.SubjectDAO;

import dto.SubjectDTO;

import model.Subject;
import model.enums.BoxType;
import model.enums.SystemMessageType;

import utils.BoxMaker;
import utils.Constants;

public class AddSubjectView {

	private static SubjectDAO subjectDao = new SubjectDAO();

	private static SubjectDTO subjectDto = new SubjectDTO();

	private static boolean added;

	public static boolean addSubject() {
		added = false;
		Stage stage = new Stage();

		Label lblEnterTitle = new Label("Enter the subject title:");
		TextField txtTitle = new TextField();
		Button btnAdd = new Button("Add subject");

		btnAdd.setOnAction(action -> {
			if (txtTitle.getText().trim().length() == 0) {
				SystemMessageView.display(SystemMessageType.ERROR, "Please enter the subject title.");
			} else {
				String title = txtTitle.getText();
				if (title.matches(Constants.TITLE_REGEX)) {
					title = subjectDto.formatTitle(title);
					int subjectId = subjectDao.getHighestSubjectId() + 1;
					Subject subject = new Subject(subjectId, title);
					subjectDao.addSubject(subject);
					added = true;
					stage.close();
				} else {
					SystemMessageView.display(SystemMessageType.ERROR,
							"Title must be only alphanumeric, and no repeating spaces.");
				}
			}
		});

		HBox hboxTitle = (HBox) BoxMaker.makeBox(BoxType.HBOX, Pos.CENTER, 5, lblEnterTitle, txtTitle);

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
}