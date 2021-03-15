package controller;

import java.io.IOException;
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
import model.questionpapergeneration.QuestionPaperGenerator;
import model.service.QuestionPaperService;
import model.service.SubjectService;

import view.Constants;
import view.builders.ButtonBuilder;
import view.builders.PaneBuilder;
import view.enums.BoxType;
import view.enums.DifficultyLevel;
import view.enums.SystemNotificationType;
import view.enums.UserAction;

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

		Button btnGenerate = new ButtonBuilder().withWidth(100)
			.withUserAction(UserAction.GENERATE)
			.withClickAction(action -> {
				QuestionPaper generatedPaper = null;
				try {
					generatedPaper = generatePaperWithParams();
				} catch (IOException e) {
					e.printStackTrace();
					SystemNotification.display(SystemNotificationType.ERROR,
						Constants.UNEXPECTED_ERROR + e.getClass().getName());
				}
				if (generatedPaper != null) {
					QuestionPaperService.getInstance().addQuestionPaper(generatedPaper);
					generated = true;
					stage.close();
				}
			})
			.build();

		VBox vbox1 = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.TOP_LEFT)
			.withSpacing(5)
			.withNodes(lblSelectSubject, cbSubject, lblEnterTitle, txtTitle, lblEnterCourseTitle, txtCourseTitle,
				lblEnterCourseCode, txtCourseCode)
			.build();
		VBox vbox2 = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.TOP_LEFT)
			.withSpacing(5)
			.withNodes(lblSelectDifficulty, cbDifficulty, lblEnterMarks, txtMarks, lblEnterTimeReq, txtTimeRequired)
			.build();
		HBox hbox = (HBox) new PaneBuilder().withBoxType(BoxType.HBOX)
			.withAlignment(Pos.TOP_CENTER)
			.withSpacing(20)
			.withNodes(vbox1, vbox2)
			.build();
		VBox vboxMain = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(20)
			.withNodes(hbox, btnGenerate)
			.build();

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
	private static QuestionPaper generatePaperWithParams() throws IOException {
		String title = SubjectDTO.getInstance().formatTitle(txtTitle.getText());
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

		QuestionPaper generatedPaper = QuestionPaperGenerator.getInstance()
			.generatePaper(subjectId, title, courseTitle, courseCode, difficultyLevel, marks, timeReq);

		return generatedPaper;
	}

	/**
	 * Set up choice boxes.
	 */
	private static void setup() {
		txtTitle.setText(Constants.EMPTY);
		txtCourseTitle.setText(Constants.EMPTY);
		txtCourseCode.setText(Constants.EMPTY);
		txtMarks.setText(Constants.EMPTY);
		txtTimeRequired.setText(Constants.EMPTY);

		List<Subject> allSubjects = SubjectService.getInstance().getAllSubjects();
		cbSubject.getItems().clear();
		cbSubject.getItems()
			.addAll(allSubjects.stream()
				.map(subject -> (subject.getTitle() + " (ID " + subject.getId() + ")"))
				.collect(Collectors.toList()));
		cbSubject.getSelectionModel().select(0);
		cbSubject.setMinWidth(200);
		cbSubject.setMaxWidth(200);

		List<DifficultyLevel> allDifficulties = new ArrayList<>(EnumSet.allOf(DifficultyLevel.class));
		cbDifficulty.getItems().clear();
		cbDifficulty.getItems()
			.addAll(allDifficulties.stream().map(DifficultyLevel::getStrVal).collect(Collectors.toList()));
		cbDifficulty.getSelectionModel().select(0);
		cbDifficulty.setMinWidth(200);
		cbDifficulty.setMaxWidth(200);
	}
}
