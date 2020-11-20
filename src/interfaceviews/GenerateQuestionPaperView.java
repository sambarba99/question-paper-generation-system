package interfaceviews;

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

import dao.QuestionPaperDAO;
import dao.SubjectDAO;

import dto.DifficultyLevelDTO;
import dto.SubjectDTO;

import model.QuestionPaper;
import model.Subject;
import model.enums.BoxType;
import model.enums.DifficultyLevel;
import model.enums.SystemMessageType;

import tools.BoxMaker;
import tools.Constants;

import questionpapergeneration.QuestionPaperGenerator;

public class GenerateQuestionPaperView {

	private static SubjectDAO subjectDao = new SubjectDAO();

	private static SubjectDTO subjectDto = new SubjectDTO();

	private static DifficultyLevelDTO difficultyLevelDto = new DifficultyLevelDTO();

	private static QuestionPaperDAO questionPaperDao = new QuestionPaperDAO();

	private static QuestionPaperGenerator questionPaperGenerator = new QuestionPaperGenerator();

	private static boolean generated;

	/*
	 * Nodes for adding a new paper
	 */
	private static ChoiceBox cbSubject = new ChoiceBox();

	private static TextField txtTitle = new TextField();

	private static TextField txtCourseTitle = new TextField();

	private static TextField txtCourseCode = new TextField();

	private static ChoiceBox cbDifficulty = new ChoiceBox();

	private static TextField txtMarks = new TextField();

	private static TextField txtTimeRequired = new TextField();

	public static boolean generatePaper() {
		generated = false;
		Stage stage = new Stage();

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
				questionPaperDao.addQuestionPaper(generatedPaper);
				generated = true;
				stage.close();
				SystemMessageView.display(SystemMessageType.SUCCESS,
						"Paper generated! Return to Tutor view to view/export.");
			}
		});

		VBox vbox1 = (VBox) BoxMaker.makeBox(BoxType.VBOX, Pos.TOP_LEFT, 5, lblSelectSubject, cbSubject, lblEnterTitle,
				txtTitle, lblEnterCourseTitle, txtCourseTitle, lblEnterCourseCode, txtCourseCode);
		VBox vbox2 = (VBox) BoxMaker.makeBox(BoxType.VBOX, Pos.TOP_LEFT, 5, lblSelectDifficulty, cbDifficulty,
				lblEnterMarks, txtMarks, lblEnterTimeReq, txtTimeRequired);
		HBox hbox = (HBox) BoxMaker.makeBox(BoxType.HBOX, Pos.TOP_CENTER, 20, vbox1, vbox2);
		VBox vboxMain = (VBox) BoxMaker.makeBox(BoxType.VBOX, Pos.CENTER, 20, hbox, btnGenerate);

		FlowPane pane = new FlowPane();
		pane.getStyleClass().add("flow-pane");
		pane.getChildren().add(vboxMain);

		setup();

		Scene scene = new Scene(pane, 550, 400);
		scene.getStylesheets().add("style.css");
		stage.setTitle("Generate Question Paper");
		stage.setScene(scene);
		stage.setResizable(false);
		// so multiple instances of this window can't be opened
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
		return generated;
	}

	/**
	 * Generate a paper and add it via QuestionPaperDAO
	 * 
	 * @return whether or not paper has been generated successfully
	 */
	private static QuestionPaper generatePaperWithParams() {
		String title = txtTitle.getText().trim();
		String courseTitle = txtCourseTitle.getText().trim();
		String courseCode = txtCourseCode.getText().trim();
		if (title.length() == 0 || courseTitle.length() == 0 || courseCode.length() == 0) {
			SystemMessageView.display(SystemMessageType.ERROR, "Please enter the title, course title and course code.");
			return null;
		} else if (!title.matches(Constants.TITLE_REGEX) || !courseTitle.matches(Constants.TITLE_REGEX)
				|| !courseCode.matches(Constants.TITLE_REGEX)) {
			SystemMessageView.display(SystemMessageType.ERROR,
					"Titles and codes must be only alphanumeric, and no repeating spaces.");
			return null;
		}

		int marks = 0;
		try {
			marks = Integer.parseInt(txtMarks.getText());
		} catch (NumberFormatException e) {
			SystemMessageView.display(SystemMessageType.ERROR, "Invalid number of marks.");
			return null;
		}
		int timeReq = 0;
		try {
			timeReq = Integer.parseInt(txtTimeRequired.getText());
		} catch (NumberFormatException e) {
			SystemMessageView.display(SystemMessageType.ERROR, "Invalid time required.");
			return null;
		}

		int subjectId = subjectDto.getSubjectId(cbSubject);
		DifficultyLevel difficultyLevel = difficultyLevelDto.getSelectedDifficulty(cbDifficulty);

		QuestionPaper generatedPaper = questionPaperGenerator.generatePaper(subjectId, title, courseTitle, courseCode,
				difficultyLevel, marks, timeReq);

		return generatedPaper;
	}

	/**
	 * Set up choice boxes
	 */
	private static void setup() {
		txtTitle.setText("");
		txtCourseTitle.setText("");
		txtCourseCode.setText("");
		txtMarks.setText("");
		txtTimeRequired.setText("");

		List<Subject> allSubjects = subjectDao.getAllSubjects();
		cbSubject.getItems().clear();
		cbSubject.getItems().addAll(
				allSubjects.stream().map(s -> (s.getTitle() + " (ID " + s.getId() + ")")).collect(Collectors.toList()));
		cbSubject.getSelectionModel().select(0);
		cbSubject.setPrefWidth(200);

		List<DifficultyLevel> allDifficulties = new ArrayList<>(EnumSet.allOf(DifficultyLevel.class));
		cbDifficulty.getItems().clear();
		cbDifficulty.getItems().addAll(allDifficulties.stream().map(d -> d.toString()).collect(Collectors.toList()));
		cbDifficulty.getSelectionModel().select(0);
		cbDifficulty.setPrefWidth(200);
	}
}