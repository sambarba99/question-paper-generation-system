package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.builders.AnswerBuilder;
import model.builders.QuestionBuilder;
import model.persisted.Answer;
import model.persisted.Question;
import model.persisted.Subject;
import model.service.QuestionService;
import model.service.SubjectService;

import view.SystemNotification;
import view.builders.ButtonBuilder;
import view.builders.PaneBuilder;
import view.enums.BoxType;
import view.enums.SkillLevel;
import view.enums.SystemNotificationType;
import view.enums.UserAction;
import view.utils.Constants;
import view.utils.LogoMaker;
import view.utils.StringFormatter;

/**
 * Allows the user to view all stored questions, and manage questions.
 *
 * @author Sam Barba
 */
public class AddQuestion {

	private static Stage stage;

	private static boolean added;

	private static ChoiceBox choiceSubject = new ChoiceBox();

	private static TextArea txtAreaStatement = new TextArea();

	private static TextField txtAnsA = new TextField();

	private static TextField txtAnsB = new TextField();

	private static TextField txtAnsC = new TextField();

	private static TextField txtAnsD = new TextField();

	private static ChoiceBox choiceCorrectAnsLetter = new ChoiceBox();

	private static Slider sliderSkillLvl = new Slider();

	private static Label lblSelectedSkillLvl = new Label("Skill level: KNOWLEDGE");

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
		Label lblSelectSkillLvl = new Label("Select skill level\n(based on Bloom's taxonomy):");
		Label lblEnterMarks = new Label("Enter no. marks:");
		Label lblEnterTimeReq = new Label("Enter time required (mins):");

		Button btnAddQuestion = new ButtonBuilder().withWidth(100).withUserAction(UserAction.ADD).withActionEvent(e -> {
			if (validateAndAddQuestion()) {
				resetAddQuestionFields();
				added = true;
				stage.close();
			}
		}).build();

		VBox vbox1 = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.TOP_LEFT)
			.withSpacing(10)
			.withNodes(lblSelectSubject, choiceSubject, lblEnterStatement, txtAreaStatement, lblEnterAnsA, txtAnsA,
				lblEnterAnsB, txtAnsB, lblEnterAnsC, txtAnsC, lblEnterAnsD, txtAnsD)
			.build();
		VBox vbox2 = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.TOP_LEFT)
			.withSpacing(10)
			.withNodes(lblSelectCorrect, choiceCorrectAnsLetter, lblSelectSkillLvl, sliderSkillLvl, lblSelectedSkillLvl,
				lblEnterMarks, txtMarks, lblEnterTimeReq, txtTimeRequired, btnAddQuestion)
			.build();
		HBox hbox = (HBox) new PaneBuilder().withBoxType(BoxType.HBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(20)
			.withNodes(vbox1, vbox2)
			.build();
		VBox vboxMain = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(30)
			.withNodes(LogoMaker.makeLogo(300), hbox)
			.build();

		setup();

		Scene scene = new Scene(vboxMain, 700, 750);
		scene.getStylesheets().add(Constants.CSS_STYLE_PATH);
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
		String ansA = txtAnsA.getText();
		String ansB = txtAnsB.getText();
		String ansC = txtAnsC.getText();
		String ansD = txtAnsD.getText();

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

		statement = StringFormatter.capitalise(statement);
		ansA = StringFormatter.capitalise(ansA);
		ansB = StringFormatter.capitalise(ansB);
		ansC = StringFormatter.capitalise(ansC);
		ansD = StringFormatter.capitalise(ansD);

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
		int subjectId = SubjectService.getInstance()
			.getSubjectIdFromDisplayStr(choiceSubject.getSelectionModel().getSelectedItem().toString());

		int correctAnswerPos = choiceCorrectAnsLetter.getSelectionModel().getSelectedIndex();
		SkillLevel skillLevel = SkillLevel.getFromInt((int) sliderSkillLvl.getValue());

		Question question = new QuestionBuilder().withId(id)
			.withSubjectId(subjectId)
			.withStatement(statement)
			.withAnswers(makeAnswers(Arrays.asList(ansA, ansB, ansC, ansD), correctAnswerPos))
			.withSkillLevel(skillLevel)
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
		choiceSubject.getItems().clear();
		choiceSubject.getItems().addAll(allSubjects.stream().map(Subject::toString).collect(Collectors.toList()));
		choiceSubject.getSelectionModel().select(0);
		choiceSubject.setPrefWidth(200);

		txtAreaStatement.setPrefSize(350, 160);
		txtAreaStatement.textProperty()
			.addListener((obs, oldText, newText) -> {
				// remove characters that could potentially harm CSV read/write functionality
				txtAreaStatement
					.setText(newText.replace(Constants.NEWLINE, Constants.EMPTY).replace(Constants.QUOT_MARK, "'"));
			});

		choiceCorrectAnsLetter.getItems().clear();
		choiceCorrectAnsLetter.getItems().addAll("A", "B", "C", "D");
		choiceCorrectAnsLetter.getSelectionModel().select(0);

		List<SkillLevel> allSkillLvls = new ArrayList<>(EnumSet.allOf(SkillLevel.class));
		sliderSkillLvl.setMin(1);
		sliderSkillLvl.setMax(allSkillLvls.size());
		sliderSkillLvl.setPrefWidth(200);
		sliderSkillLvl.setMajorTickUnit(1);
		sliderSkillLvl.setShowTickLabels(true);
		sliderSkillLvl.setShowTickMarks(true);
		sliderSkillLvl.valueProperty().addListener((obs, oldValue, newValue) -> {
			int intVal = newValue.intValue();
			sliderSkillLvl.setValue(intVal); // snap to exact value
			lblSelectedSkillLvl.setText("Skill level: " + allSkillLvls.get(intVal - 1).getStrVal());
		});

		lblSelectedSkillLvl.setPrefWidth(240);
	}

	/**
	 * Reset all nodes for adding a question.
	 */
	private static void resetAddQuestionFields() {
		choiceSubject.getSelectionModel().select(0);
		txtAreaStatement.setText(Constants.EMPTY);
		txtAnsA.setText(Constants.EMPTY);
		txtAnsB.setText(Constants.EMPTY);
		txtAnsC.setText(Constants.EMPTY);
		txtAnsD.setText(Constants.EMPTY);
		choiceCorrectAnsLetter.getSelectionModel().select(0);
		sliderSkillLvl.setValue(1);
		txtMarks.setText(Constants.EMPTY);
		txtTimeRequired.setText(Constants.EMPTY);
	}

	/**
	 * Make answers for a question, given a list of possible answers and the position of the correct one. Shuffle at the
	 * end.
	 * 
	 * @param answersStr       - the list of possible answers
	 * @param correctAnswerPos - the position of the correct answer
	 * @return - list of Answer objects to be used in Question building
	 */
	private static List<Answer> makeAnswers(List<String> answersStr, int correctAnswerPos) {
		List<Answer> answers = new ArrayList<>();

		for (int i = 0; i < answersStr.size(); i++) {
			Answer answer = new AnswerBuilder().withValue(answersStr.get(i))
				.withIsCorrect(i == correctAnswerPos)
				.build();

			answers.add(answer);
		}
		Collections.shuffle(answers);

		return answers;
	}
}
