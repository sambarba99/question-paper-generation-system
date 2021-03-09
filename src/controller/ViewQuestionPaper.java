package controller;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.dto.QuestionPaperDTO;
import model.persisted.QuestionPaper;

import view.BoxMaker;
import view.enums.BoxType;
import view.enums.SystemNotificationType;

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
		Button btnExport = new Button("Export to .txt");

		txtAreaPaper.setEditable(false);
		txtAreaPaper.setText(QuestionPaperDTO.getInstance().getTxtAreaQuestionPaperStr(questionPaper));
		txtAreaPaper.setPrefSize(400, 600);

		btnExport.setOnAction(action -> {
			SystemNotification.display(SystemNotificationType.NEUTRAL, "Unimplemented");
		});

		BoxMaker boxMaker = BoxMaker.getInstance();
		HBox hboxBtns = (HBox) boxMaker.makeBox(BoxType.HBOX, Pos.CENTER, 5, btnExport);
		VBox vboxMain = (VBox) boxMaker.makeBox(BoxType.VBOX, Pos.CENTER, 20, txtAreaPaper, hboxBtns);

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
