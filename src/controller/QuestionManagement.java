package controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
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
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import model.dto.QuestionDTO;

import view.builders.ButtonBuilder;
import view.builders.PaneBuilder;
import view.enums.BloomSkillLevel;
import view.enums.BoxType;
import view.enums.SystemNotificationType;
import view.enums.UserAction;
import view.utils.Constants;
import view.utils.LogoMaker;

/**
 * Allows the user to view all stored questions, and manage them.
 *
 * @author Sam Barba
 */
public class QuestionManagement extends UIController {

	private static boolean modified;

	private static Label lblSelectQuestion = new Label();

	private static TableView tblQuestions = new TableView();

	private static List<CheckBox> cbSubjects;

	private static List<CheckBox> cbSkillLvls;

	private static Accordion accFilters = new Accordion();

	private static TextField txtStatementSubstringFilter = new TextField();

	private static TextArea txtAreaQuestion = new TextArea();

	/**
	 * Display all questions and capability to filter and modify them.
	 */
	public static boolean display() {
		modified = false;

		Button btnAddQuestion = new ButtonBuilder()
			.withWidth(160)
			.withUserAction(UserAction.ADD_NEW_QUESTION)
			.withActionEvent(e -> {
				// if added a new question, refresh questions TableView
				if (AddQuestion.display()) {
					refreshQuestionsTbl();
					modified = true;
					SystemNotification.display(SystemNotificationType.SUCCESS, "Question added!");
				}
			})
			.build();
		Button btnDelQuestion = new ButtonBuilder()
			.withWidth(160)
			.withUserAction(UserAction.DELETE_QUESTION)
			.withActionEvent(e -> {
				ObservableList<QuestionDTO> questionDtos = tblQuestions.getSelectionModel().getSelectedItems();

				if (questionDtos.isEmpty()) {
					SystemNotification.display(SystemNotificationType.ERROR, "Please select at least 1 question.");
				} else if (UserConfirmation.confirm(SystemNotificationType.CONFIRM_DELETION)) {
					List<Integer> deleteIds = questionDtos.stream()
						.map(QuestionDTO::getId)
						.collect(Collectors.toList());

					questionService.deleteQuestionsByIds(deleteIds);
					refreshQuestionsTbl();
					modified = true;
					SystemNotification.display(SystemNotificationType.SUCCESS, "Selected questions deleted.");
				}
			})
			.build();

		VBox vboxTbl = (VBox) new PaneBuilder(BoxType.VBOX)
			.withAlignment(Pos.TOP_CENTER)
			.withSpacing(10)
			.withNodes(lblSelectQuestion, tblQuestions)
			.build();
		VBox vboxFilters = (VBox) new PaneBuilder(BoxType.VBOX)
			.withAlignment(Pos.TOP_CENTER)
			.withSpacing(10)
			.withNodes(new Label("Filter by statement?"), txtStatementSubstringFilter,
				new Label("Other filters:"), accFilters)
			.build();
		HBox hbox1 = (HBox) new PaneBuilder(BoxType.HBOX)
			.withAlignment(Pos.TOP_CENTER)
			.withSpacing(20)
			.withNodes(vboxTbl, vboxFilters)
			.build();
		VBox vboxBtns = (VBox) new PaneBuilder(BoxType.VBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(10)
			.withNodes(btnAddQuestion, btnDelQuestion, new ButtonBuilder().buildExitBtn(308, 136))
			.build();
		HBox hbox2 = (HBox) new PaneBuilder(BoxType.HBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(30)
			.withNodes(txtAreaQuestion, vboxBtns)
			.build();
		VBox root = (VBox) new PaneBuilder(BoxType.VBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(30)
			.withNodes(LogoMaker.makeLogo(300), hbox1, hbox2)
			.build();

		setup();

		Scene scene = new Scene(root, 1400, 900);
		scene.getStylesheets().add(Constants.CSS_STYLE_PATH);
		stage.setScene(scene);
		stage.setTitle("Question Management");
		stage.setResizable(false);
		stage.showAndWait();
		return modified;
	}

	/**
	 * Set up window.
	 */
	private static void setup() {
		stage = new Stage();

		/*
		 * Set up TableView of questions
		 */
		TableColumn<QuestionDTO, Integer> colId = new TableColumn<>("ID");
		TableColumn<QuestionDTO, String> colSubjectTitle = new TableColumn<>("Subject");
		TableColumn<QuestionDTO, String> colStatement = new TableColumn<>("Statement");
		TableColumn<QuestionDTO, String> colSkillLevel = new TableColumn<>("Bloom skill level");
		TableColumn<QuestionDTO, Integer> colMarks = new TableColumn<>("Marks");
		TableColumn<QuestionDTO, Integer> colMinsRequired = new TableColumn<>("Minutes required");
		TableColumn<QuestionDTO, String> colDateCreated = new TableColumn<>("Date created");

		colId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colSubjectTitle.setCellValueFactory(new PropertyValueFactory<>("subjectTitle"));
		colStatement.setCellValueFactory(new PropertyValueFactory<>("statement"));
		colSkillLevel.setCellValueFactory(new PropertyValueFactory<>("skillLevel"));
		colMarks.setCellValueFactory(new PropertyValueFactory<>("marks"));
		colMinsRequired.setCellValueFactory(new PropertyValueFactory<>("minutesRequired"));
		colDateCreated.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));

		colId.setPrefWidth(50);
		colSubjectTitle.setPrefWidth(200);
		colStatement.setPrefWidth(250);
		colSkillLevel.setPrefWidth(200);
		colMarks.setPrefWidth(70);
		colMinsRequired.setPrefWidth(180);
		colDateCreated.setPrefWidth(150);

		tblQuestions.getColumns().setAll(colId, colSubjectTitle, colStatement, colSkillLevel, colMarks, colMinsRequired,
			colDateCreated);
		tblQuestions.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tblQuestions.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			QuestionDTO questionDto = (QuestionDTO) tblQuestions.getSelectionModel().getSelectedItem();
			if (questionDto != null) {
				txtAreaQuestion.setText(questionService.getTxtAreaQuestionStr(questionDto.getId()));
			}
		});
		tblQuestions.setPrefSize(1119, 300);
		tblQuestions.setEditable(false);

		/*
		 * Set up table filters accordion control
		 */
		cbSubjects = subjectService.getAllSubjects().stream()
			.map(subject -> new CheckBox(subject.toString()))
			.collect(Collectors.toList());

		// TableView of questions must be refreshed if these CheckBoxes are toggled
		for (CheckBox cb : cbSubjects) {
			cb.getStyleClass().add("check-box-accordion");
			cb.selectedProperty().addListener(listener -> refreshQuestionsTbl());
		}

		VBox vboxSubjects = (VBox) new PaneBuilder(BoxType.VBOX)
			.withAlignment(Pos.CENTER_LEFT)
			.withSpacing(3)
			.build();
		vboxSubjects.getChildren().setAll(cbSubjects);

		cbSkillLvls = Arrays.stream(BloomSkillLevel.values())
			.map(skillLvl -> new CheckBox(skillLvl.toString()))
			.collect(Collectors.toList());

		// TableView of questions must be refreshed if these CheckBoxes are toggled
		for (CheckBox cb : cbSkillLvls) {
			cb.getStyleClass().add("check-box-accordion");
			cb.selectedProperty().addListener(listener -> refreshQuestionsTbl());
		}

		VBox vboxSkillLvls = (VBox) new PaneBuilder(BoxType.VBOX)
			.withAlignment(Pos.CENTER_LEFT)
			.withSpacing(3)
			.build();
		vboxSkillLvls.getChildren().setAll(cbSkillLvls);

		TitledPane tPaneSubjects = new TitledPane("Filter by subject", vboxSubjects);
		TitledPane tPaneSkillLvls = new TitledPane("Filter by skill level", vboxSkillLvls);
		accFilters.getPanes().setAll(tPaneSubjects, tPaneSkillLvls);

		txtStatementSubstringFilter.clear();
		txtStatementSubstringFilter.textProperty().addListener((obs, oldText, newText) -> {
			txtStatementSubstringFilter.setText(newText.toLowerCase());
			refreshQuestionsTbl();
		});

		txtAreaQuestion.setEditable(false);
		txtAreaQuestion.setPrefSize(700, 350);
		txtAreaQuestion.setText("No question selected.");

		refreshQuestionsTbl();
	}

	/**
	 * Refresh TableView of questions.
	 */
	private static void refreshQuestionsTbl() {
		List<Integer> subjectIdFilters = cbSubjects.stream()
			.filter(CheckBox::isSelected)
			.map(cb -> subjectService.getSubjectIdFromDisplayStr(cb.getText()))
			.collect(Collectors.toList());

		List<Integer> skillLvlFilters = cbSkillLvls.stream()
			.filter(CheckBox::isSelected)
			.map(cb -> BloomSkillLevel.getFromStr(cb.getText()).getIntVal())
			.collect(Collectors.toList());

		List<QuestionDTO> questionDTOs = questionService.getQuestionDTOsWithFilters(skillLvlFilters, subjectIdFilters,
			txtStatementSubstringFilter.getText());
		tblQuestions.getItems().setAll(questionDTOs);

		if (questionDTOs.isEmpty()) {
			lblSelectQuestion.setText("No questions to show.");
		} else {
			lblSelectQuestion.setText("Select one of " + questionDTOs.size() + " questions to view!");
		}
		txtAreaQuestion.setText("No question selected.");
	}
}
