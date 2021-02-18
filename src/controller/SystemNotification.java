package controller;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import view.enums.SystemNotificationType;

/**
 * Displays an error, success, or neutral notification.
 *
 * @author Sam Barba
 */
public class SystemNotification {

	private static Stage stage;

	/**
	 * Display a system notification.
	 * 
	 * @param notificationType - the type of system notification
	 * @param notification     - the message to display
	 */
	public static void display(SystemNotificationType notificationType, String notification) {
		stage = new Stage();
		Label lblNotification = new Label(notification);

		FlowPane pane = new FlowPane();
		pane.getStyleClass().add("flow-pane");
		pane.getChildren().add(lblNotification);

		Scene scene = new Scene(pane, 600, 150);
		scene.getStylesheets().add("style.css");
		stage.setScene(scene);
		stage.setTitle(notificationType.getStrVal());
		stage.setResizable(false);
		// so multiple instances of this window can't be opened
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
	}
}
