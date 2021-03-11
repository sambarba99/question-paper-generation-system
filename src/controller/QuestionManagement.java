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

import view.BoxMaker;
import view.ButtonMaker;
import view.Constants;
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

		Button btnAddQuestion = ButtonMaker.getInstance().makeButton(150, Constants.BTN_HEIGHT,
			UserAction.ADD_NEW_QUESTION, action -> {
				// if added a new question, refresh questions ListView
				if (AddQuestion.display()) {
					setup();
					SystemNotification.display(SystemNotificationType.SUCCESS, "Question added!");
				}
			});
		Button btnDelQuestion = ButtonMaker.getInstance().makeButton(150, Constants.BTN_HEIGHT,
			UserAction.DELETE_QUESTION, action -> {
				deleteQuestion();
			});

		BoxMaker boxMaker = BoxMaker.getInstance();
		HBox hboxOptions = (HBox) boxMaker.makeBox(BoxType.HBOX, Pos.CENTER, 7, btnAddQuestion, btnDelQuestion);
		VBox vboxMain = (VBox) boxMaker.makeBox(BoxType.VBOX, Pos.TOP_CENTER, 10, lblSelectQuestion, listViewQuestions,
			txtAreaQuestion, hboxOptions);

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
			SystemNotification.display(SystemNotificationType.ERROR, "Please select a question.");
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
