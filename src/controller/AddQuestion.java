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

import model.builders.AnswerBuilder;
import model.builders.QuestionBuilder;
import model.dto.DifficultyLevelDTO;
import model.dto.SubjectDTO;
import model.persisted.Answer;
import model.persisted.Question;
import model.persisted.Subject;
import model.service.QuestionService;
import model.service.SubjectService;

import view.Constants;
import view.builders.ButtonBuilder;
import view.builders.PaneBuilder;
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

	private static final int ASCII_A = 65;

	private static Stage stage;

	private static boolean added;

	/*
	 * Nodes for adding a new question
	 */
	private static ChoiceBox cbSubject = new ChoiceBox();

	private static TextArea txtAreaStatement = new TextArea();

	private static TextField txtAnsA = new TextField();

	private static TextField txtAnsB = new TextField();

	private static TextField txtAnsC = new TextField();

	private static TextField txtAnsD = new TextField();

	private static ChoiceBox cbCorrectAnsLetter = new ChoiceBox();

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
		Label lblEnterAnsA = new Label("Enter answer A:");
		Label lblEnterAnsB = new Label("Enter answer B:");
		Label lblEnterAnsC = new Label("Enter answer C:");
		Label lblEnterAnsD = new Label("Enter answer D:");
		Label lblSelectCorrect = new Label("Select correct answer:");
		Label lblSelectDIfficulty = new Label("Select difficulty level:");
		Label lblEnterMarks = new Label("Enter no. marks:");
		Label lblEnterTimeReq = new Label("Enter time required (mins):");

		Button btnAddQuestion = new ButtonBuilder().withWidth(100)
			.withUserAction(UserAction.ADD)
			.withClickAction(action -> {
				if (validateAndAddQuestion()) {
					resetAddQuestionFields();
					added = true;
					stage.close();
				}
			})
			.build();

		VBox vbox1 = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.TOP_LEFT)
			.withSpacing(10)
			.withNodes(lblSelectSubject, cbSubject, lblEnterStatement, txtAreaStatement, lblEnterAnsA, txtAnsA,
				lblEnterAnsB, txtAnsB, lblEnterAnsC, txtAnsC, lblEnterAnsD, txtAnsD)
			.build();
		VBox vbox2 = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.TOP_LEFT)
			.withSpacing(10)
			.withNodes(lblSelectCorrect, cbCorrectAnsLetter, lblSelectDIfficulty, cbDifficulty, lblEnterMarks, txtMarks,
				lblEnterTimeReq, txtTimeRequired, btnAddQuestion)
			.build();
		HBox hboxMain = (HBox) new PaneBuilder().withBoxType(BoxType.HBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(20)
			.withNodes(vbox1, vbox2)
			.build();

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
		String ansA = txtAnsA.getText().trim();
		String ansB = txtAnsB.getText().trim();
		String ansC = txtAnsC.getText().trim();
		String ansD = txtAnsD.getText().trim();

		if (statement.length() == 0 || ansA.length() == 0 || ansB.length() == 0 || ansC.length() == 0
			|| ansD.length() == 0) {
			SystemNotification.display(SystemNotificationType.ERROR,
				"Please enter the statement and all possible answers.");
			return false;
		} else if (!statement.matches(Constants.QUESTION_STATEMENT_REGEX)
			|| !ansA.matches(Constants.QUESTION_STATEMENT_REGEX) || !ansB.matches(Constants.QUESTION_STATEMENT_REGEX)
			|| !ansC.matches(Constants.QUESTION_STATEMENT_REGEX) || !ansD.matches(Constants.QUESTION_STATEMENT_REGEX)) {
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
		int correctAnswerPos = cbCorrectAnsLetter.getSelectionModel().getSelectedIndex();
		DifficultyLevel difficultyLevel = DifficultyLevelDTO.getInstance().getSelectedDifficulty(cbDifficulty);

		Question question = new QuestionBuilder().withId(id)
			.withSubjectId(subjectId)
			.withStatement(statement)
			.withAnswers(makeAnswers(Arrays.asList(ansA, ansB, ansC, ansD), correctAnswerPos))
			.withDifficultyLevel(difficultyLevel)
			.withMarks(marks)
			.withTimeRequiredMins(timeReq)
			.build();

		QuestionService.getInstance().addQuestion(question);
		return true;
	}

	/**
	 * Set up window.
	 */
	private static void setup() {
		List<Subject> allSubjects = SubjectService.getInstance().getAllSubjects();
		cbSubject.getItems().clear();
		cbSubject.getItems()
			.addAll(allSubjects.stream()
				.map(subject -> (subject.getTitle() + " (ID " + subject.getId() + ")"))
				.collect(Collectors.toList()));
		cbSubject.getSelectionModel().select(0);
		cbSubject.setMinWidth(200);
		cbSubject.setMaxWidth(200);

		txtAreaStatement.setMinSize(350, 160);
		txtAreaStatement.setMaxSize(350, 160);
		txtAreaStatement.textProperty()
			.addListener((obs, oldText, newText) -> {
				// remove characters that could potentially harm CSV read/write functionality
				txtAreaStatement
					.setText(newText.replace(Constants.NEWLINE, Constants.EMPTY).replace(Constants.QUOT_MARK, "'"));
			});

		cbCorrectAnsLetter.getItems().clear();
		cbCorrectAnsLetter.getItems().addAll("A", "B", "C", "D");
		cbCorrectAnsLetter.getSelectionModel().select(0);

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
		txtAnsA.setText(Constants.EMPTY);
		txtAnsB.setText(Constants.EMPTY);
		txtAnsC.setText(Constants.EMPTY);
		txtAnsD.setText(Constants.EMPTY);
		cbCorrectAnsLetter.getSelectionModel().select(0);
		cbDifficulty.getSelectionModel().select(0);
		txtMarks.setText(Constants.EMPTY);
		txtTimeRequired.setText(Constants.EMPTY);
	}

	/**
	 * Make answers for a question, given a list of possible answers and the position of the correct one.
	 * 
	 * @param answersStr          - the list of possible answers
	 * @param correctAnswerLetter - the position of the correct answer
	 * @return - list of Answer objects to be used in Question building
	 */
	private static List<Answer> makeAnswers(List<String> answersStr, int correctAnswerPos) {
		List<Answer> answers = new ArrayList<>();

		for (int i = 0; i < answersStr.size(); i++) {
			Answer answer = new AnswerBuilder().withValue(answersStr.get(i))
				.withLetter(Character.toString((char) (ASCII_A + i)))
				.withIsCorrect(i == correctAnswerPos)
				.build();

			answers.add(answer);
		}
		return answers;
	}
}