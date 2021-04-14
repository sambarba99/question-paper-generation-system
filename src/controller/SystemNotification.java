package controller;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import view.builders.ButtonBuilder;
import view.builders.PaneBuilder;
import view.enums.BoxType;
import view.enums.SystemNotificationType;
import view.enums.UserAction;
import view.utils.Constants;

/**
 * Displays an error, neutral, or success notification.
 *
 * @author Sam Barba
 */
public class SystemNotification {

	private static Stage stage = new Stage();

	/**
	 * Display a system notification.
	 * 
	 * @param notificationType - the type of system notification
	 * @param msg              - the message to display
	 */
	public static void display(SystemNotificationType notificationType, String msg) {
		Label lbl = new Label(msg);
		lbl.setTextAlignment(TextAlignment.CENTER);

		Button btnOk = new ButtonBuilder()
			.withWidth(60)
			.withUserAction(UserAction.OK)
			.withActionEvent(e -> stage.close())
			.build();

		VBox root = (VBox) new PaneBuilder(BoxType.VBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(20)
			.withNodes(lbl, btnOk)
			.build();

		Scene scene = new Scene(root, 600, 160);
		scene.getStylesheets().add(Constants.CSS_STYLE_PATH);
		stage.setScene(scene);
		stage.setTitle(notificationType.toString());
		stage.setResizable(false);
		stage.setAlwaysOnTop(true);
		stage.show();
	}
}
