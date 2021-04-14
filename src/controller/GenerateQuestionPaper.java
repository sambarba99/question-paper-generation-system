package controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import model.persisted.Question;
import model.persisted.QuestionPaper;
import model.persisted.Subject;
import model.questionpapergeneration.QuestionPaperGenerator;

import view.builders.ButtonBuilder;
import view.builders.PaneBuilder;
import view.enums.BloomSkillLevel;
import view.enums.BoxType;
import view.enums.SystemNotificationType;
import view.enums.UserAction;
import view.utils.Constants;
import view.utils.LogoMaker;
import view.utils.StringFormatter;

/**
 * Allows the user to generate a question paper with specified parameters such as subject and skill
 * level.
 *
 * @author Sam Barba
 */
public class GenerateQuestionPaper extends UIController {

	private static boolean generated;

	private static ChoiceBox choiceSubject = new ChoiceBox();

	private static TextField txtTitle = new TextField();

	private static TextField txtCourseTitle = new TextField();

	private static TextField txtCourseCode = new TextField();

	private static Slider sliderSkillLvl = new Slider();

	private static Label lblSelectedSkillLvl = new Label("Skill level: Knowledge");

	private static Slider sliderMinutesRequired = new Slider();

	private static Label lblSelectedMinsRequired = new Label("Approx. duration: 60 minutes");

	/**
	 * Return whether a paper has been generated successfully or not.
	 * 
	 * @return whether or not the paper has been generated successfully
	 */
	public static boolean generatePaper() {
		generated = false;

		Label lblSelectSubject = new Label("Select the subject:");
		Label lblEnterTitle = new Label("Enter the paper title:");
		Label lblEnterCourseTitle = new Label("Enter the course title:");
		Label lblEnterCourseCode = new Label("Enter the course code:");
		Label lblSelectSkillLvl = new Label("Select approx. paper skill level\n(based on Bloom's taxonomy):");
		Label lblSelectMinsRequired = new Label("Select approx. duration (mins):");

		Button btnGenerate = new ButtonBuilder()
			.withWidth(120)
			.withUserAction(UserAction.GENERATE)
			.withActionEvent(e -> {
				Optional<QuestionPaper> generatedPaper = prepareParamsAndGenerate();
				if (generatedPaper.isPresent()) {
					questionPaperService.addQuestionPaper(generatedPaper.get());
					generated = true;
					stage.close();
				}
			})
			.build();

		VBox vbox1 = (VBox) new PaneBuilder(BoxType.VBOX)
			.withAlignment(Pos.TOP_LEFT)
			.withSpacing(10)
			.withNodes(lblSelectSubject, choiceSubject, lblEnterTitle, txtTitle, lblEnterCourseTitle, txtCourseTitle,
				lblEnterCourseCode, txtCourseCode)
			.build();
		VBox vbox2 = (VBox) new PaneBuilder(BoxType.VBOX)
			.withAlignment(Pos.TOP_LEFT)
			.withSpacing(10)
			.withNodes(lblSelectSkillLvl, sliderSkillLvl, lblSelectedSkillLvl, lblSelectMinsRequired,
				sliderMinutesRequired, lblSelectedMinsRequired, btnGenerate)
			.build();
		HBox hbox = (HBox) new PaneBuilder(BoxType.HBOX)
			.withAlignment(Pos.TOP_CENTER)
			.withSpacing(20)
			.withNodes(vbox1, vbox2)
			.build();
		VBox root = (VBox) new PaneBuilder(BoxType.VBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(30)
			.withNodes(LogoMaker.makeLogo(300), hbox)
			.build();

		setup();

		Scene scene = new Scene(root, 550, 550);
		scene.getStylesheets().add(Constants.CSS_STYLE_PATH);
		stage.setScene(scene);
		stage.setTitle("Generate Question Paper");
		stage.setResizable(false);
		stage.showAndWait();
		return generated;
	}

	/**
	 * Generate a paper and add it via QuestionPaperService.
	 * 
	 * @return whether or not paper has been generated successfully
	 */
	private static Optional<QuestionPaper> prepareParamsAndGenerate() {
		String title = txtTitle.getText().trim();
		String courseTitle = txtCourseTitle.getText().trim();
		String courseCode = txtCourseCode.getText().trim();

		if (title.isEmpty() || courseTitle.isEmpty() || courseCode.isEmpty()) {
			SystemNotification.display(SystemNotificationType.ERROR,
				"Please enter the title, course title and course code.");
			return Optional.empty();
		}

		title = StringFormatter.formatTitle(txtTitle.getText());
		courseTitle = StringFormatter.formatTitle(txtCourseTitle.getText());
		courseCode = txtCourseCode.getText();

		if (!title.matches(Constants.TITLE_REGEX) || !courseTitle.matches(Constants.TITLE_REGEX)
			|| !courseCode.matches(Constants.TITLE_REGEX)) {
			SystemNotification.display(SystemNotificationType.ERROR,
				"Titles and codes must be only alphanumeric, and no repeating spaces.");
			return Optional.empty();
		}

		int subjectId = subjectService.getSubjectIdFromDisplayStr(choiceSubject.getSelectionModel()
			.getSelectedItem().toString());

		BloomSkillLevel skillLevel = BloomSkillLevel.getFromInt((int) sliderSkillLvl.getValue());
		int minsRequired = (int) sliderMinutesRequired.getValue();

		/*
		 * Get questions by user-selected subject, so initially 'fit' questions are already
		 * identified here
		 */
		List<Question> questions = questionService.getQuestionsBySubjectId(subjectId);

		if (questions.size() < Constants.MIN_QUESTIONS_PER_PAPER) {
			SystemNotification.display(SystemNotificationType.ERROR,
				"Insufficient questions of this subject:\n"
					+ subjectService.getSubjectById(subjectId).get().toString()
					+ "\nSubjects require at least " + Constants.MIN_QUESTIONS_PER_PAPER
					+ " questions to generate a paper.");

			return Optional.empty();
		}

		try {
			SystemNotification.display(SystemNotificationType.NEUTRAL, "Generating...\nPlease wait.");
			return QuestionPaperGenerator.getInstance().generatePaper(questions, subjectId, title,
				courseTitle, courseCode, skillLevel, minsRequired);
		} catch (IOException e) {
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + e.getClass().getName() + "\nIn: " + GenerateQuestionPaper.class.getName());
			return Optional.empty();
		}
	}

	/**
	 * Set up window.
	 */
	private static void setup() {
		stage = new Stage();
		txtTitle.clear();
		txtCourseTitle.clear();
		txtCourseCode.clear();

		choiceSubject.getItems().setAll(subjectService.getAllSubjects().stream()
			.map(Subject::toString)
			.collect(Collectors.toList()));
		choiceSubject.getSelectionModel().selectFirst();
		choiceSubject.setPrefWidth(200);

		sliderSkillLvl.setMin(1);
		sliderSkillLvl.setMax(BloomSkillLevel.values().length);
		sliderSkillLvl.setPrefWidth(200);
		sliderSkillLvl.setMajorTickUnit(1);
		sliderSkillLvl.setShowTickLabels(true);
		sliderSkillLvl.setShowTickMarks(true);
		sliderSkillLvl.setValue(1);
		sliderSkillLvl.valueProperty().addListener((obs, oldValue, newValue) -> {
			int intVal = newValue.intValue();
			sliderSkillLvl.setValue(intVal); // snap to exact value
			lblSelectedSkillLvl.setText("Skill level: " + BloomSkillLevel.values()[intVal - 1].toString());
		});
		lblSelectedSkillLvl.setPrefWidth(240);

		sliderMinutesRequired.setMin(60);
		sliderMinutesRequired.setMax(180);
		sliderMinutesRequired.setPrefWidth(200);
		sliderMinutesRequired.setMajorTickUnit(15);
		sliderMinutesRequired.setShowTickLabels(true);
		sliderMinutesRequired.setShowTickMarks(true);
		sliderMinutesRequired.setValue(60);
		sliderMinutesRequired.valueProperty().addListener((obs, oldValue, newValue) -> {
			int intVal = (int) (15 * Math.round(newValue.doubleValue() / 15));
			sliderMinutesRequired.setValue(intVal); // snap to nearest 15 mins
			lblSelectedMinsRequired.setText("Approx. duration: " + intVal + " minutes");
		});
		lblSelectedMinsRequired.setPrefWidth(240);
	}
}
