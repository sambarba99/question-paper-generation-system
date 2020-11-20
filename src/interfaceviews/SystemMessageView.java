package interfaceviews;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.enums.SystemMessageType;

public class SystemMessageView {

	public static void display(SystemMessageType msgType, String message) {
		Label lblMsg = new Label(message);

		FlowPane pane = new FlowPane();
		pane.getStyleClass().add("flow-pane");
		pane.getChildren().add(lblMsg);

		Stage stage = new Stage();
		Scene scene = new Scene(pane, 600, 150);
		scene.getStylesheets().add("style.css");
		stage.setTitle(msgType.getStrVal());
		stage.setScene(scene);
		stage.setResizable(false);
		// so multiple instances of this window can't be opened
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
	}
}