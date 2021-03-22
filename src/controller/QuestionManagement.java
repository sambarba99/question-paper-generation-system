package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
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
import view.enums.DifficultyLevel;
import view.enums.SystemNotificationType;
import view.enums.UserAction;

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

	private static List<CheckBox> cbDifficultyLvls;

	private static Accordion accFilters = new Accordion();

	private static List<Integer> subjectIdFilters = new ArrayList<>();

	private static List<Integer> difficultyLvlFilters = new ArrayList<>();

	private static TextArea txtAreaQuestion = new TextArea();

	/**
	 * Display all questions and capability to filter and modify them.
	 */
	public static void display() {
		stage = new Stage();

		lblSelectQuestion.setStyle("-fx-font-size: 20px");
		Label lblSelectFilters = new Label("Select filters to apply?");

		Button btnAddQuestion = new ButtonBuilder().withWidth(150)
			.withUserAction(UserAction.ADD_NEW_QUESTION)
			.withClickAction(action -> {
				// if added a new question, refresh questions TableView
				if (AddQuestion.display()) {
					refreshQuestionsTbl();
					SystemNotification.display(SystemNotificationType.SUCCESS, "Question added!");
				}
			})
			.build();
		Button btnDelQuestion = new ButtonBuilder().withWidth(150)
			.withUserAction(UserAction.DELETE_QUESTION)
			.withClickAction(action -> {
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
			.withSpacing(30)
			.withNodes(vboxTbl, vboxFilter)
			.build();
		VBox vboxBtns = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(10)
			.withNodes(btnAddQuestion, btnDelQuestion)
			.build();
		HBox hbox2 = (HBox) new PaneBuilder().withBoxType(BoxType.HBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(30)
			.withNodes(txtAreaQuestion, vboxBtns)
			.build();
		VBox vboxMain = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(30)
			.withNodes(hbox1, hbox2)
			.build();

		FlowPane pane = new FlowPane();
		pane.getStyleClass().add("flow-pane");
		pane.getChildren().add(vboxMain);

		setup();

		Scene scene = new Scene(pane, 1450, 800);
		scene.getStylesheets().add("style.css");
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
		} else if (DeletionConfirm.confirmDelete("question")) {
			QuestionDTO questionDto = (QuestionDTO) tblQuestions.getSelectionModel().getSelectedItems().get(0);
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
		TableColumn<QuestionDTO, String> colDifficultyLevel = new TableColumn<>("Difficulty level");
		TableColumn<QuestionDTO, Integer> colMarks = new TableColumn<>("Marks");
		TableColumn<QuestionDTO, Integer> colTimeRequired = new TableColumn<>("Time required (mins)");
		TableColumn<QuestionDTO, String> colDateCreated = new TableColumn<>("Date created");

		colId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colSubjectTitle.setCellValueFactory(new PropertyValueFactory<>("subjectTitle"));
		colStatement.setCellValueFactory(new PropertyValueFactory<>("statement"));
		colDifficultyLevel.setCellValueFactory(new PropertyValueFactory<>("difficultyLevel"));
		colMarks.setCellValueFactory(new PropertyValueFactory<>("marks"));
		colTimeRequired.setCellValueFactory(new PropertyValueFactory<>("timeRequiredMins"));
		colDateCreated.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));

		colId.setPrefWidth(50);
		colSubjectTitle.setPrefWidth(200);
		colStatement.setPrefWidth(250);
		colDifficultyLevel.setPrefWidth(200);
		colMarks.setPrefWidth(70);
		colTimeRequired.setPrefWidth(180);
		colDateCreated.setPrefWidth(150);

		tblQuestions.getColumns()
			.addAll(colId, colSubjectTitle, colStatement, colDifficultyLevel, colMarks, colTimeRequired,
				colDateCreated);

		tblQuestions.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		tblQuestions.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			ObservableList<QuestionDTO> selectedItems = tblQuestions.getSelectionModel().getSelectedItems();
			if (selectedItems.size() > 0) {
				QuestionDTO questionDto = (QuestionDTO) selectedItems.get(0);
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

		cbDifficultyLvls = Arrays.asList(DifficultyLevel.values())
			.stream()
			.map(difficultyLvl -> new CheckBox(difficultyLvl.getDisplayStr()))
			.collect(Collectors.toList());

		// TableView of questions must be refreshed if these CheckBoxes are toggled
		for (CheckBox cb : cbDifficultyLvls) {
			cb.selectedProperty().addListener(new ChangeListener<Boolean>() {

				@Override
				public void changed(ObservableValue<? extends Boolean> obs, Boolean oldValue, Boolean newValue) {
					refreshQuestionsTbl();
				}
			});
		}

		VBox vboxDifficultyLvls = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.CENTER_LEFT)
			.withSpacing(3)
			.build();
		vboxDifficultyLvls.getChildren().addAll(cbDifficultyLvls);
		TitledPane tPaneDifficultyLvls = new TitledPane("Filter by difficulty", vboxDifficultyLvls);

		accFilters.getPanes().addAll(tPaneSubjects, tPaneDifficultyLvls);

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

		difficultyLvlFilters = cbDifficultyLvls.stream()
			.filter(CheckBox::isSelected)
			.map(cb -> DifficultyLevel.getIntFromDisplayStr(cb.getText()))
			.collect(Collectors.toList());

		questionDTOs = QuestionService.getInstance().getQuestionDTOsWithFilters(difficultyLvlFilters, subjectIdFilters);
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
