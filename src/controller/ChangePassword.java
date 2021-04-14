package controller;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import model.persisted.User;

import view.builders.ButtonBuilder;
import view.builders.PaneBuilder;
import view.enums.BoxType;
import view.enums.SystemNotificationType;
import view.enums.UserAction;
import view.utils.Constants;
import view.utils.SecurityUtils;

/**
 * Allows the user to change their password.
 *
 * @author Sam Barba
 */
public class ChangePassword extends UIController {

	/**
	 * Display page for changing password.
	 * 
	 * @param currentUser - the user calling this method
	 */
	public static void display(User currentUser) {
		stage = new Stage();

		PasswordField passFieldCurrent = new PasswordField();
		PasswordField passFieldNew = new PasswordField();
		PasswordField passFieldRepeat = new PasswordField();

		Button btnChange = new ButtonBuilder()
			.withWidth(150)
			.withUserAction(UserAction.CHANGE_PASSWORD)
			.withActionEvent(e -> {
				String currentPass = passFieldCurrent.getText();
				String newPass = passFieldNew.getText();
				String repeatPass = passFieldRepeat.getText();
				updatePassword(currentPass, newPass, repeatPass, currentUser);
			})
			.build();

		VBox vboxLbls = (VBox) new PaneBuilder(BoxType.VBOX)
			.withAlignment(Pos.CENTER_RIGHT)
			.withSpacing(30)
			.withNodes(new Label("Enter current password:"), new Label("Enter new password:"),
				new Label("Repeat new password:"))
			.build();
		VBox vboxPassFields = (VBox) new PaneBuilder(BoxType.VBOX)
			.withAlignment(Pos.CENTER_LEFT)
			.withSpacing(20)
			.withNodes(passFieldCurrent, passFieldNew, passFieldRepeat)
			.build();
		HBox hbox = (HBox) new PaneBuilder(BoxType.HBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(5)
			.withNodes(vboxLbls, vboxPassFields)
			.build();
		VBox root = (VBox) new PaneBuilder(BoxType.VBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(20)
			.withNodes(hbox, btnChange)
			.build();

		Scene scene = new Scene(root, 500, 250);
		scene.getStylesheets().add(Constants.CSS_STYLE_PATH);
		stage.setScene(scene);
		stage.setTitle("Change Password");
		stage.setResizable(false);
		stage.showAndWait();
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
			if (validateResetPassword(currentUser, currentPass, newPass, repeatPass)) {
				userService.updatePassword(currentUser, newPass);
				SystemNotification.display(SystemNotificationType.SUCCESS, "Password changed!");
				stage.close();
			}
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + e.getClass().getName() + "\nIn: " + ChangePassword.class.getName());
		}
	}

	/**
	 * Validate a password update.
	 * 
	 * @param currentUser   - the user performing this action
	 * @param currentPass   - the user's current password
	 * @param newPass       - the user's new password
	 * @param repeatNewPass - the user's repeated new password
	 * @return whether or not the new passwords are valid
	 */
	private static boolean validateResetPassword(User currentUser, String currentPass, String newPass,
		String repeatNewPass) throws NoSuchAlgorithmException, UnsupportedEncodingException {

		if (currentPass.isEmpty()) {
			SystemNotification.display(SystemNotificationType.ERROR, "Please enter current password.");
			return false;
		}
		if (!SecurityUtils.getInstance().sha512(currentPass).equals(currentUser.getPassword())) {
			SystemNotification.display(SystemNotificationType.ERROR, "Current password incorrect.");
			return false;
		}
		if (!newPass.matches(Constants.PASSWORD_REGEX)) {
			SystemNotification.display(SystemNotificationType.ERROR,
				"Password must contain 0-9, a-z, A-Z, and be at least 8 long.");
			return false;
		}
		if (!newPass.equals(repeatNewPass)) {
			SystemNotification.display(SystemNotificationType.ERROR, "Those passwords don't match!");
			return false;
		}
		if (newPass.equals(currentPass)) {
			SystemNotification.display(SystemNotificationType.ERROR, "New password must be different to current.");
			return false;
		}
		return true;
	}
}
