package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.persisted.QuestionPaper;
import model.persisted.Subject;
import model.questionpapergeneration.QuestionPaperGenerator;
import model.service.QuestionPaperService;
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
 * Allows the user to generate a question paper with specified parameters such as subject and skill level.
 *
 * @author Sam Barba
 */
public class GenerateQuestionPaper {

	private static Stage stage;

	private static boolean generated;

	private static ChoiceBox choiceSubject = new ChoiceBox();

	private static TextField txtTitle = new TextField();

	private static TextField txtCourseTitle = new TextField();

	private static TextField txtCourseCode = new TextField();

	private static Slider sliderSkillLvl = new Slider();

	private static Label lblSelectedSkillLvl = new Label("Skill level: KNOWLEDGE");

	private static Slider sliderTimeReqMins = new Slider();

	private static Label lblSelectedTimeReq = new Label("Approx. time required: 60 minutes");

	/**
	 * Return whether a paper has been generated successfully or not.
	 * 
	 * @return whether or not the paper has been generated successfully
	 */
	public static boolean generatePaper() {
		stage = new Stage();
		generated = false;

		Label lblSelectSubject = new Label("Select the subject:");
		Label lblEnterTitle = new Label("Enter the paper title:");
		Label lblEnterCourseTitle = new Label("Enter the course title:");
		Label lblEnterCourseCode = new Label("Enter the course code:");
		Label lblSelectSkillLvl = new Label("Select approx. paper skill level\n(based on Bloom's taxonomy):");
		Label lblSelectTimeReq = new Label("Select approx. time required (mins):");

		Button btnGenerate = new ButtonBuilder().withWidth(120)
			.withUserAction(UserAction.GENERATE)
			.withActionEvent(e -> {
				Optional<QuestionPaper> generatedPaper = prepareParamsAndGenerate();
				if (generatedPaper.isPresent()) {
					QuestionPaperService.getInstance().addQuestionPaper(generatedPaper.get());
					generated = true;
					stage.close();
				}
			})
			.build();

		VBox vbox1 = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.TOP_LEFT)
			.withSpacing(10)
			.withNodes(lblSelectSubject, choiceSubject, lblEnterTitle, txtTitle, lblEnterCourseTitle, txtCourseTitle,
				lblEnterCourseCode, txtCourseCode)
			.build();
		VBox vbox2 = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.TOP_LEFT)
			.withSpacing(10)
			.withNodes(lblSelectSkillLvl, sliderSkillLvl, lblSelectedSkillLvl, lblSelectTimeReq, sliderTimeReqMins,
				lblSelectedTimeReq, btnGenerate)
			.build();
		HBox hbox = (HBox) new PaneBuilder().withBoxType(BoxType.HBOX)
			.withAlignment(Pos.TOP_CENTER)
			.withSpacing(20)
			.withNodes(vbox1, vbox2)
			.build();
		VBox vboxMain = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(30)
			.withNodes(LogoMaker.makeLogo(300), hbox)
			.build();

		setup();

		Scene scene = new Scene(vboxMain, 550, 550);
		scene.getStylesheets().add(Constants.CSS_STYLE_PATH);
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
	private static Optional<QuestionPaper> prepareParamsAndGenerate() {
		String title = StringFormatter.formatTitle(txtTitle.getText());
		String courseTitle = StringFormatter.formatTitle(txtCourseTitle.getText());
		String courseCode = txtCourseCode.getText();

		if (title.length() == 0 || courseTitle.length() == 0 || courseCode.length() == 0) {
			SystemNotification.display(SystemNotificationType.ERROR,
				"Please enter the title, course title and course code.");
			return Optional.empty();
		} else if (!title.matches(Constants.TITLE_REGEX) || !courseTitle.matches(Constants.TITLE_REGEX)
			|| !courseCode.matches(Constants.TITLE_REGEX)) {
			SystemNotification.display(SystemNotificationType.ERROR,
				"Titles and codes must be only alphanumeric, and no repeating spaces.");
			return Optional.empty();
		}

		int subjectId = SubjectService.getInstance()
			.getSubjectIdFromDisplayStr(choiceSubject.getSelectionModel().getSelectedItem().toString());

		SkillLevel skillLevel = SkillLevel.getFromInt((int) sliderSkillLvl.getValue());
		int timeReq = (int) sliderTimeReqMins.getValue();

		/*
		 * REMOVE THIS WHEN REMOVING FILE WRITING CODE
		 */
		Optional<QuestionPaper> generatedPaper = Optional.empty();
		try {
			generatedPaper = QuestionPaperGenerator.getInstance()
				.generatePaper(subjectId, title, courseTitle, courseCode, skillLevel, timeReq);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return generatedPaper;
	}

	/**
	 * Set up window.
	 */
	private static void setup() {
		txtTitle.setText(Constants.EMPTY);
		txtCourseTitle.setText(Constants.EMPTY);
		txtCourseCode.setText(Constants.EMPTY);

		choiceSubject.getItems().clear();
		choiceSubject.getItems()
			.addAll(SubjectService.getInstance()
				.getAllSubjects()
				.stream()
				.map(Subject::toString)
				.collect(Collectors.toList()));
		choiceSubject.getSelectionModel().select(0);
		choiceSubject.setPrefWidth(200);

		List<SkillLevel> allSkillLvls = new ArrayList<>(EnumSet.allOf(SkillLevel.class));
		sliderSkillLvl.setMin(1);
		sliderSkillLvl.setMax(allSkillLvls.size());
		sliderSkillLvl.setPrefWidth(200);
		sliderSkillLvl.setMajorTickUnit(1);
		sliderSkillLvl.setShowTickLabels(true);
		sliderSkillLvl.setShowTickMarks(true);
		sliderSkillLvl.setValue(1);
		sliderSkillLvl.valueProperty().addListener((obs, oldValue, newValue) -> {
			int intVal = newValue.intValue();
			sliderSkillLvl.setValue(intVal); // snap to exact value
			lblSelectedSkillLvl.setText("Skill level: " + allSkillLvls.get(intVal - 1).getStrVal());
		});
		lblSelectedSkillLvl.setPrefWidth(240);

		sliderTimeReqMins.setMin(60);
		sliderTimeReqMins.setMax(180);
		sliderTimeReqMins.setPrefWidth(200);
		sliderTimeReqMins.setMajorTickUnit(15);
		sliderTimeReqMins.setShowTickLabels(true);
		sliderTimeReqMins.setShowTickMarks(true);
		sliderTimeReqMins.setValue(60);
		sliderTimeReqMins.valueProperty().addListener((obs, oldValue, newValue) -> {
			int intVal = (int) (15 * Math.round(newValue.doubleValue() / 15));
			sliderTimeReqMins.setValue(intVal); // snap to nearest 15 mins
			lblSelectedTimeReq.setText("Approx. time required: " + intVal + " minutes");
		});
		lblSelectedTimeReq.setPrefWidth(240);
	}
}
