package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.builders.QuestionBuilder;
import model.dto.AnswerOptionDTO;
import model.dto.DifficultyLevelDTO;
import model.dto.SubjectDTO;
import model.persisted.Question;
import model.persisted.Subject;
import model.service.QuestionService;
import model.service.SubjectService;

import view.Constants;
import view.builders.ButtonBuilder;
import view.builders.PaneBuilder;
import view.enums.AnswerOption;
import view.enums.BoxType;
import view.enums.DifficultyLevel;
import view.enums.SystemNotificationType;
import view.enums.UserAction;

/**
 * Allows the user to view all stored questions, and manage questions.
 *
 * @author Sam Barba
 */
public class AddQuestion {

	private static Stage stage;

	private static boolean added;

	/*
	 * Nodes for adding a new question
	 */
	private static ChoiceBox cbSubject = new ChoiceBox();

	private static TextArea txtAreaStatement = new TextArea();

	private static TextField txtOpt1 = new TextField();

	private static TextField txtOpt2 = new TextField();

	private static TextField txtOpt3 = new TextField();

	private static TextField txtOpt4 = new TextField();

	private static ChoiceBox cbCorrectAns = new ChoiceBox();

	private static ChoiceBox cbDifficulty = new ChoiceBox();

	private static TextField txtMarks = new TextField();

	private static TextField txtTimeRequired = new TextField();

	/**
	 * Display nodes for adding a question.
	 */
	public static boolean display() {
		stage = new Stage();
		added = false;

		Label lblSelectSubject = new Label("Select the subject:");
		Label lblEnterStatement = new Label("Enter question statement:");
		Label lblEnterOpt1 = new Label("Enter answer option A:");
		Label lblEnterOpt2 = new Label("Enter answer option B:");
		Label lblEnterOpt3 = new Label("Enter answer option C:");
		Label lblEnterOpt4 = new Label("Enter answer option D:");
		Label lblSelectCorrect = new Label("Select correct answer:");
		Label lblSelectDIfficulty = new Label("Select difficulty level:");
		Label lblEnterMarks = new Label("Enter no. marks:");
		Label lblEnterTimeReq = new Label("Enter time required (mins):");

		Button btnAddQuestion = new ButtonBuilder().withWidth(100).withUserAction(UserAction.ADD)
			.withActionEvent(action -> {
				if (validateAndAddQuestion()) {
					resetAddQuestionFields();
					stage.close();
					added = true;
				}
			}).build();

		VBox vbox1 = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX).withAlignment(Pos.TOP_LEFT).withSpacing(10)
			.withNodes(lblSelectSubject, cbSubject, lblEnterStatement, txtAreaStatement, lblEnterOpt1, txtOpt1,
				lblEnterOpt2, txtOpt2, lblEnterOpt3, txtOpt3, lblEnterOpt4, txtOpt4)
			.build();
		VBox vbox2 = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX).withAlignment(Pos.TOP_LEFT).withSpacing(10)
			.withNodes(lblSelectCorrect, cbCorrectAns, lblSelectDIfficulty, cbDifficulty, lblEnterMarks, txtMarks,
				lblEnterTimeReq, txtTimeRequired, btnAddQuestion)
			.build();
		HBox hboxMain = (HBox) new PaneBuilder().withBoxType(BoxType.HBOX).withAlignment(Pos.CENTER).withSpacing(20)
			.withNodes(vbox1, vbox2).build();

		setup();

		FlowPane pane = new FlowPane();
		pane.getStyleClass().add("flow-pane");
		pane.getChildren().add(hboxMain);

		Scene scene = new Scene(pane, 700, 600);
		scene.getStylesheets().add("style.css");
		stage.setScene(scene);
		stage.setTitle("Add New Question");
		stage.setResizable(false);
		// so multiple instances of this window can't be opened
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
		return added;
	}

	/**
	 * Validate question attributes, and add via QuestionService.
	 * 
	 * @return whether or not question has been added successfully
	 */
	private static boolean validateAndAddQuestion() {
		String statement = txtAreaStatement.getText();
		String opt1 = txtOpt1.getText().trim();
		String opt2 = txtOpt2.getText().trim();
		String opt3 = txtOpt3.getText().trim();
		String opt4 = txtOpt4.getText().trim();

		if (statement.length() == 0 || opt1.length() == 0 || opt2.length() == 0 || opt3.length() == 0
			|| opt4.length() == 0) {
			SystemNotification.display(SystemNotificationType.ERROR,
				"Please enter the statement and all answer options.");
			return false;
		} else if (!statement.matches(Constants.QUESTION_STATEMENT_REGEX)
			|| !opt1.matches(Constants.QUESTION_STATEMENT_REGEX) || !opt2.matches(Constants.QUESTION_STATEMENT_REGEX)
			|| !opt3.matches(Constants.QUESTION_STATEMENT_REGEX) || !opt4.matches(Constants.QUESTION_STATEMENT_REGEX)) {
			SystemNotification.display(SystemNotificationType.ERROR,
				"Statement and answers must not have repeating spaces.");
			return false;
		}

		int marks = 0;
		try {
			marks = Integer.parseInt(txtMarks.getText());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			SystemNotification.display(SystemNotificationType.ERROR, "Invalid number of marks.");
			return false;
		}
		int timeReq = 0;
		try {
			timeReq = Integer.parseInt(txtTimeRequired.getText());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			SystemNotification.display(SystemNotificationType.ERROR, "Invalid time required.");
			return false;
		}
		int id = QuestionService.getInstance().getHighestQuestionId() + 1;
		int subjectId = SubjectDTO.getInstance().getSubjectId(cbSubject);
		AnswerOption correctAnsOption = AnswerOptionDTO.getInstance().getSelectedAnswerOption(cbCorrectAns);
		DifficultyLevel difficultyLevel = DifficultyLevelDTO.getInstance().getSelectedDifficulty(cbDifficulty);

		Question question = new QuestionBuilder().withId(id).withSubjectId(subjectId).withStatement(statement)
			.withAnswerOptions(Arrays.asList(opt1, opt2, opt3, opt4)).withCorrectAnswerOption(correctAnsOption)
			.withDifficultyLevel(difficultyLevel).withMarks(marks).withTimeRequiredMins(timeReq).build();

		QuestionService.getInstance().addQuestion(question);
		return true;
	}

	/**
	 * Set up window.
	 */
	private static void setup() {
		List<Subject> allSubjects = SubjectService.getInstance().getAllSubjects();
		cbSubject.getItems().clear();
		cbSubject.getItems().addAll(allSubjects.stream()
			.map(subject -> (subject.getTitle() + " (ID " + subject.getId() + ")")).collect(Collectors.toList()));
		cbSubject.getSelectionModel().select(0);
		cbSubject.setMinWidth(200);
		cbSubject.setMaxWidth(200);

		txtAreaStatement.setMinSize(350, 160);
		txtAreaStatement.setMaxSize(350, 160);
		txtAreaStatement.textProperty().addListener((obs, oldText, newText) -> {
			txtAreaStatement.setText(newText.replace("\n", ""));
		});

		List<AnswerOption> allAnswerOptions = new ArrayList<>(EnumSet.allOf(AnswerOption.class));
		cbCorrectAns.getItems().clear();
		cbCorrectAns.getItems()
			.addAll(allAnswerOptions.stream().map(AnswerOption::toString).collect(Collectors.toList()));
		cbCorrectAns.getSelectionModel().select(0);

		List<DifficultyLevel> allDifficulties = new ArrayList<>(EnumSet.allOf(DifficultyLevel.class));
		cbDifficulty.getItems().clear();
		cbDifficulty.getItems()
			.addAll(allDifficulties.stream().map(DifficultyLevel::getStrVal).collect(Collectors.toList()));
		cbDifficulty.getSelectionModel().select(0);
		cbDifficulty.setMinWidth(200);
		cbDifficulty.setMaxWidth(200);
	}

	/**
	 * Reset all nodes for adding a question.
	 */
	private static void resetAddQuestionFields() {
		cbSubject.getSelectionModel().select(0);
		txtAreaStatement.setText(Constants.EMPTY);
		txtOpt1.setText(Constants.EMPTY);
		txtOpt2.setText(Constants.EMPTY);
		txtOpt3.setText(Constants.EMPTY);
		txtOpt4.setText(Constants.EMPTY);
		cbCorrectAns.getSelectionModel().select(0);
		cbDifficulty.getSelectionModel().select(0);
		txtMarks.setText(Constants.EMPTY);
		txtTimeRequired.setText(Constants.EMPTY);
	}
}
