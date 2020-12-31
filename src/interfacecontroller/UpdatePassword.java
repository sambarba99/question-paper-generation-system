package interfacecontroller;

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

import service.UserService;

import model.User;
import model.enums.BoxType;
import model.enums.SystemNotificationType;

import utils.BoxMaker;
import utils.Constants;

/**
 * Allows the user to update their password.
 *
 * @author Sam Barba
 */
public class UpdatePassword {

	private static Stage stage = new Stage();

	private static boolean updated;

	/**
	 * Update the user's password.
	 * 
	 * @param currentUser - the user calling this method
	 * @return whether or not the password has been updated
	 */
	public static boolean updatePassword(User currentUser) {
		updated = false;

		Label lblEnterCurrentPass = new Label("Enter current password:");
		PasswordField passFieldCurrent = new PasswordField();
		Label lblEnterNewPass = new Label("Enter new password:");
		PasswordField passFieldNew = new PasswordField();
		Label lblRepeatNewPass = new Label("Repeat new password:");
		PasswordField passFieldRepeat = new PasswordField();
		Button btnUpdate = new Button("Update");

		btnUpdate.setOnAction(action -> {
			String currentPass = passFieldCurrent.getText();
			String newPass = passFieldNew.getText();
			String repeatPass = passFieldRepeat.getText();
			updatePassword(currentPass, newPass, repeatPass, currentUser);
		});

		BoxMaker boxMaker = BoxMaker.getInstance();
		VBox vboxLbls = (VBox) boxMaker.makeBox(BoxType.VBOX, Pos.CENTER_RIGHT, 30, lblEnterCurrentPass,
				lblEnterNewPass, lblRepeatNewPass);
		VBox vboxPassFields = (VBox) boxMaker.makeBox(BoxType.VBOX, Pos.CENTER_LEFT, 20, passFieldCurrent, passFieldNew,
				passFieldRepeat);
		HBox hbox = (HBox) boxMaker.makeBox(BoxType.HBOX, Pos.CENTER, 5, vboxLbls, vboxPassFields);
		VBox vboxMain = (VBox) boxMaker.makeBox(BoxType.VBOX, Pos.CENTER, 20, hbox, btnUpdate);

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
				String newPassHash = userService.getUserByUsername(currentUser.getUsername()).getPassword();
				currentUser.setPassword(newPassHash);
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
