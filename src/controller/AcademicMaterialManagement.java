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
import javafx.stage.Stage;

import model.dto.QuestionPaperDTO;
import model.dto.SubjectDTO;
import model.persisted.QuestionPaper;
import model.persisted.User;

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
public class AcademicMaterialManagement extends UIController {

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
		Label lblHeader = new Label("Academic Material Management");
		lblHeader.setStyle("-fx-font-size: 30px");

		Button btnAddSubject = new ButtonBuilder()
			.withWidth(Constants.ACADEMIC_MATERIAL_BTN_WIDTH)
			.withUserAction(UserAction.ADD_NEW_SUBJECT)
			.withActionEvent(e -> performAction(UserAction.ADD_NEW_SUBJECT, currentUser))
			.build();
		Button btnDelSubject = new ButtonBuilder()
			.withWidth(Constants.ACADEMIC_MATERIAL_BTN_WIDTH)
			.withUserAction(UserAction.DELETE_SUBJECT)
			.withActionEvent(e -> performAction(UserAction.DELETE_SUBJECT, currentUser))
			.build();
		Button btnQuestionManagement = new ButtonBuilder()
			.withWidth(Constants.ACADEMIC_MATERIAL_BTN_WIDTH)
			.withUserAction(UserAction.OPEN_QUESTION_MANAGEMENT)
			.withActionEvent(e -> performAction(UserAction.OPEN_QUESTION_MANAGEMENT, currentUser))
			.build();
		Button btnGeneratePaper = new ButtonBuilder()
			.withWidth(Constants.ACADEMIC_MATERIAL_BTN_WIDTH)
			.withUserAction(UserAction.GENERATE_QUESTION_PAPER)
			.withActionEvent(e -> performAction(UserAction.GENERATE_QUESTION_PAPER, currentUser))
			.build();
		Button btnViewPaper = new ButtonBuilder()
			.withWidth(Constants.ACADEMIC_MATERIAL_BTN_WIDTH)
			.withUserAction(UserAction.VIEW_QUESTION_PAPER)
			.withActionEvent(e -> performAction(UserAction.VIEW_QUESTION_PAPER, currentUser))
			.build();
		Button btnDelPaper = new ButtonBuilder()
			.withWidth(Constants.ACADEMIC_MATERIAL_BTN_WIDTH)
			.withUserAction(UserAction.DELETE_QUESTION_PAPER)
			.withActionEvent(e -> performAction(UserAction.DELETE_QUESTION_PAPER, currentUser))
			.build();
		Button btnToggleFilter = new ButtonBuilder()
			.withWidth(Constants.ACADEMIC_MATERIAL_BTN_WIDTH)
			.withUserAction(UserAction.TOGGLE_FILTER_PAPERS)
			.withActionEvent(e -> performAction(UserAction.TOGGLE_FILTER_PAPERS, currentUser))
			.build();
		Button btnChangePassword = new ButtonBuilder()
			.withWidth(Constants.ACADEMIC_MATERIAL_BTN_WIDTH)
			.withUserAction(UserAction.CHANGE_PASSWORD)
			.withActionEvent(e -> performAction(UserAction.CHANGE_PASSWORD, currentUser))
			.build();

		VBox vboxSubjects = (VBox) new PaneBuilder(BoxType.VBOX)
			.withAlignment(Pos.TOP_CENTER)
			.withSpacing(10)
			.withNodes(new Label("Subjects"), tblSubjects)
			.build();
		VBox vboxQuestionPapers = (VBox) new PaneBuilder(BoxType.VBOX)
			.withAlignment(Pos.TOP_CENTER)
			.withSpacing(10)
			.withNodes(lblQuestionPapers, tblQuestionPapers)
			.build();
		VBox vboxActions = (VBox) new PaneBuilder(BoxType.VBOX)
			.withAlignment(Pos.TOP_CENTER)
			.withSpacing(10)
			.withNodes(new Label("Actions"), btnAddSubject, btnDelSubject, btnQuestionManagement, btnGeneratePaper,
				btnViewPaper, btnDelPaper, btnToggleFilter, btnChangePassword,
				new ButtonBuilder().buildExitBtn(115, 17))
			.build();
		HBox hboxViews = (HBox) new PaneBuilder(BoxType.HBOX)
			.withAlignment(Pos.TOP_CENTER)
			.withSpacing(20)
			.withNodes(vboxSubjects, vboxQuestionPapers, vboxActions)
			.build();
		VBox root = (VBox) new PaneBuilder(BoxType.VBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(30)
			.withNodes(LogoMaker.makeLogo(300), lblHeader, hboxViews)
			.build();

		setup();

		Scene scene = new Scene(root, 1650, 680);
		scene.getStylesheets().add(Constants.CSS_STYLE_PATH);
		stage.setScene(scene);
		stage.setTitle("Academic Material");
		stage.setResizable(false);
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
				ObservableList<SubjectDTO> subjectDtos = tblSubjects.getSelectionModel().getSelectedItems();

				if (subjectDtos.isEmpty()) {
					SystemNotification.display(SystemNotificationType.ERROR, "Please select at least 1 subject.");
				} else if (UserConfirmation.confirm(SystemNotificationType.CONFIRM_DELETION)) {
					List<Integer> deleteIds = subjectDtos.stream()
						.map(SubjectDTO::getId)
						.collect(Collectors.toList());

					subjectService.deleteSubjectsByIds(deleteIds);
					refreshSubjectsTbl();
					refreshQuestionPapersTbl();
					SystemNotification.display(SystemNotificationType.SUCCESS, "Selected subjects deleted.");
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
				if (subjectService.getAllSubjects().isEmpty()) {
					SystemNotification.display(SystemNotificationType.ERROR, "Add at least 1 subject first.");
				} else if (QuestionManagement.display()) {
					/*
					 * If questions have been added/deleted, refresh subjects table (as this
					 * contains 'No. questions' column)
					 */
					refreshSubjectsTbl();
				}
				break;
			case GENERATE_QUESTION_PAPER:
				if (questionService.getAllQuestions().isEmpty()) {
					SystemNotification.display(SystemNotificationType.ERROR, "Add some questions first.");
				} else if (GenerateQuestionPaper.generatePaper()) {
					refreshQuestionPapersTbl();
					SystemNotification.display(SystemNotificationType.SUCCESS,
						"Generated best possible paper!\nYou can now view and export it.");
				}
				break;
			case VIEW_QUESTION_PAPER:
				if (tblQuestionPapers.getSelectionModel().getSelectedItems().size() != 1) {
					SystemNotification.display(SystemNotificationType.ERROR, "Please select 1 paper.");
				} else {
					QuestionPaperDTO questionPaperDto = (QuestionPaperDTO) tblQuestionPapers.getSelectionModel()
						.getSelectedItem();
					Optional<QuestionPaper> questionPaperOpt = questionPaperService
						.getQuestionPaperById(questionPaperDto.getId());

					ViewQuestionPaper.display(questionPaperOpt.get());
				}
				break;
			case DELETE_QUESTION_PAPER:
				ObservableList<QuestionPaperDTO> questionPaperDtos = tblQuestionPapers.getSelectionModel()
					.getSelectedItems();

				if (questionPaperDtos.isEmpty()) {
					SystemNotification.display(SystemNotificationType.ERROR, "Please select at least 1 paper.");
				} else if (UserConfirmation.confirm(SystemNotificationType.CONFIRM_DELETION)) {
					List<Integer> deleteIds = questionPaperDtos.stream()
						.map(QuestionPaperDTO::getId)
						.collect(Collectors.toList());

					questionPaperService.deleteQuestionPapersByIds(deleteIds);
					refreshQuestionPapersTbl();
					SystemNotification.display(SystemNotificationType.SUCCESS, "Selected papers deleted.");
				}
				break;
			case CHANGE_PASSWORD:
				ChangePassword.display(currentUser);
				break;
			default:
				SystemNotification.display(SystemNotificationType.ERROR, "Unexpected error.");
		}
	}

	/**
	 * Set up window.
	 */
	private static void setup() {
		stage = new Stage();

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

		tblSubjects.getColumns().setAll(colSubjectId, colSubjectTitle, colNumQuestions, colSubjectDateCreated);
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

		tblQuestionPapers.getColumns().setAll(colPaperId, colPaperTitle, colPaperSubject, colPaperCourse,
			colPaperDateCreated);
		tblQuestionPapers.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tblQuestionPapers.setPrefSize(802, 365);
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
		List<SubjectDTO> subjectDTOs = subjectService.getAllSubjectDTOs();
		tblSubjects.getItems().setAll(subjectDTOs);
	}

	/**
	 * Refresh TableView of question papers.
	 */
	private static void refreshQuestionPapersTbl() {
		List<QuestionPaperDTO> questionPaperDTOs = questionPaperService
			.getQuestionPaperDTOsWithSubjectFilter(subjectIdFilters);
		tblQuestionPapers.getItems().setAll(questionPaperDTOs);
	}
}
