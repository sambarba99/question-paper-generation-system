package view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

import view.builders.PaneBuilder;
import view.enums.BoxType;
import view.enums.SystemNotificationType;
import view.utils.Constants;

/**
 * Displays an error, neutral, or success notification.
 *
 * @author Sam Barba
 */
public class SystemNotification {

	private static Stage stage;

	/**
	 * Display a system notification.
	 * 
	 * @param notificationType - the type of system notification
	 * @param msg              - the message to display
	 */
	public static void display(SystemNotificationType notificationType, String msg) {
		stage = new Stage();

		Label lbl = new Label(msg);
		lbl.setTextAlignment(TextAlignment.CENTER);

		HBox hboxMain = (HBox) new PaneBuilder().withBoxType(BoxType.HBOX)
			.withAlignment(Pos.CENTER)
			.withNodes(lbl)
			.build();

		Scene scene = new Scene(hboxMain, 600, 150);
		scene.getStylesheets().add(Constants.CSS_STYLE_PATH);
		stage.setScene(scene);
		stage.setTitle(notificationType.getStrVal());
		stage.setResizable(false);
		// so multiple instances of this window can't be opened
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
	}
}
