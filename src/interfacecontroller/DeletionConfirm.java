package interfacecontroller;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.enums.BoxType;

import utils.BoxMaker;

/**
 * Requests confirmation from the user to delete academic material e.g. a subject, or to delete another user (admin
 * capability only).
 *
 * @author Sam Barba
 */
public class DeletionConfirm {

	private static Stage stage = new Stage();

	private static boolean deleted;

	/**
	 * Display the name of the item to delete, and return whether or not it has been deleted when the window closes.
	 * 
	 * @param deleteItem - the type of item to delete, e.g. a user or subject
	 * @return whether or not the item has been deleted
	 */
	public static boolean confirmDelete(String deleteItem) {
		deleted = false;

		Label lbl = new Label("Are you sure you wish to delete this " + deleteItem + "?");
		Button btnYes = new Button("Yes");
		Button btnNo = new Button("No");

		btnYes.setOnAction(action -> {
			deleted = true;
			stage.close();
		});
		btnNo.setOnAction(action -> {
			stage.close();
		});

		VBox vboxMain = (VBox) BoxMaker.getInstance().makeBox(BoxType.VBOX, Pos.CENTER, 20, lbl, btnYes, btnNo);

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
