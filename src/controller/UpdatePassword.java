package controller;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.persisted.User;
import model.service.UserService;

import view.Constants;
import view.builders.ButtonBuilder;
import view.builders.PaneBuilder;
import view.enums.BoxType;
import view.enums.SystemNotificationType;
import view.enums.UserAction;

/**
 * Allows the user to update their password.
 *
 * @author Sam Barba
 */
public class UpdatePassword {

	private static Stage stage;

	private static boolean updated;

	/**
	 * Update the user's password.
	 * 
	 * @param currentUser - the user calling this method
	 * @return whether or not the password has been updated
	 */
	public static boolean updatePassword(User currentUser) {
		stage = new Stage();
		updated = false;

		Label lblEnterCurrentPass = new Label("Enter current password:");
		PasswordField passFieldCurrent = new PasswordField();
		Label lblEnterNewPass = new Label("Enter new password:");
		PasswordField passFieldNew = new PasswordField();
		Label lblRepeatNewPass = new Label("Repeat new password:");
		PasswordField passFieldRepeat = new PasswordField();
		Button btnUpdate = new ButtonBuilder().withWidth(150).withUserAction(UserAction.UPDATE_PASSWORD)
			.withActionEvent(action -> {
				String currentPass = passFieldCurrent.getText();
				String newPass = passFieldNew.getText();
				String repeatPass = passFieldRepeat.getText();
				updatePassword(currentPass, newPass, repeatPass, currentUser);
			}).build();

		VBox vboxLbls = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX).withAlignment(Pos.CENTER_RIGHT)
			.withSpacing(30).withNodes(lblEnterCurrentPass, lblEnterNewPass, lblRepeatNewPass).build();
		VBox vboxPassFields = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX).withAlignment(Pos.CENTER_LEFT)
			.withSpacing(20).withNodes(passFieldCurrent, passFieldNew, passFieldRepeat).build();
		HBox hbox = (HBox) new PaneBuilder().withBoxType(BoxType.HBOX).withAlignment(Pos.CENTER).withSpacing(5)
			.withNodes(vboxLbls, vboxPassFields).build();
		VBox vboxMain = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX).withAlignment(Pos.CENTER).withSpacing(20)
			.withNodes(hbox, btnUpdate).build();

		FlowPane pane = new FlowPane();
		pane.getStyleClass().add("flow-pane");
		pane.getChildren().add(vboxMain);

		Scene scene = new Scene(pane, 600, 300);
		scene.getStylesheets().add("style.css");
		stage.setScene(scene);
		stage.setTitle("Update Password");
		stage.setResizable(false);
		// so multiple instances of this window can't be opened
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
		return updated;
	}

	/**
	 * Update the current user's password.
	 * 
	 * @param currentPass - the user's current password
	 * @param newPass     - the user's new password
	 * @param repeatPass  - the user's repeated new password
	 * @param currentUser - the user currently in session
	 */
	private static void updatePassword(String currentPass, String newPass, String repeatPass, User currentUser) {
		try {
			UserService userService = UserService.getInstance();
			if (userService.validateResetPassword(currentUser, currentPass, newPass, repeatPass)) {
				userService.updatePassword(currentUser, newPass);
				updated = true;
				stage.close();
			}
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + e.getClass().getName());
		}
	}
}
