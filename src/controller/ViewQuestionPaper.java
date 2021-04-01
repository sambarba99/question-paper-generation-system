package controller;

import java.io.File;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
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

	private static Stage stage;

	private static String destinationDir;

	/**
	 * Display a question paper.
	 */
	public static void display(QuestionPaper questionPaper) {
		stage = new Stage();

		TextArea txtAreaPaper = new TextArea();
		txtAreaPaper.setEditable(false);
		txtAreaPaper.setText(QuestionPaperService.getInstance().getQuestionPaperDisplayStr(questionPaper));
		txtAreaPaper.setMinSize(600, 600);
		txtAreaPaper.setMaxSize(600, 600);

		/*
		 * Set default export directory to Downloads folder
		 */
		destinationDir = System.getProperty("user.home") + "\\Downloads";

		Label lblDestinationDir = new Label("Export to:\n" + destinationDir);
		lblDestinationDir.setTextAlignment(TextAlignment.CENTER);

		Button btnExport = new ButtonBuilder().withWidth(120).withUserAction(UserAction.EXPORT).withActionEvent(e -> {
			if ("C:\\".equals(destinationDir)) {
				SystemNotification.display(SystemNotificationType.ERROR,
					"Cannot export to C drive. Please choose another directory.");
			} else {
				boolean success = QuestionPaperService.getInstance().exportToTxt(questionPaper, destinationDir);
				if (success) {
					SystemNotification.display(SystemNotificationType.SUCCESS,
						"Paper successfully exported to\n" + destinationDir);
				}
			}
		}).build();

		Button btnChooseExportDest = new ButtonBuilder().withWidth(220)
			.withUserAction(UserAction.CHOOSE_EXPORT_DESTINATION)
			.withActionEvent(e -> {
				DirectoryChooser dc = new DirectoryChooser();
				dc.setInitialDirectory(new File(destinationDir));
				File selectedDir = dc.showDialog(stage);

				if (selectedDir != null) {
					destinationDir = selectedDir.getAbsolutePath();
					lblDestinationDir.setText("Export to:\n" + destinationDir);
				}
			})
			.build();

		HBox hboxBtns = (HBox) new PaneBuilder().withBoxType(BoxType.HBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(10)
			.withNodes(btnExport, btnChooseExportDest)
			.build();
		VBox vboxMain = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(20)
			.withNodes(LogoMaker.makeLogo(300), txtAreaPaper, lblDestinationDir, hboxBtns)
			.build();

		Scene scene = new Scene(vboxMain, 700, 900);
		scene.getStylesheets().add(Constants.CSS_STYLE_PATH);
		stage.setScene(scene);
		stage.setTitle("View Question Paper");
		stage.setResizable(false);
		// so multiple instances of this window can't be opened
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
	}
}
