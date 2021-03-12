package controller;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import view.builders.PaneBuilder;
import view.builders.ButtonBuilder;
import view.enums.BoxType;
import view.enums.UserAction;

/**
 * Requests confirmation from the user to delete academic material e.g. a subject, or to delete another user (admin
 * capability only).
 *
 * @author Sam Barba
 */
public class DeletionConfirm {

	private static Stage stage;

	private static boolean deleted;

	/**
	 * Display the name of the item to delete, and return whether or not it has been deleted when the window closes.
	 * 
	 * @param deletingItem - the type of item to delete, e.g. 'user' or 'subject'
	 * @return whether or not the item has been deleted
	 */
	public static boolean confirmDelete(String deletingItem) {
		stage = new Stage();
		deleted = false;

		Label lbl = new Label("Are you sure you wish to delete this " + deletingItem + "?");

		Button btnYes = new ButtonBuilder().withWidth(70).withUserAction(UserAction.DELETE_CONFIRM_YES)
			.withActionEvent(action -> {
				deleted = true;
				stage.close();
			}).build();
		Button btnNo = new ButtonBuilder().withWidth(70).withUserAction(UserAction.DELETE_CONFIRM_NO)
			.withActionEvent(action -> {
				stage.close();
			}).build();

		VBox vboxMain = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX).withAlignment(Pos.CENTER).withSpacing(20)
			.withNodes(lbl, btnYes, btnNo).build();

		FlowPane pane = new FlowPane();
		pane.getStyleClass().add("flow-pane");
		pane.getChildren().add(vboxMain);

		Scene scene = new Scene(pane, 600, 300);
		scene.getStylesheets().add("style.css");
		stage.setScene(scene);
		stage.setTitle("Deletion Confirmation");
		stage.setResizable(false);
		// wait for window to be closed before returning 'confirm'
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
		return deleted;
	}
}
