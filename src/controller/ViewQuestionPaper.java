package controller;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.dto.QuestionPaperDTO;
import model.persisted.QuestionPaper;

import view.builders.ButtonBuilder;
import view.builders.PaneBuilder;
import view.enums.BoxType;
import view.enums.SystemNotificationType;
import view.enums.UserAction;

/**
 * Allows the user to view a question paper.
 * 
 * @author Sam Barba
 */
public class ViewQuestionPaper {

	private static Stage stage;

	/**
	 * Display a question paper.
	 */
	public static void display(QuestionPaper questionPaper) {
		stage = new Stage();

		TextArea txtAreaPaper = new TextArea();
		txtAreaPaper.setEditable(false);
		txtAreaPaper.setText(QuestionPaperDTO.getInstance().getTxtAreaQuestionPaperStr(questionPaper));
		txtAreaPaper.setMinSize(400, 600);
		txtAreaPaper.setMaxSize(400, 600);

		Button btnExport = new ButtonBuilder().withWidth(100)
			.withUserAction(UserAction.EXPORT)
			.withClickAction(action -> {
				// use Constants.EXPORTED_PAPERS_FILE_PATH;
				SystemNotification.display(SystemNotificationType.NEUTRAL, "Unimplemented");
			})
			.build();

		VBox vboxMain = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(20)
			.withNodes(txtAreaPaper, btnExport)
			.build();

		FlowPane pane = new FlowPane();
		pane.getStyleClass().add("flow-pane");
		pane.getChildren().add(vboxMain);

		Scene scene = new Scene(pane, 600, 700);
		scene.getStylesheets().add("style.css");
		stage.setScene(scene);
		stage.setTitle("View Question Paper");
		stage.setResizable(false);
		// so multiple instances of this window can't be opened
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
	}
}
