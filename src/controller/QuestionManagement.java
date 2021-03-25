package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.dto.QuestionDTO;
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

/**
 * Allows the user to view all stored questions, and manage them.
 *
 * @author Sam Barba
 */
public class QuestionManagement {

	private static Stage stage;

	private static List<QuestionDTO> questionDTOs;

	private static Label lblSelectQuestion = new Label();

	private static TableView tblQuestions = new TableView();

	private static List<CheckBox> cbSubjects;

	private static List<CheckBox> cbSkillLvls;

	private static Accordion accFilters;

	private static List<Integer> subjectIdFilters = new ArrayList<>();

	private static List<Integer> skillLvlFilters = new ArrayList<>();

	private static TextArea txtAreaQuestion = new TextArea();

	/**
	 * Display all questions and capability to filter and modify them.
	 */
	public static void display() {
		stage = new Stage();
		accFilters = new Accordion();

		Label lblSelectFilters = new Label("Select filters to apply?");

		Button btnAddQuestion = new ButtonBuilder().withWidth(150)
			.withUserAction(UserAction.ADD_NEW_QUESTION)
			.withActionEvent(e -> {
				// if added a new question, refresh questions TableView
				if (AddQuestion.display()) {
					refreshQuestionsTbl();
					SystemNotification.display(SystemNotificationType.SUCCESS, "Question added!");
				}
			})
			.build();
		Button btnDelQuestion = new ButtonBuilder().withWidth(150)
			.withUserAction(UserAction.DELETE_QUESTION)
			.withActionEvent(e -> {
				deleteQuestion();
			})
			.build();

		VBox vboxTbl = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.TOP_CENTER)
			.withSpacing(10)
			.withNodes(lblSelectQuestion, tblQuestions)
			.build();
		VBox vboxFilter = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.TOP_CENTER)
			.withSpacing(10)
			.withNodes(lblSelectFilters, accFilters)
			.build();
		HBox hbox1 = (HBox) new PaneBuilder().withBoxType(BoxType.HBOX)
			.withAlignment(Pos.TOP_CENTER)
			.withSpacing(20)
			.withNodes(vboxTbl, vboxFilter)
			.build();
		VBox vboxBtns = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(10)
			.withNodes(btnAddQuestion, btnDelQuestion, new ButtonBuilder().buildExitBtn(308, 136))
			.build();
		HBox hbox2 = (HBox) new PaneBuilder().withBoxType(BoxType.HBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(30)
			.withNodes(txtAreaQuestion, vboxBtns)
			.build();
		VBox vboxMain = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(30)
			.withNodes(LogoMaker.makeLogo(300), hbox1, hbox2)
			.build();

		setup();

		Scene scene = new Scene(vboxMain, 1400, 900);
		scene.getStylesheets().add(Constants.CSS_STYLE_PATH);
		stage.setScene(scene);
		stage.setTitle("Question Management");
		stage.setResizable(false);
		// so multiple instances of this window can't be opened
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
	}

	/**
	 * Delete selected question with confirmation.
	 */
	private static void deleteQuestion() {
		if (tblQuestions.getSelectionModel().getSelectedItems().size() != 1) {
			SystemNotification.display(SystemNotificationType.ERROR, "Please select 1 question.");
		} else if (UserConfirmation.confirm(SystemNotificationType.CONFIRM_DELETION, "question")) {
			QuestionDTO questionDto = (QuestionDTO) tblQuestions.getSelectionModel().getSelectedItem();
			QuestionService.getInstance().deleteQuestionById(questionDto.getId());
			refreshQuestionsTbl();
			SystemNotification.display(SystemNotificationType.SUCCESS, "Question deleted.");
		}
	}

	/**
	 * Set up window.
	 */
	private static void setup() {
		/*
		 * Set up TableView of questions
		 */
		TableColumn<QuestionDTO, Integer> colId = new TableColumn<>("ID");
		TableColumn<QuestionDTO, String> colSubjectTitle = new TableColumn<>("Subject");
		TableColumn<QuestionDTO, String> colStatement = new TableColumn<>("Statement");
		TableColumn<QuestionDTO, String> colSkillLevel = new TableColumn<>("Skill level");
		TableColumn<QuestionDTO, Integer> colMarks = new TableColumn<>("Marks");
		TableColumn<QuestionDTO, Integer> colTimeRequired = new TableColumn<>("Time required (mins)");
		TableColumn<QuestionDTO, String> colDateCreated = new TableColumn<>("Date created");

		colId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colSubjectTitle.setCellValueFactory(new PropertyValueFactory<>("subjectTitle"));
		colStatement.setCellValueFactory(new PropertyValueFactory<>("statement"));
		colSkillLevel.setCellValueFactory(new PropertyValueFactory<>("skillLevel"));
		colMarks.setCellValueFactory(new PropertyValueFactory<>("marks"));
		colTimeRequired.setCellValueFactory(new PropertyValueFactory<>("timeRequiredMins"));
		colDateCreated.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));

		colId.setPrefWidth(50);
		colSubjectTitle.setPrefWidth(200);
		colStatement.setPrefWidth(250);
		colSkillLevel.setPrefWidth(200);
		colMarks.setPrefWidth(70);
		colTimeRequired.setPrefWidth(180);
		colDateCreated.setPrefWidth(150);

		tblQuestions.getColumns()
			.addAll(colId, colSubjectTitle, colStatement, colSkillLevel, colMarks, colTimeRequired, colDateCreated);
		tblQuestions.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		tblQuestions.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			QuestionDTO questionDto = (QuestionDTO) tblQuestions.getSelectionModel().getSelectedItem();
			if (questionDto != null) {
				txtAreaQuestion.setText(QuestionService.getInstance().getTxtAreaQuestionStr(questionDto.getId()));
			}
		});
		tblQuestions.setPrefSize(1119, 300);
		tblQuestions.setEditable(false);

		/*
		 * Set up table filters accordion control
		 */
		cbSubjects = SubjectService.getInstance()
			.getAllSubjects()
			.stream()
			.map(subject -> new CheckBox(subject.toString()))
			.collect(Collectors.toList());

		// TableView of questions must be refreshed if these CheckBoxes are toggled
		for (CheckBox cb : cbSubjects) {
			cb.selectedProperty().addListener(new ChangeListener<Boolean>() {

				@Override
				public void changed(ObservableValue<? extends Boolean> obs, Boolean oldValue, Boolean newValue) {
					refreshQuestionsTbl();
				}
			});
		}

		VBox vboxSubjects = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.CENTER_LEFT)
			.withSpacing(3)
			.build();
		vboxSubjects.getChildren().addAll(cbSubjects);
		TitledPane tPaneSubjects = new TitledPane("Filter by subject", vboxSubjects);

		cbSkillLvls = Arrays.stream(SkillLevel.values())
			.map(skillLvl -> new CheckBox(skillLvl.getDisplayStr()))
			.collect(Collectors.toList());

		// TableView of questions must be refreshed if these CheckBoxes are toggled
		for (CheckBox cb : cbSkillLvls) {
			cb.selectedProperty().addListener(new ChangeListener<Boolean>() {

				@Override
				public void changed(ObservableValue<? extends Boolean> obs, Boolean oldValue, Boolean newValue) {
					refreshQuestionsTbl();
				}
			});
		}

		VBox vboxskillLvls = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.CENTER_LEFT)
			.withSpacing(3)
			.build();
		vboxskillLvls.getChildren().addAll(cbSkillLvls);
		TitledPane tPaneSkillLvls = new TitledPane("Filter by skill level", vboxskillLvls);

		accFilters.getPanes().addAll(tPaneSubjects, tPaneSkillLvls);

		/*
		 * Set up question info TextArea
		 */
		txtAreaQuestion.setEditable(false);
		txtAreaQuestion.setPrefSize(700, 350);
		txtAreaQuestion.setText("No question selected.");

		refreshQuestionsTbl();
	}

	/**
	 * Refresh TableView of questions.
	 */
	private static void refreshQuestionsTbl() {
		subjectIdFilters = cbSubjects.stream()
			.filter(CheckBox::isSelected)
			.map(cb -> SubjectService.getInstance().getSubjectIdFromDisplayStr(cb.getText()))
			.collect(Collectors.toList());

		skillLvlFilters = cbSkillLvls.stream()
			.filter(CheckBox::isSelected)
			.map(cb -> SkillLevel.getIntFromDisplayStr(cb.getText()))
			.collect(Collectors.toList());

		questionDTOs = QuestionService.getInstance().getQuestionDTOsWithFilters(skillLvlFilters, subjectIdFilters);
		tblQuestions.getItems().clear();
		tblQuestions.getItems().addAll(questionDTOs);

		if (questionDTOs.isEmpty()) {
			lblSelectQuestion.setText("No questions to show.");
		} else {
			lblSelectQuestion.setText("Select one of " + questionDTOs.size() + " questions to view!");
		}
		txtAreaQuestion.setText("No question selected.");
	}
}
