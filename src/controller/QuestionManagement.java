package controller;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.dto.QuestionDTO;
import model.service.QuestionService;

import view.Constants;
import view.builders.ButtonBuilder;
import view.builders.PaneBuilder;
import view.enums.BoxType;
import view.enums.SystemNotificationType;
import view.enums.UserAction;

/**
 * Allows the user to view all stored questions, and manage them.
 *
 * @author Sam Barba
 */
public class QuestionManagement {

	private static Stage stage;

	private static ListView<String> listViewQuestions = new ListView<>();

	private static TextArea txtAreaQuestion = new TextArea();

	/**
	 * Display all questions and capability to modify them.
	 */
	public static void display() {
		stage = new Stage();

		Label lblSelectQuestion = new Label("Select a question to view:");

		listViewQuestions.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			int questionId = QuestionDTO.getInstance().getQuestionId(listViewQuestions);
			if (questionId != 0) {
				txtAreaQuestion.setText(QuestionDTO.getInstance().getTxtAreaQuestionStr(questionId));
			}
		});

		Button btnAddQuestion = new ButtonBuilder().withWidth(150)
			.withUserAction(UserAction.ADD_NEW_QUESTION)
			.withClickAction(action -> {
				// if added a new question, refresh questions ListView
				if (AddQuestion.display()) {
					setup();
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

		HBox hboxActions = (HBox) new PaneBuilder().withBoxType(BoxType.HBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(7)
			.withNodes(btnAddQuestion, btnDelQuestion)
			.build();
		VBox vboxMain = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.TOP_CENTER)
			.withSpacing(10)
			.withNodes(lblSelectQuestion, listViewQuestions, txtAreaQuestion, hboxActions)
			.build();

		setup();

		FlowPane pane = new FlowPane();
		pane.getStyleClass().add("flow-pane");
		pane.getChildren().add(vboxMain);

		Scene scene = new Scene(pane, 550, 700);
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
		if (listViewQuestions.getSelectionModel().getSelectedItems().isEmpty()) {
			SystemNotification.display(SystemNotificationType.ERROR, "Please select 1 question.");
		} else if (DeletionConfirm.confirmDelete("question")) {
			int questionId = QuestionDTO.getInstance().getQuestionId(listViewQuestions);
			QuestionService.getInstance().deleteQuestionById(questionId);
			listViewQuestions.getItems().clear();
			listViewQuestions.getItems().addAll(QuestionDTO.getInstance().getQuestionListViewItems());
			txtAreaQuestion.setText(Constants.EMPTY);
			setup(); // refresh questions ListView
			SystemNotification.display(SystemNotificationType.SUCCESS, "Question deleted.");
		}
	}

	/**
	 * Set up window.
	 */
	private static void setup() {
		listViewQuestions.getItems().clear();
		listViewQuestions.getItems().addAll(QuestionDTO.getInstance().getQuestionListViewItems());
		listViewQuestions.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		listViewQuestions.setMinSize(400, 300);
		listViewQuestions.setMaxSize(400, 300);

		txtAreaQuestion.setEditable(false);
		txtAreaQuestion.setMinSize(400, 240);
		txtAreaQuestion.setMaxSize(400, 240);
		txtAreaQuestion.setText("No question selected.");
	}
}
