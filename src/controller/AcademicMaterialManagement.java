package controller;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.FlowPane;
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

import view.BoxMaker;
import view.ButtonMaker;
import view.Constants;
import view.enums.BoxType;
import view.enums.SystemNotificationType;
import view.enums.UserAction;

/**
 * Allows the user to view and modify academic material.
 *
 * @author Sam Barba
 */
public class AcademicMaterialManagement {

	private static Stage stage;

	private static boolean paperSubjectFilterOn;

	private static Label lblPaperFilterStatus = new Label();

	private static ListView<String> listViewSubjects = new ListView<>();

	private static ListView<String> listViewQuestionPapers = new ListView<>();

	/**
	 * Display academic material and related options.
	 * 
	 * @param currentUser - the user currently in session
	 */
	public static void display(User currentUser) {
		stage = new Stage();

		Label lblHeader = new Label("Academic Material Management");
		Label lblSubjects = new Label("Subjects");
		Label lblQuestionPapers = new Label("Question Papers");
		Label lblActions = new Label("Actions");

		Button btnAddSubject = ButtonMaker.getInstance().makeButton(Constants.ACADEMIC_MATERIAL_BTN_WIDTH,
			Constants.BTN_HEIGHT, UserAction.ADD_NEW_SUBJECT, action -> {
				performAction(UserAction.ADD_NEW_SUBJECT, currentUser);
			});
		Button btnDelSubject = ButtonMaker.getInstance().makeButton(Constants.ACADEMIC_MATERIAL_BTN_WIDTH,
			Constants.BTN_HEIGHT, UserAction.DELETE_SUBJECT, action -> {
				performAction(UserAction.DELETE_SUBJECT, currentUser);
			});
		Button btnAllQuestions = ButtonMaker.getInstance().makeButton(Constants.ACADEMIC_MATERIAL_BTN_WIDTH,
			Constants.BTN_HEIGHT, UserAction.VIEW_MODIFY_ALL_QUESTIONS, action -> {
				performAction(UserAction.VIEW_MODIFY_ALL_QUESTIONS, currentUser);
			});
		Button btnGeneratePaper = ButtonMaker.getInstance().makeButton(Constants.ACADEMIC_MATERIAL_BTN_WIDTH,
			Constants.BTN_HEIGHT, UserAction.GENERATE_QUESTION_PAPER, action -> {
				performAction(UserAction.GENERATE_QUESTION_PAPER, currentUser);
			});
		Button btnViewPaper = ButtonMaker.getInstance().makeButton(Constants.ACADEMIC_MATERIAL_BTN_WIDTH,
			Constants.BTN_HEIGHT, UserAction.VIEW_QUESTION_PAPER, action -> {
				performAction(UserAction.VIEW_QUESTION_PAPER, currentUser);
			});
		Button btnDelPaper = ButtonMaker.getInstance().makeButton(Constants.ACADEMIC_MATERIAL_BTN_WIDTH,
			Constants.BTN_HEIGHT, UserAction.DELETE_QUESTION_PAPER, action -> {
				performAction(UserAction.DELETE_QUESTION_PAPER, currentUser);
			});
		Button btnToggleFilter = ButtonMaker.getInstance().makeButton(Constants.ACADEMIC_MATERIAL_BTN_WIDTH,
			Constants.BTN_HEIGHT, UserAction.TOGGLE_FILTER_PAPERS, action -> {
				performAction(UserAction.TOGGLE_FILTER_PAPERS, currentUser);
			});
		Button btnUpdatePassword = ButtonMaker.getInstance().makeButton(Constants.ACADEMIC_MATERIAL_BTN_WIDTH,
			Constants.BTN_HEIGHT, UserAction.UPDATE_PASSWORD, action -> {
				performAction(UserAction.UPDATE_PASSWORD, currentUser);
			});

		lblHeader.setStyle("-fx-font-size: 25px");

		BoxMaker boxMaker = BoxMaker.getInstance();
		VBox vboxSubjects = (VBox) boxMaker.makeBox(BoxType.VBOX, Pos.CENTER, 10, lblSubjects, listViewSubjects);
		VBox vboxQuestionPapers = (VBox) boxMaker.makeBox(BoxType.VBOX, Pos.CENTER, 10, lblQuestionPapers,
			listViewQuestionPapers);
		VBox vboxActions = (VBox) boxMaker.makeBox(BoxType.VBOX, Pos.TOP_CENTER, 10, lblActions, btnAddSubject,
			btnDelSubject, btnAllQuestions, btnGeneratePaper, btnViewPaper, btnDelPaper, btnToggleFilter,
			btnUpdatePassword, lblPaperFilterStatus);
		HBox hboxViews = (HBox) boxMaker.makeBox(BoxType.HBOX, Pos.CENTER, 20, vboxSubjects, vboxQuestionPapers,
			vboxActions);
		VBox vboxMain = (VBox) boxMaker.makeBox(BoxType.VBOX, Pos.CENTER, 20, lblHeader, hboxViews);

		FlowPane pane = new FlowPane();
		pane.getStyleClass().add("flow-pane");
		pane.getChildren().add(vboxMain);

		setup();

		Scene scene = new Scene(pane, 900, 550);
		scene.getStylesheets().add("style.css");
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
					listViewSubjects.getItems().clear();
					listViewSubjects.getItems().addAll(SubjectDTO.getInstance().getSubjectListViewItems());
					SystemNotification.display(SystemNotificationType.SUCCESS, "Subject added!");
				}
				break;
			case DELETE_SUBJECT:
				if (listViewSubjects.getSelectionModel().getSelectedItems().size() != 1) {
					SystemNotification.display(SystemNotificationType.ERROR, "Please select 1 subject.");
				} else if (DeletionConfirm.confirmDelete("subject")) {
					int subjectId = SubjectDTO.getInstance().getSelectedSubjectsIds(listViewSubjects).get(0);
					SubjectService.getInstance().deleteSubjectById(subjectId);
					listViewSubjects.getItems().clear();
					listViewSubjects.getItems().addAll(SubjectDTO.getInstance().getSubjectListViewItems());
					SystemNotification.display(SystemNotificationType.SUCCESS, "Subject deleted.");
				}
				break;
			case TOGGLE_FILTER_PAPERS:
				if (!paperSubjectFilterOn) {
					if (listViewSubjects.getSelectionModel().getSelectedItems().isEmpty()) {
						SystemNotification.display(SystemNotificationType.ERROR,
							"Please select subjects to filter by.");
					} else {
						paperSubjectFilterOn = true;
						List<Integer> subjectIds = SubjectDTO.getInstance().getSelectedSubjectsIds(listViewSubjects);
						listViewQuestionPapers.getItems().clear();
						listViewQuestionPapers.getItems().addAll(
							QuestionPaperDTO.getInstance().getQuestionPaperListViewItemsBySubjectIds(subjectIds));
						lblPaperFilterStatus.setText("Paper subject filters: ON");
					}
				} else {
					refreshQuestionPapersListView();
				}
				break;
			case VIEW_MODIFY_ALL_QUESTIONS:
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
					refreshQuestionPapersListView();
					SystemNotification.display(SystemNotificationType.SUCCESS,
						"Paper generated! You can now view/export it.");
				}
				break;
			case VIEW_QUESTION_PAPER:
				if (listViewQuestionPapers.getSelectionModel().getSelectedItems().isEmpty()) {
					SystemNotification.display(SystemNotificationType.ERROR, "Please select a paper.");
				} else {
					int id = QuestionPaperDTO.getInstance().getQuestionPaperId(listViewQuestionPapers);
					QuestionPaper questionPaper = QuestionPaperService.getInstance().getQuestionPaperById(id);
					ViewQuestionPaper.display(questionPaper);
				}
				break;
			case DELETE_QUESTION_PAPER:
				if (listViewQuestionPapers.getSelectionModel().getSelectedItems().isEmpty()) {
					SystemNotification.display(SystemNotificationType.ERROR, "Please select a paper.");
				} else if (DeletionConfirm.confirmDelete("question paper")) {
					int paperId = QuestionPaperDTO.getInstance().getQuestionPaperId(listViewQuestionPapers);
					QuestionPaperService.getInstance().deleteQuestionPaperById(paperId);
					refreshQuestionPapersListView();
					SystemNotification.display(SystemNotificationType.SUCCESS, "Paper deleted.");
				}
				break;
			case UPDATE_PASSWORD:
				if (UpdatePassword.updatePassword(currentUser)) {
					SystemNotification.display(SystemNotificationType.SUCCESS, "Password updated.");
				}
				break;
			default:
				SystemNotification.display(SystemNotificationType.ERROR, "Unexpected error.");
		}
	}

	/**
	 * Set up the window.
	 */
	private static void setup() {
		listViewSubjects.getItems().clear();
		listViewSubjects.getItems().addAll(SubjectDTO.getInstance().getSubjectListViewItems());
		listViewSubjects.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		listViewQuestionPapers.getItems().clear();
		listViewQuestionPapers.getItems().addAll(QuestionPaperDTO.getInstance().getQuestionPaperListViewItems());
		listViewQuestionPapers.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		paperSubjectFilterOn = false;
		lblPaperFilterStatus.setText("Paper subject filters: OFF");
	}

	/**
	 * Reset question papers ListView.
	 */
	private static void refreshQuestionPapersListView() {
		listViewQuestionPapers.getItems().clear();
		listViewQuestionPapers.getItems().addAll(QuestionPaperDTO.getInstance().getQuestionPaperListViewItems());
		lblPaperFilterStatus.setText("Paper subject filters: OFF");
		paperSubjectFilterOn = false;
	}
}
