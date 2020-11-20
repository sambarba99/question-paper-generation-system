package interfaceviews;

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

import dao.QuestionDAO;
import dao.SubjectDAO;

import dto.DifficultyLevelDTO;
import dto.QuestionDTO;
import dto.SubjectDTO;

import model.Question;
import model.Subject;
import model.enums.BoxType;
import model.enums.DifficultyLevel;
import model.enums.SystemMessageType;

import utils.BoxMaker;
import utils.Constants;

public class AllQuestionsView {

	private static QuestionDAO questionDao = new QuestionDAO();

	private static QuestionDTO questionDto = new QuestionDTO();

	private static SubjectDAO subjectDao = new SubjectDAO();

	private static SubjectDTO subjectDto = new SubjectDTO();

	private static DifficultyLevelDTO difficultyLevelDto = new DifficultyLevelDTO();

	private static ListView<String> listViewQuestions = new ListView<>();

	private static TextArea txtAreaQuestion = new TextArea();

	private static boolean modified;

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

	public static boolean display() {
		modified = false;
		Stage stage = new Stage();

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
			int questionId = questionDto.getQuestionId(listViewQuestions);
			if (questionId != 0) {
				txtAreaQuestion.setText(questionDto.getTxtAreaQuestionStr(questionId));
			}
		});
		btnAddQuestion.setOnAction(action -> {
			if (addQuestion()) {
				listViewQuestions.getItems().clear();
				listViewQuestions.getItems().addAll(questionDto.getQuestionListViewItems());
				resetAddQuestionFields();
				txtAreaQuestion.setText("");
				modified = true;
				SystemMessageView.display(SystemMessageType.SUCCESS, "Question added!");
			}
		});
		btnDelQuestion.setOnAction(action -> {
			if (listViewQuestions.getSelectionModel().getSelectedItems().isEmpty()) {
				SystemMessageView.display(SystemMessageType.ERROR, "Please select a question.");
			} else if (DeletionConfirmView.confirmDelete("question")) {
				int questionId = questionDto.getQuestionId(listViewQuestions);
				questionDao.deleteQuestionById(questionId);
				listViewQuestions.getItems().clear();
				listViewQuestions.getItems().addAll(questionDto.getQuestionListViewItems());
				txtAreaQuestion.setText("");
				modified = true;
				SystemMessageView.display(SystemMessageType.SUCCESS, "Question deleted.");
			}
		});

		VBox vboxViewQuestion = (VBox) BoxMaker.makeBox(BoxType.VBOX, Pos.TOP_CENTER, 10, lblSelectQuestion,
				listViewQuestions, txtAreaQuestion);
		VBox vboxQuestionValues = (VBox) BoxMaker.makeBox(BoxType.VBOX, Pos.TOP_LEFT, 7, lblAddQueston,
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
	 * Set up window
	 */
	private static void setup() {
		listViewQuestions.getItems().clear();
		listViewQuestions.getItems().addAll(questionDto.getQuestionListViewItems());
		listViewQuestions.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		txtAreaQuestion.setEditable(false);
		txtAreaQuestion.setPrefHeight(200);
		txtAreaQuestion.setText("");

		List<Subject> allSubjects = subjectDao.getAllSubjects();
		cbSubject.getItems().clear();
		cbSubject.getItems().addAll(
				allSubjects.stream().map(s -> (s.getTitle() + " (ID " + s.getId() + ")")).collect(Collectors.toList()));
		cbSubject.getSelectionModel().select(0);
		cbSubject.setPrefWidth(200);

		cbCorrectNum.getItems().clear();
		cbCorrectNum.getItems().addAll("1", "2", "3", "4");
		cbCorrectNum.getSelectionModel().select(0);

		List<DifficultyLevel> allDifficulties = new ArrayList<>(EnumSet.allOf(DifficultyLevel.class));
		cbDifficulty.getItems().clear();
		cbDifficulty.getItems().addAll(allDifficulties.stream().map(d -> d.toString()).collect(Collectors.toList()));
		cbDifficulty.getSelectionModel().select(0);
		cbDifficulty.setPrefWidth(200);
	}

	/**
	 * Add a question via QuestionDAO based on entered/selected attribute values
	 * 
	 * @return whether or not question has been added successfully
	 */
	public static boolean addQuestion() {
		String statement = txtStatement.getText();
		String opt1 = txtOpt1.getText().trim();
		String opt2 = txtOpt2.getText().trim();
		String opt3 = txtOpt3.getText().trim();
		String opt4 = txtOpt4.getText().trim();
		if (statement.length() == 0 || opt1.length() == 0 || opt2.length() == 0 || opt3.length() == 0
				|| opt4.length() == 0) {
			SystemMessageView.display(SystemMessageType.ERROR, "Please enter the statement and all answer options.");
			return false;
		} else if (!statement.matches(Constants.QUESTION_REGEX) || !opt1.matches(Constants.QUESTION_REGEX)
				|| !opt2.matches(Constants.QUESTION_REGEX) || !opt3.matches(Constants.QUESTION_REGEX)
				|| !opt4.matches(Constants.QUESTION_REGEX)) {
			SystemMessageView.display(SystemMessageType.ERROR, "Statement and answers must not have repeating spaces.");
			return false;
		}

		int marks = 0;
		try {
			marks = Integer.parseInt(txtMarks.getText());
		} catch (NumberFormatException e) {
			SystemMessageView.display(SystemMessageType.ERROR, "Invalid number of marks.");
			return false;
		}
		int timeReq = 0;
		try {
			timeReq = Integer.parseInt(txtTimeRequired.getText());
		} catch (NumberFormatException e) {
			SystemMessageView.display(SystemMessageType.ERROR, "Invalid time required.");
			return false;
		}
		int id = questionDao.getHighestQuestionId() + 1;
		int subjectId = subjectDto.getSubjectId(cbSubject);
		int answerNo = Integer.parseInt(cbCorrectNum.getSelectionModel().getSelectedItem().toString());
		DifficultyLevel difficultyLevel = difficultyLevelDto.getSelectedDifficulty(cbDifficulty);

		Question q = new Question(id, subjectId, statement, Arrays.asList(opt1, opt2, opt3, opt4), answerNo,
				difficultyLevel, marks, timeReq);
		questionDao.addQuestion(q);

		return true;
	}

	/**
	 * Reset all nodes for adding a question
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