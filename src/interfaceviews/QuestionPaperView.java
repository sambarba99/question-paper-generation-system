package interfaceviews;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import dto.QuestionPaperDTO;

import model.QuestionPaper;
import model.enums.BoxType;
import model.enums.SystemMessageType;

import tools.BoxMaker;

public class QuestionPaperView {

	private static QuestionPaperDTO questionPaperDto = new QuestionPaperDTO();

	public static void display(QuestionPaper qp) {
		Stage stage = new Stage();

		TextArea txtAreaPaper = new TextArea();
		Button btnExport = new Button("Export to .docx");

		txtAreaPaper.setEditable(false);
		txtAreaPaper.setText(questionPaperDto.getTxtAreaQuestionPaperStr(qp));
		txtAreaPaper.setPrefSize(400, 600);

		btnExport.setOnAction(action -> {
			SystemMessageView.display(SystemMessageType.NEUTRAL, "Unimplemented");
		});

		HBox hboxBtns = (HBox) BoxMaker.makeBox(BoxType.HBOX, Pos.CENTER, 5, btnExport);
		VBox vboxMain = (VBox) BoxMaker.makeBox(BoxType.VBOX, Pos.CENTER, 20, txtAreaPaper, hboxBtns);

		FlowPane pane = new FlowPane();
		pane.getStyleClass().add("flow-pane");
		pane.getChildren().add(vboxMain);

		Scene scene = new Scene(pane, 600, 700);
		scene.getStylesheets().add("style.css");
		stage.setTitle("View Question Paper");
		stage.setScene(scene);
		stage.setResizable(false);
		// so multiple instances of this window can't be opened
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
	}
}