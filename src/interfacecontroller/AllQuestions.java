package interfacecontroller;

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
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import service.QuestionService;
import service.SubjectService;

import dto.DifficultyLevelDTO;
import dto.QuestionDTO;
import dto.SubjectDTO;

import model.Question;
import model.Subject;
import model.enums.BoxType;
import model.enums.DifficultyLevel;
import model.enums.SystemNotificationType;

import utils.BoxMaker;
import utils.Constants;

/**
 * Allows the user to view all stored questions.
 *
 * @author Sam Barba
 */
public class AllQuestions {

	private static Stage stage = new Stage();

	private static boolean modified;

	private static ListView<String> listViewQuestions = new ListView<>();

	private static TextArea txtAreaQuestion = new TextArea();

	/*
	 * Nodes for adding a new question
	 */
	private static ChoiceBox cbSubject = new ChoiceBox();

	private static TextField txtStatement = new TextField();

	private static TextField txtOpt1 = new TextField();

	private static TextField txtOpt2 = new TextField();

	private static TextField txtOpt3 = new TextField();

	private static TextField txtOpt4 = new TextField();

	private static ChoiceBox cbCorrectNum = new ChoiceBox();

	private static ChoiceBox cbDifficulty = new ChoiceBox();

	private static TextField txtMarks = new TextField();

	private static TextField txtTimeRequired = new TextField();

	/**
	 * Display all questions, and return whether or not a modification has occurred upon closing the window.
	 * 
	 * @return whether or not the user has made a modification
	 */
	public static boolean display() {
		modified = false;

		Label lblSelectQuestion = new Label("Select a question to view:");
		Label lblAddQueston = new Label("Add a question?");
		Label lblSelectSubject = new Label("Select the subject:");
		Label lblEnterStatement = new Label("Enter question statement:");
		Label lblEnterOpt1 = new Label("Enter answer option 1:");
		Label lblEnterOpt2 = new Label("Enter answer option 2:");
		Label lblEnterOpt3 = new Label("Enter answer option 3:");
		Label lblEnterOpt4 = new Label("Enter answer option 4:");
		Label lblSelectCorrect = new Label("Select correct answer no.:");
		Label lblSelectDIfficulty = new Label("Select difficulty level:");
		Label lblEnterMarks = new Label("Enter no. marks:");
		Label lblEnterTimeReq = new Label("Enter time required (mins):");
		Button btnAddQuestion = new Button("Add question");
		Button btnDelQuestion = new Button("Delete question");

		listViewQuestions.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			int questionId = QuestionDTO.getInstance().getQuestionId(listViewQuestions);
			if (questionId != 0) {
				txtAreaQuestion.setText(QuestionDTO.getInstance().getTxtAreaQuestionStr(questionId));
			}
		});
		btnAddQuestion.setOnAction(action -> {
			addQuestion();
		});
		btnDelQuestion.setOnAction(action -> {
			deleteQuestion();
		});

		BoxMaker boxMaker = BoxMaker.getInstance();
		VBox vboxViewQuestion = (VBox) boxMaker.makeBox(BoxType.VBOX, Pos.TOP_CENTER, 10, lblSelectQuestion,
				listViewQuestions, txtAreaQuestion);
		VBox vboxQuestionValues = (VBox) boxMaker.makeBox(BoxType.VBOX, Pos.TOP_LEFT, 7, lblAddQueston,
				lblSelectSubject, cbSubject, lblEnterStatement, txtStatement, lblEnterOpt1, txtOpt1, lblEnterOpt2,
				txtOpt2, lblEnterOpt3, txtOpt3, lblEnterOpt4, txtOpt4, lblSelectCorrect, cbCorrectNum,
				lblSelectDIfficulty, cbDifficulty, lblEnterMarks, txtMarks, lblEnterTimeReq, txtTimeRequired,
				btnAddQuestion);

		setup();

		FlowPane pane = new FlowPane();
		pane.getStyleClass().add("flow-pane");
		pane.getChildren().addAll(btnDelQuestion, vboxViewQuestion, vboxQuestionValues);

		Scene scene = new Scene(pane, 950, 750);
		scene.getStylesheets().add("style.css");
		stage.setScene(scene);
		stage.setTitle("View All Questions");
		stage.setResizable(false);
		// so multiple instances of this window can't be opened
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
		return modified;
	}

	/**
	 * Verify the validity of the new question's attributes, and add the new question.
	 */
	private static void addQuestion() {
		if (questionAdded()) {
			listViewQuestions.getItems().clear();
			listViewQuestions.getItems().addAll(QuestionDTO.getInstance().getQuestionListViewItems());
			resetAddQuestionFields();
			txtAreaQuestion.setText("");
			modified = true;
			SystemNotification.display(SystemNotificationType.SUCCESS, "Question added!");
		}
	}

	/**
	 * Delete selected question with confirmation.
	 */
	private static void deleteQuestion() {
		if (listViewQuestions.getSelectionModel().getSelectedItems().isEmpty()) {
			SystemNotification.display(SystemNotificationType.ERROR, "Please select a question.");
		} else if (DeletionConfirm.confirmDelete("question")) {
			int questionId = QuestionDTO.getInstance().getQuestionId(listViewQuestions);
			QuestionService.getInstance().deleteQuestionById(questionId);
			listViewQuestions.getItems().clear();
			listViewQuestions.getItems().addAll(QuestionDTO.getInstance().getQuestionListViewItems());
			txtAreaQuestion.setText("");
			modified = true;
			SystemNotification.display(SystemNotificationType.SUCCESS, "Question deleted.");
		}
	}

	/**
	 * Add a question via QuestionService based on entered/selected attribute values.
	 * 
	 * @return whether or not question has been added successfully
	 */
	private static boolean questionAdded() {
		String statement = txtStatement.getText();
		String opt1 = txtOpt1.getText().trim();
		String opt2 = txtOpt2.getText().trim();
		String opt3 = txtOpt3.getText().trim();
		String opt4 = txtOpt4.getText().trim();
		if (statement.length() == 0 || opt1.length() == 0 || opt2.length() == 0 || opt3.length() == 0
				|| opt4.length() == 0) {
			SystemNotification.display(SystemNotificationType.ERROR,
					"Please enter the statement and all answer options.");
			return false;
		} else if (!statement.matches(Constants.QUESTION_REGEX) || !opt1.matches(Constants.QUESTION_REGEX)
				|| !opt2.matches(Constants.QUESTION_REGEX) || !opt3.matches(Constants.QUESTION_REGEX)
				|| !opt4.matches(Constants.QUESTION_REGEX)) {
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
		int correctAnsNo = Integer.parseInt(cbCorrectNum.getSelectionModel().getSelectedItem().toString());
		DifficultyLevel difficultyLevel = DifficultyLevelDTO.getInstance().getSelectedDifficulty(cbDifficulty);

		Question question = new Question(id, subjectId, statement, Arrays.asList(opt1, opt2, opt3, opt4), correctAnsNo,
				difficultyLevel, marks, timeReq);
		QuestionService.getInstance().addQuestion(question);

		return true;
	}

	/**
	 * Set up window.
	 */
	private static void setup() {
		listViewQuestions.getItems().clear();
		listViewQuestions.getItems().addAll(QuestionDTO.getInstance().getQuestionListViewItems());
		listViewQuestions.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		txtAreaQuestion.setEditable(false);
		txtAreaQuestion.setPrefHeight(200);
		txtAreaQuestion.setText("");

		List<Subject> allSubjects = SubjectService.getInstance().getAllSubjects();
		cbSubject.getItems().clear();
		cbSubject.getItems().addAll(allSubjects.stream()
				.map(subject -> (subject.getTitle() + " (ID " + subject.getId() + ")")).collect(Collectors.toList()));
		cbSubject.getSelectionModel().select(0);
		cbSubject.setPrefWidth(200);

		cbCorrectNum.getItems().clear();
		cbCorrectNum.getItems().addAll("1", "2", "3", "4");
		cbCorrectNum.getSelectionModel().select(0);

		List<DifficultyLevel> allDifficulties = new ArrayList<>(EnumSet.allOf(DifficultyLevel.class));
		cbDifficulty.getItems().clear();
		cbDifficulty.getItems()
				.addAll(allDifficulties.stream().map(DifficultyLevel::getStrVal).collect(Collectors.toList()));
		cbDifficulty.getSelectionModel().select(0);
		cbDifficulty.setPrefWidth(200);
	}

	/**
	 * Reset all nodes for adding a question.
	 */
	private static void resetAddQuestionFields() {
		cbSubject.getSelectionModel().select(0);
		txtStatement.setText("");
		txtOpt1.setText("");
		txtOpt2.setText("");
		txtOpt3.setText("");
		txtOpt4.setText("");
		cbCorrectNum.getSelectionModel().select(0);
		cbDifficulty.getSelectionModel().select(0);
		txtMarks.setText("");
		txtTimeRequired.setText("");
	}
}
