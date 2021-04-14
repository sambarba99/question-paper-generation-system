package controller;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
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
 * Requests confirmation from the user (can be for deletion of academic material, or to exit
 * application).
 *
 * @author Sam Barba
 */
public class UserConfirmation extends UIController {

	private static boolean actionConfirmed;

	/**
	 * Display the object to delete (if confirming deletion), and return whether or not the action
	 * was confirmed.
	 * 
	 * @return whether or not the action has been confirmed by the user
	 */
	public static boolean confirm(SystemNotificationType notificationType) {
		stage = new Stage();
		actionConfirmed = false;

		Label lbl = new Label();
		lbl.setTextAlignment(TextAlignment.CENTER);

		if (SystemNotificationType.CONFIRM_DELETION.equals(notificationType)) {
			lbl.setText(Constants.SURE_TO_DELETE);
		} else if (SystemNotificationType.CONFIRM_EXIT_APPLICATION.equals(notificationType)) {
			lbl.setText(Constants.SURE_TO_EXIT);
		}

		Button btnYes = new ButtonBuilder()
			.withWidth(70)
			.withUserAction(UserAction.CONFIRM_YES)
			.withActionEvent(e -> {
				actionConfirmed = true;
				if (SystemNotificationType.CONFIRM_EXIT_APPLICATION.equals(notificationType)) {
					System.exit(0);
				}
				stage.close();
			})
			.build();
		Button btnNo = new ButtonBuilder()
			.withWidth(70)
			.withUserAction(UserAction.CONFIRM_NO)
			.withActionEvent(e -> stage.close())
			.build();

		HBox hboxBtns = (HBox) new PaneBuilder(BoxType.HBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(10)
			.withNodes(btnYes, btnNo)
			.build();
		VBox root = (VBox) new PaneBuilder(BoxType.VBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(20)
			.withNodes(lbl, hboxBtns)
			.build();

		Scene scene = new Scene(root, 600, 300);
		scene.getStylesheets().add(Constants.CSS_STYLE_PATH);
		stage.setScene(scene);
		stage.setTitle(notificationType.toString());
		stage.setResizable(false);
		stage.showAndWait();
		return actionConfirmed;
	}
}
