package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.dto.QuestionPaperDTO;
import model.dto.SubjectDTO;
import model.persisted.QuestionPaper;
import model.persisted.User;
import model.service.QuestionPaperService;
import model.service.QuestionService;
import model.service.SubjectService;

import view.SystemNotification;
import view.builders.ButtonBuilder;
import view.builders.PaneBuilder;
import view.enums.BoxType;
import view.enums.SystemNotificationType;
import view.enums.UserAction;
import view.utils.Constants;
import view.utils.LogoMaker;

/**
 * Allows the user to view and modify academic material.
 *
 * @author Sam Barba
 */
public class AcademicMaterialManagement {

	private static Stage stage;

	private static List<SubjectDTO> subjectDTOs;

	private static List<QuestionPaperDTO> questionPaperDTOs;

	private static TableView tblSubjects = new TableView();

	private static TableView tblQuestionPapers = new TableView();

	private static List<Integer> subjectIdFilters = new ArrayList<>();

	private static boolean paperSubjectFilterOn;

	private static Label lblQuestionPapers = new Label();

	/**
	 * Display academic material and related user actions.
	 * 
	 * @param currentUser - the user currently in session
	 */
	public static void display(User currentUser) {
		stage = new Stage();

		Label lblHeader = new Label("Academic Material Management");
		Label lblSubjects = new Label("Subjects");
		Label lblActions = new Label("Actions");

		Button btnAddSubject = new ButtonBuilder().withWidth(Constants.ACADEMIC_MATERIAL_BTN_WIDTH)
			.withUserAction(UserAction.ADD_NEW_SUBJECT)
			.withActionEvent(e -> {
				performAction(UserAction.ADD_NEW_SUBJECT, currentUser);
			})
			.build();
		Button btnDelSubject = new ButtonBuilder().withWidth(Constants.ACADEMIC_MATERIAL_BTN_WIDTH)
			.withUserAction(UserAction.DELETE_SUBJECT)
			.withActionEvent(e -> {
				performAction(UserAction.DELETE_SUBJECT, currentUser);
			})
			.build();
		Button btnQuestionManagement = new ButtonBuilder().withWidth(Constants.ACADEMIC_MATERIAL_BTN_WIDTH)
			.withUserAction(UserAction.OPEN_QUESTION_MANAGEMENT)
			.withActionEvent(e -> {
				performAction(UserAction.OPEN_QUESTION_MANAGEMENT, currentUser);
			})
			.build();
		Button btnGeneratePaper = new ButtonBuilder().withWidth(Constants.ACADEMIC_MATERIAL_BTN_WIDTH)
			.withUserAction(UserAction.GENERATE_QUESTION_PAPER)
			.withActionEvent(e -> {
				performAction(UserAction.GENERATE_QUESTION_PAPER, currentUser);
			})
			.build();
		Button btnViewPaper = new ButtonBuilder().withWidth(Constants.ACADEMIC_MATERIAL_BTN_WIDTH)
			.withUserAction(UserAction.VIEW_QUESTION_PAPER)
			.withActionEvent(e -> {
				performAction(UserAction.VIEW_QUESTION_PAPER, currentUser);
			})
			.build();
		Button btnDelPaper = new ButtonBuilder().withWidth(Constants.ACADEMIC_MATERIAL_BTN_WIDTH)
			.withUserAction(UserAction.DELETE_QUESTION_PAPER)
			.withActionEvent(e -> {
				performAction(UserAction.DELETE_QUESTION_PAPER, currentUser);
			})
			.build();
		Button btnToggleFilter = new ButtonBuilder().withWidth(Constants.ACADEMIC_MATERIAL_BTN_WIDTH)
			.withUserAction(UserAction.TOGGLE_FILTER_PAPERS)
			.withActionEvent(e -> {
				performAction(UserAction.TOGGLE_FILTER_PAPERS, currentUser);
			})
			.build();
		Button btnUpdatePassword = new ButtonBuilder().withWidth(Constants.ACADEMIC_MATERIAL_BTN_WIDTH)
			.withUserAction(UserAction.UPDATE_PASSWORD)
			.withActionEvent(e -> {
				performAction(UserAction.UPDATE_PASSWORD, currentUser);
			})
			.build();

		lblHeader.setStyle("-fx-font-size: 30px");

		VBox vboxSubjects = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.TOP_CENTER)
			.withSpacing(10)
			.withNodes(lblSubjects, tblSubjects)
			.build();
		VBox vboxQuestionPapers = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.TOP_CENTER)
			.withSpacing(10)
			.withNodes(lblQuestionPapers, tblQuestionPapers)
			.build();
		VBox vboxActions = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.TOP_CENTER)
			.withSpacing(10)
			.withNodes(lblActions, btnAddSubject, btnDelSubject, btnQuestionManagement, btnGeneratePaper, btnViewPaper,
				btnDelPaper, btnToggleFilter, btnUpdatePassword, new ButtonBuilder().buildExitBtn(117, 17))
			.build();
		HBox hboxViews = (HBox) new PaneBuilder().withBoxType(BoxType.HBOX)
			.withAlignment(Pos.TOP_CENTER)
			.withSpacing(20)
			.withNodes(vboxSubjects, vboxQuestionPapers, vboxActions)
			.build();
		VBox vboxMain = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(30)
			.withNodes(LogoMaker.makeLogo(300), lblHeader, hboxViews)
			.build();

		setup();

		Scene scene = new Scene(vboxMain, 1650, 680);
		scene.getStylesheets().add(Constants.CSS_STYLE_PATH);
		stage.setScene(scene);
		stage.setTitle("Academic Material");
		stage.setResizable(false);
		// so multiple instances of this window can't be opened
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
	}

	/**
	 * Perform the selected user action.
	 * 
	 * @param userAction  - the selected action to perform
	 * @param currentUser - the user currently in session
	 */
	private static void performAction(UserAction userAction, User currentUser) {
		switch (userAction) {
			case ADD_NEW_SUBJECT:
				if (AddSubject.addSubject()) {
					// if added a new subject, refresh subjects TableView
					refreshSubjectsTbl();
					SystemNotification.display(SystemNotificationType.SUCCESS, "Subject added!");
				}
				break;
			case DELETE_SUBJECT:
				if (tblSubjects.getSelectionModel().getSelectedItems().size() != 1) {
					SystemNotification.display(SystemNotificationType.ERROR, "Please select 1 subject.");
				} else if (UserConfirmation.confirm(SystemNotificationType.CONFIRM_DELETION, "subject")) {
					SubjectDTO subjectDto = (SubjectDTO) tblSubjects.getSelectionModel().getSelectedItem();
					SubjectService.getInstance().deleteSubjectById(subjectDto.getId());
					refreshSubjectsTbl();
					refreshQuestionPapersTbl();
					SystemNotification.display(SystemNotificationType.SUCCESS,
						"Subject '" + subjectDto.getTitle() + "' deleted.");
				}
				break;
			case TOGGLE_FILTER_PAPERS:
				if (!paperSubjectFilterOn) {
					ObservableList<SubjectDTO> selectedSubjects = tblSubjects.getSelectionModel().getSelectedItems();

					if (selectedSubjects.isEmpty()) {
						SystemNotification.display(SystemNotificationType.ERROR,
							"Please select subjects to filter by.");
					} else {
						subjectIdFilters = selectedSubjects.stream()
							.map(SubjectDTO::getId)
							.collect(Collectors.toList());

						paperSubjectFilterOn = true;
						lblQuestionPapers.setText("Question Papers (subject filters: ON)");
					}
				} else {
					subjectIdFilters.clear();
					paperSubjectFilterOn = false;
					lblQuestionPapers.setText("Question Papers (subject filters: OFF)");
				}
				refreshQuestionPapersTbl();
				break;
			case OPEN_QUESTION_MANAGEMENT:
				if (SubjectService.getInstance().getAllSubjects().isEmpty()) {
					SystemNotification.display(SystemNotificationType.ERROR, "Add at least 1 subject first.");
				} else {
					QuestionManagement.display();
				}
				break;
			case GENERATE_QUESTION_PAPER:
				if (QuestionService.getInstance().getAllQuestions().isEmpty()) {
					SystemNotification.display(SystemNotificationType.ERROR, "Add some questions first.");
				} else if (GenerateQuestionPaper.generatePaper()) {
					refreshQuestionPapersTbl();
					SystemNotification.display(SystemNotificationType.SUCCESS,
						"Generated best possible paper! You can now view/export it.");
				}
				break;
			case VIEW_QUESTION_PAPER:
				if (tblQuestionPapers.getSelectionModel().getSelectedItems().size() != 1) {
					SystemNotification.display(SystemNotificationType.ERROR, "Please select 1 paper.");
				} else {
					QuestionPaperDTO questionPaperDto = (QuestionPaperDTO) tblQuestionPapers.getSelectionModel()
						.getSelectedItem();
					Optional<QuestionPaper> questionPaperOpt = QuestionPaperService.getInstance()
						.getQuestionPaperById(questionPaperDto.getId());

					if (!questionPaperOpt.isPresent()) {
						throw new IllegalArgumentException(
							"Invalid question paper ID passed: " + questionPaperDto.getId());
					}
					ViewQuestionPaper.display(questionPaperOpt.get());
				}
				break;
			case DELETE_QUESTION_PAPER:
				if (tblQuestionPapers.getSelectionModel().getSelectedItems().size() != 1) {
					SystemNotification.display(SystemNotificationType.ERROR, "Please select 1 paper.");
				} else if (UserConfirmation.confirm(SystemNotificationType.CONFIRM_DELETION, "question paper")) {
					QuestionPaperDTO questionPaperDto = (QuestionPaperDTO) tblQuestionPapers.getSelectionModel()
						.getSelectedItem();
					QuestionPaperService.getInstance().deleteQuestionPaperById(questionPaperDto.getId());
					refreshQuestionPapersTbl();
					SystemNotification.display(SystemNotificationType.SUCCESS, "Question paper deleted.");
				}
				break;
			case UPDATE_PASSWORD:
				UpdatePassword.updatePassword(currentUser);
				break;
			default:
				SystemNotification.display(SystemNotificationType.ERROR, "Unexpected error.");
		}
	}

	/**
	 * Set up window.
	 */
	private static void setup() {
		/*
		 * Set up TableView of subjects
		 */
		TableColumn<SubjectDTO, Integer> colSubjectId = new TableColumn<>("ID");
		TableColumn<SubjectDTO, String> colSubjectTitle = new TableColumn<>("Title");
		TableColumn<SubjectDTO, Integer> colNumQuestions = new TableColumn<>("No. questions");
		TableColumn<SubjectDTO, String> colSubjectDateCreated = new TableColumn<>("Date created");

		colSubjectId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colSubjectTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
		colNumQuestions.setCellValueFactory(new PropertyValueFactory<>("numQuestions"));
		colSubjectDateCreated.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));

		colSubjectId.setPrefWidth(50);
		colSubjectTitle.setPrefWidth(200);
		colNumQuestions.setPrefWidth(120);
		colSubjectDateCreated.setPrefWidth(150);

		tblSubjects.getColumns().clear();
		tblSubjects.getColumns().addAll(colSubjectId, colSubjectTitle, colNumQuestions, colSubjectDateCreated);
		tblSubjects.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tblSubjects.setPrefSize(522, 365);
		tblSubjects.setEditable(false);

		/*
		 * Set up TableView of question papers
		 */
		TableColumn<QuestionPaperDTO, Integer> colPaperId = new TableColumn<>("ID");
		TableColumn<QuestionPaperDTO, String> colPaperTitle = new TableColumn<>("Title");
		TableColumn<QuestionPaperDTO, String> colPaperSubject = new TableColumn<>("Subject");
		TableColumn<QuestionPaperDTO, String> colPaperCourse = new TableColumn<>("Course");
		TableColumn<QuestionPaperDTO, String> colPaperDateCreated = new TableColumn<>("Date created");

		colPaperId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colPaperTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
		colPaperSubject.setCellValueFactory(new PropertyValueFactory<>("subjectTitle"));
		colPaperCourse.setCellValueFactory(new PropertyValueFactory<>("course"));
		colPaperDateCreated.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));

		colPaperId.setPrefWidth(50);
		colPaperTitle.setPrefWidth(200);
		colPaperSubject.setPrefWidth(200);
		colPaperCourse.setPrefWidth(200);
		colPaperDateCreated.setPrefWidth(150);

		tblQuestionPapers.getColumns().clear();
		tblQuestionPapers.getColumns()
			.addAll(colPaperId, colPaperTitle, colPaperSubject, colPaperCourse, colPaperDateCreated);
		tblQuestionPapers.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		tblQuestionPapers.setPrefSize(800, 365);
		tblQuestionPapers.setEditable(false);

		refreshSubjectsTbl();
		refreshQuestionPapersTbl();

		paperSubjectFilterOn = false;
		lblQuestionPapers.setText("Question Papers (subject filters: OFF)");
	}

	/**
	 * Refresh TableView of subjects.
	 */
	private static void refreshSubjectsTbl() {
		subjectDTOs = SubjectService.getInstance().getAllSubjectDTOs();
		tblSubjects.getItems().clear();
		tblSubjects.getItems().addAll(subjectDTOs);
	}

	/**
	 * Refresh TableView of question papers.
	 */
	private static void refreshQuestionPapersTbl() {
		questionPaperDTOs = QuestionPaperService.getInstance().getQuestionPaperDTOsWithSubjectFilter(subjectIdFilters);
		tblQuestionPapers.getItems().clear();
		tblQuestionPapers.getItems().addAll(questionPaperDTOs);
	}
}
