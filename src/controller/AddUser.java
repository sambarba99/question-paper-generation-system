package controller;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.stream.Collectors;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import model.builders.UserBuilder;
import model.persisted.User;

import view.builders.ButtonBuilder;
import view.builders.PaneBuilder;
import view.enums.BoxType;
import view.enums.SystemNotificationType;
import view.enums.UserAction;
import view.enums.UserPrivilege;
import view.utils.Constants;

/**
 * Allows admin users to add new tutor or admin users.
 *
 * @author Sam Barba
 */
public class AddUser extends UIController {

	private static boolean added;

	/**
	 * Add a new user.
	 * 
	 * @return whether or not a user has been added successfully
	 */
	public static boolean addUser() {
		stage = new Stage();
		added = false;

		TextField txtUsername = new TextField();
		PasswordField passField = new PasswordField();
		ChoiceBox choicePrivilege = new ChoiceBox();

		txtUsername.textProperty().addListener((obs, oldText, newText) -> {
			txtUsername.setText(newText.toLowerCase());
		});
		choicePrivilege.getItems().addAll(Arrays.stream(UserPrivilege.values())
			.map(UserPrivilege::toString)
			.collect(Collectors.toList()));
		choicePrivilege.getSelectionModel().selectFirst();
		choicePrivilege.setPrefWidth(100);

		Button btnAddUser = new ButtonBuilder()
			.withWidth(100)
			.withUserAction(UserAction.ADD)
			.withActionEvent(e -> {
				UserPrivilege privilege = UserPrivilege.getFromStr(choicePrivilege.getSelectionModel()
					.getSelectedItem().toString());

				createAndAddUser(txtUsername.getText(), passField.getText(), privilege);
			})
			.build();

		VBox vboxLbls = (VBox) new PaneBuilder(BoxType.VBOX)
			.withAlignment(Pos.CENTER_RIGHT)
			.withSpacing(30)
			.withNodes(new Label("Enter their username:"), new Label("Enter their temporary password:"),
				new Label("Select their privilege:"))
			.build();
		VBox vboxCreds = (VBox) new PaneBuilder(BoxType.VBOX)
			.withAlignment(Pos.CENTER_LEFT)
			.withSpacing(20)
			.withNodes(txtUsername, passField, choicePrivilege)
			.build();
		HBox hboxUserCreds = (HBox) new PaneBuilder(BoxType.HBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(10)
			.withNodes(vboxLbls, vboxCreds)
			.build();
		VBox root = (VBox) new PaneBuilder(BoxType.VBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(20)
			.withNodes(hboxUserCreds, btnAddUser)
			.build();

		Scene scene = new Scene(root, 500, 300);
		scene.getStylesheets().add(Constants.CSS_STYLE_PATH);
		stage.setScene(scene);
		stage.setTitle("Add New User");
		stage.setResizable(false);
		stage.showAndWait();
		return added;
	}

	/**
	 * Add a new user.
	 * 
	 * @param username  - the new user's username
	 * @param password  - the new user's raw password
	 * @param privilege - the new user's privilege level
	 */
	private static void createAndAddUser(String username, String password, UserPrivilege privilege) {
		try {
			if (validNewUserCreds(username, password)) {
				User user = new UserBuilder()
					.withUsername(username)
					.withPassword(password)
					.withPrivilege(privilege)
					.build();

				userService.addUser(user);
				added = true;
				stage.close();
			}
		} catch (Exception e) {
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + e.getClass().getName() + "\nIn: " + AddUser.class.getName());
		}
	}

	/**
	 * Validate the addition of a new user by an admin.
	 * 
	 * @param username - the username of the new user
	 * @param pass     - the temporary password of the new user
	 * @return whether or not the new credentials are valid
	 */
	private static boolean validNewUserCreds(String username, String pass) throws FileNotFoundException,
		NoSuchAlgorithmException, UnsupportedEncodingException {

		if (username.isEmpty()) {
			SystemNotification.display(SystemNotificationType.ERROR, "Please enter a username.");
			return false;
		}
		if (userService.checkUserExists(username).isPresent()) {
			SystemNotification.display(SystemNotificationType.ERROR,
				"That username already exists.\nTry adding a number on the end to make it unique!");
			return false;
		}
		if (!username.matches(Constants.USERNAME_REGEX)) {
			SystemNotification.display(SystemNotificationType.ERROR,
				"Username must be letters only, and optionally end with digits.");
			return false;
		}
		if (!pass.matches(Constants.PASSWORD_REGEX)) {
			SystemNotification.display(SystemNotificationType.ERROR,
				"Password must contain 0-9, a-z, A-Z, and be at least 8 long.");
			return false;
		}
		return true;
	}
}
