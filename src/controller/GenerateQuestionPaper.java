package controller;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.dto.DifficultyLevelDTO;
import model.dto.SubjectDTO;
import model.persisted.QuestionPaper;
import model.persisted.Subject;
import model.service.QuestionPaperService;
import model.service.SubjectService;
import model.service.questionpapergeneration.QuestionPaperGenerator;

import view.BoxMaker;
import view.Constants;
import view.enums.BoxType;
import view.enums.DifficultyLevel;
import view.enums.SystemNotificationType;

/**
 * Allows the user to generate a question paper with specified parameters such as subject and difficulty.
 *
 * @author Sam Barba
 */
public class GenerateQuestionPaper {

	private static Stage stage;

	private static boolean generated;

	/*
	 * Nodes for specifying question paper parameters
	 */
	private static ChoiceBox cbSubject = new ChoiceBox();

	private static TextField txtTitle = new TextField();

	private static TextField txtCourseTitle = new TextField();

	private static TextField txtCourseCode = new TextField();

	private static ChoiceBox cbDifficulty = new ChoiceBox();

	private static TextField txtMarks = new TextField();

	private static TextField txtTimeRequired = new TextField();

	/**
	 * Return whether a paper has been generated successfully or not.
	 * 
	 * @return whether or not the paper has been generated successfully
	 */
	public static boolean generatePaper() {
		stage = new Stage();
		generated = false;

		Label lblSelectSubject = new Label("Select the subject:");
		Label lblEnterTitle = new Label("Enter the title:");
		Label lblEnterCourseTitle = new Label("Enter the course title:");
		Label lblEnterCourseCode = new Label("Enter the course code:");
		Label lblSelectDifficulty = new Label("Select difficulty level:");
		Label lblEnterMarks = new Label("Enter no. marks:");
		Label lblEnterTimeReq = new Label("Enter time required (mins):");
		Button btnGenerate = new Button("Generate");

		btnGenerate.setOnAction(action -> {
			QuestionPaper generatedPaper = generatePaperWithParams();
			if (generatedPaper != null) {
				QuestionPaperService.getInstance().addQuestionPaper(generatedPaper);
				generated = true;
				stage.close();
				SystemNotification.display(SystemNotificationType.SUCCESS,
					"Paper generated! Return to Academic Material to view/export.");
			}
		});

		BoxMaker boxMaker = BoxMaker.getInstance();
		VBox vbox1 = (VBox) boxMaker.makeBox(BoxType.VBOX, Pos.TOP_LEFT, 5, lblSelectSubject, cbSubject, lblEnterTitle,
			txtTitle, lblEnterCourseTitle, txtCourseTitle, lblEnterCourseCode, txtCourseCode);
		VBox vbox2 = (VBox) boxMaker.makeBox(BoxType.VBOX, Pos.TOP_LEFT, 5, lblSelectDifficulty, cbDifficulty,
			lblEnterMarks, txtMarks, lblEnterTimeReq, txtTimeRequired);
		HBox hbox = (HBox) boxMaker.makeBox(BoxType.HBOX, Pos.TOP_CENTER, 20, vbox1, vbox2);
		VBox vboxMain = (VBox) boxMaker.makeBox(BoxType.VBOX, Pos.CENTER, 20, hbox, btnGenerate);

		FlowPane pane = new FlowPane();
		pane.getStyleClass().add("flow-pane");
		pane.getChildren().add(vboxMain);

		setup();

		Scene scene = new Scene(pane, 550, 400);
		scene.getStylesheets().add("style.css");
		stage.setScene(scene);
		stage.setTitle("Generate Question Paper");
		stage.setResizable(false);
		// so multiple instances of this window can't be opened
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
		return generated;
	}

	/**
	 * Generate a paper and add it via QuestionPaperService.
	 * 
	 * @return whether or not paper has been generated successfully
	 */
	private static QuestionPaper generatePaperWithParams() {
		String title = txtTitle.getText().trim();
		String courseTitle = txtCourseTitle.getText().trim();
		String courseCode = txtCourseCode.getText().trim();
		if (title.length() == 0 || courseTitle.length() == 0 || courseCode.length() == 0) {
			SystemNotification.display(SystemNotificationType.ERROR,
				"Please enter the title, course title and course code.");
			return null;
		} else if (!title.matches(Constants.TITLE_REGEX) || !courseTitle.matches(Constants.TITLE_REGEX)
			|| !courseCode.matches(Constants.TITLE_REGEX)) {
			SystemNotification.display(SystemNotificationType.ERROR,
				"Titles and codes must be only alphanumeric, and no repeating spaces.");
			return null;
		}

		int marks = 0;
		try {
			marks = Integer.parseInt(txtMarks.getText());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			SystemNotification.display(SystemNotificationType.ERROR, "Invalid number of marks.");
			return null;
		}
		int timeReq = 0;
		try {
			timeReq = Integer.parseInt(txtTimeRequired.getText());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			SystemNotification.display(SystemNotificationType.ERROR, "Invalid time required.");
			return null;
		}

		int subjectId = SubjectDTO.getInstance().getSubjectId(cbSubject);
		DifficultyLevel difficultyLevel = DifficultyLevelDTO.getInstance().getSelectedDifficulty(cbDifficulty);

		QuestionPaper generatedPaper = QuestionPaperGenerator.getInstance().generatePaper(subjectId, title, courseTitle,
			courseCode, difficultyLevel, marks, timeReq);

		return generatedPaper;
	}

	/**
	 * Set up choice boxes.
	 */
	private static void setup() {
		txtTitle.setText("");
		txtCourseTitle.setText("");
		txtCourseCode.setText("");
		txtMarks.setText("");
		txtTimeRequired.setText("");

		List<Subject> allSubjects = SubjectService.getInstance().getAllSubjects();
		cbSubject.getItems().clear();
		cbSubject.getItems().addAll(allSubjects.stream()
			.map(subject -> (subject.getTitle() + " (ID " + subject.getId() + ")")).collect(Collectors.toList()));
		cbSubject.getSelectionModel().select(0);
		cbSubject.setPrefWidth(200);

		List<DifficultyLevel> allDifficulties = new ArrayList<>(EnumSet.allOf(DifficultyLevel.class));
		cbDifficulty.getItems().clear();
		cbDifficulty.getItems()
			.addAll(allDifficulties.stream().map(DifficultyLevel::getStrVal).collect(Collectors.toList()));
		cbDifficulty.getSelectionModel().select(0);
		cbDifficulty.setPrefWidth(200);
	}
}