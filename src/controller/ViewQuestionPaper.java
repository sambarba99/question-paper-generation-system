package controller;

import java.util.logging.Logger;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.persisted.QuestionPaper;
import model.service.QuestionPaperService;

import view.SystemNotification;
import view.builders.ButtonBuilder;
import view.builders.PaneBuilder;
import view.enums.BoxType;
import view.enums.SystemNotificationType;
import view.enums.UserAction;
import view.utils.Constants;
import view.utils.LogoMaker;

/**
 * Allows the user to view a question paper.
 * 
 * @author Sam Barba
 */
public class ViewQuestionPaper {

	public static final Logger LOGGER = Logger.getLogger(ViewQuestionPaper.class.getName());

	private static Stage stage;

	/**
	 * Display a question paper.
	 */
	public static void display(QuestionPaper questionPaper) {
		stage = new Stage();

		TextArea txtAreaPaper = new TextArea();
		txtAreaPaper.setEditable(false);
		txtAreaPaper.setText(QuestionPaperService.getInstance().getTxtAreaQuestionPaperStr(questionPaper));
		txtAreaPaper.setMinSize(600, 600);
		txtAreaPaper.setMaxSize(600, 600);

		Button btnExport = new ButtonBuilder().withWidth(120).withUserAction(UserAction.EXPORT).withActionEvent(e -> {
			// use Constants.EXPORTED_PAPERS_FILE_PATH;
			LOGGER.info("Exporting papers is unimplemented");
			SystemNotification.display(SystemNotificationType.NEUTRAL, "Unimplemented");
		}).build();

		VBox vboxMain = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(20)
			.withNodes(LogoMaker.makeLogo(300), txtAreaPaper, btnExport)
			.build();

		Scene scene = new Scene(vboxMain, 700, 850);
		scene.getStylesheets().add(Constants.CSS_STYLE_PATH);
		stage.setScene(scene);
		stage.setTitle("View Question Paper");
		stage.setResizable(false);
		// so multiple instances of this window can't be opened
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
	}
}
