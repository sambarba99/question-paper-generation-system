package interfaceviews;

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

import dao.QuestionDAO;
import dao.QuestionPaperDAO;
import dao.SubjectDAO;

import dto.QuestionPaperDTO;
import dto.SubjectDTO;
import dto.TutorActionDTO;

import model.QuestionPaper;
import model.User;
import model.enums.BoxType;
import model.enums.SystemMessageType;
import model.enums.TutorAction;

import utils.BoxMaker;

public class TutorControlView {

	private static QuestionDAO questionDao = new QuestionDAO();

	private static SubjectDAO subjectDao = new SubjectDAO();

	private static SubjectDTO subjectDto = new SubjectDTO();

	private static QuestionPaperDAO questionPaperDao = new QuestionPaperDAO();

	private static QuestionPaperDTO questionPaperDto = new QuestionPaperDTO();

	private static TutorActionDTO tutorActionDto = new TutorActionDTO();

	private static boolean paperSubjectFilterOn;

	private static Label lblPaperFilterStatus = new Label();

	private static ListView<String> listViewSubjects = new ListView<>();

	private static ListView<String> listViewQuestionPapers = new ListView<>();

	private static ListView<String> listViewTutorActions = new ListView<>();

	public static void display(User currentUser) {
		Label lblHeader = new Label("View & Modify Academic Material");
		Label lblSubjects = new Label("Subjects");
		Label lblQps = new Label("Question Papers");
		Label lblActions = new Label("Select an action:");
		Button btnExecute = new Button("Perform action");

		lblHeader.setStyle("-fx-font-size: 25px");
		btnExecute.setOnAction(action -> {
			TutorAction tutorAction = tutorActionDto.getSelectedTutorAction(listViewTutorActions);

			switch (tutorAction) {
				case ADD_SUBJECT:
					if (AddSubjectView.addSubject()) {
						listViewSubjects.getItems().clear();
						listViewSubjects.getItems().addAll(subjectDto.getSubjectListViewItems());
						SystemMessageView.display(SystemMessageType.SUCCESS, "Subject added!");
					}
					break;
				case DELETE_SUBJECT:
					if (listViewSubjects.getSelectionModel().getSelectedItems().size() != 1) {
						SystemMessageView.display(SystemMessageType.ERROR, "Please select 1 subject.");
					} else if (DeletionConfirmView.confirmDelete("subject")) {
						int subjectId = subjectDto.getSelectedSubjectsIds(listViewSubjects).get(0);
						subjectDao.deleteSubjectById(subjectId);
						listViewSubjects.getItems().clear();
						listViewSubjects.getItems().addAll(subjectDto.getSubjectListViewItems());
						refreshPaperListView();
						SystemMessageView.display(SystemMessageType.SUCCESS, "Subject deleted.");
					}
					break;
				case TOGGLE_FILTER_PAPERS:
					if (!paperSubjectFilterOn) {
						if (listViewSubjects.getSelectionModel().getSelectedItems().isEmpty()) {
							SystemMessageView.display(SystemMessageType.ERROR, "Please select subjects to filter by.");
						} else {
							paperSubjectFilterOn = true;
							List<Integer> subjectIds = subjectDto.getSelectedSubjectsIds(listViewSubjects);
							listViewQuestionPapers.getItems().clear();
							listViewQuestionPapers.getItems()
									.addAll(questionPaperDto.getQuestionPaperListViewItemsBySubjectIds(subjectIds));
							lblPaperFilterStatus.setText("Paper subject filters: ON");
						}
					} else {
						refreshPaperListView();
					}
					break;
				case VIEW_MODIFY_ALL_QUESTIONS:
					if (subjectDao.getAllSubjects().isEmpty()) {
						SystemMessageView.display(SystemMessageType.ERROR, "Add at least 1 subject first.");
					} else {
						if (AllQuestionsView.display()) { // if questions are modified
							refreshPaperListView();
						}
					}
					break;
				case GENERATE_QUESTION_PAPER:
					if (questionDao.getAllQuestions().isEmpty()) {
						SystemMessageView.display(SystemMessageType.ERROR, "Add some questions first.");
					} else if (GenerateQuestionPaperView.generatePaper()) {
						refreshPaperListView();
					}
					break;
				case VIEW_QUESTION_PAPER:
					if (listViewQuestionPapers.getSelectionModel().getSelectedItems().isEmpty()) {
						SystemMessageView.display(SystemMessageType.ERROR, "Please select a paper.");
					} else {
						int id = questionPaperDto.getQpId(listViewQuestionPapers);
						QuestionPaper qp = questionPaperDao.getQuestionPaperById(id);
						QuestionPaperView.display(qp);
					}
					break;
				case DELETE_QUESTION_PAPER:
					if (listViewQuestionPapers.getSelectionModel().getSelectedItems().isEmpty()) {
						SystemMessageView.display(SystemMessageType.ERROR, "Please select a paper.");
					} else if (DeletionConfirmView.confirmDelete("question paper")) {
						int paperId = questionPaperDto.getQpId(listViewQuestionPapers);
						questionPaperDao.deleteQuestionPaperById(paperId);
						refreshPaperListView();
						SystemMessageView.display(SystemMessageType.SUCCESS, "Paper deleted.");
					}
					break;
				case CHANGE_PASSWORD:
					if (ChangePasswordView.changePassword(currentUser)) {
						SystemMessageView.display(SystemMessageType.SUCCESS, "Password updated.");
					}
					break;
				default: // NONE
					SystemMessageView.display(SystemMessageType.ERROR, "Please select an action.");
			}
		});

		VBox vboxSubjects = (VBox) BoxMaker.makeBox(BoxType.VBOX, Pos.CENTER, 10, lblSubjects, listViewSubjects);
		VBox vboxQuestionPapers = (VBox) BoxMaker.makeBox(BoxType.VBOX, Pos.CENTER, 10, lblQps, listViewQuestionPapers);
		VBox vboxActions = (VBox) BoxMaker.makeBox(BoxType.VBOX, Pos.TOP_CENTER, 10, lblActions, listViewTutorActions,
				btnExecute, lblPaperFilterStatus);
		HBox hboxViews = (HBox) BoxMaker.makeBox(BoxType.HBOX, Pos.CENTER, 20, vboxSubjects, vboxQuestionPapers,
				vboxActions);
		VBox vboxMain = (VBox) BoxMaker.makeBox(BoxType.VBOX, Pos.CENTER, 20, lblHeader, hboxViews);

		FlowPane pane = new FlowPane();
		pane.getStyleClass().add("flow-pane");
		pane.getChildren().add(vboxMain);

		setup();

		Stage stage = new Stage();
		Scene scene = new Scene(pane, 900, 550);
		scene.getStylesheets().add("style.css");
		stage.setScene(scene);
		stage.setTitle("Tutor Control");
		stage.setResizable(false);
		// so multiple instances of this window can't be opened
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
	}

	/**
	 * Set up the window
	 */
	private static void setup() {
		listViewSubjects.getItems().clear();
		listViewSubjects.getItems().addAll(subjectDto.getSubjectListViewItems());
		listViewSubjects.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		listViewQuestionPapers.getItems().clear();
		listViewQuestionPapers.getItems().addAll(questionPaperDto.getQuestionPaperListViewItems());
		listViewQuestionPapers.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		listViewTutorActions.getItems().clear();
		listViewTutorActions.getItems().addAll(tutorActionDto.getTutorActionListViewItems());
		listViewTutorActions.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		listViewTutorActions.setPrefHeight(186);

		paperSubjectFilterOn = false;
		lblPaperFilterStatus.setText("Paper subject filters: OFF");
	}

	/**
	 * Reset question paper ListView
	 */
	private static void refreshPaperListView() {
		paperSubjectFilterOn = false;
		listViewQuestionPapers.getItems().clear();
		listViewQuestionPapers.getItems().addAll(questionPaperDto.getQuestionPaperListViewItems());
		lblPaperFilterStatus.setText("Paper subject filters: OFF");
	}
}