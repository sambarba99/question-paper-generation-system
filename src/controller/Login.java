package controller;

import java.util.Optional;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import model.persisted.User;
import model.service.UserService;

import view.builders.ButtonBuilder;
import view.builders.PaneBuilder;
import view.enums.BoxType;
import view.enums.SystemNotificationType;
import view.enums.UserAction;
import view.enums.UserPrivilege;
import view.utils.Constants;
import view.utils.LogoMaker;

/**
 * Allows the user to access the system with correct username and password.
 * 
 * @author Sam Barba
 */
public class Login extends Application {

	private static UserService userService = UserService.getInstance();

	@Override
	public void start(Stage primaryStage) {
		TextField txtUsername = new TextField();
		PasswordField passField = new PasswordField();

		txtUsername.setMaxWidth(200);
		txtUsername.textProperty().addListener((obs, oldText, newText) -> {
			txtUsername.setText(newText.toLowerCase());
		});
		passField.setMaxWidth(200);

		Button btnLogin = new ButtonBuilder()
			.withWidth(100)
			.withUserAction(UserAction.LOG_IN)
			.withActionEvent(e -> {
				login(txtUsername.getText(), passField.getText(), primaryStage);
			})
			.build();

		VBox vboxCredentials = (VBox) new PaneBuilder(BoxType.VBOX)
			.withAlignment(Pos.CENTER_LEFT)
			.withSpacing(10)
			.withNodes(new Label("Enter username:"), txtUsername, new Label("Enter password:"), passField)
			.build();
		HBox hboxCredsAndLogin = (HBox) new PaneBuilder(BoxType.HBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(30)
			.withNodes(vboxCredentials, btnLogin)
			.build();
		VBox root = (VBox) new PaneBuilder(BoxType.VBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(40)
			.withNodes(LogoMaker.makeLogo(400), hboxCredsAndLogin)
			.build();

		Scene scene = new Scene(root, 600, 500);
		scene.getStylesheets().add(Constants.CSS_STYLE_PATH);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Login");
		primaryStage.setResizable(false);
		primaryStage.show();

		/*
		 * If this is first-time system access, the first user needs to be notified
		 */
		if (!userService.usersFileExists()) {
			SystemNotification.display(SystemNotificationType.NEUTRAL,
				"Welcome.\nYou are the first user, and have admin privilege.\nChoose a username and set a secure password.");
		}
	}

	/**
	 * Log a user into the system.
	 * 
	 * @param username - the username of the user
	 * @param password - the password of the user
	 * @param stage    - the Login window stage
	 */
	private void login(String username, String password, Stage stage) {
		try {
			Optional<User> currentUser = userService.login(username, password);

			if (currentUser.isPresent()) {
				stage.close();
				UserPrivilege userPrivilege = currentUser.get().getPrivilege();

				switch (userPrivilege) {
					case ADMIN:
						AdminPanel.display(currentUser.get());
						break;
					default: // TUTOR
						AcademicMaterialManagement.display(currentUser.get());
				}
			} else if (userService.usersFileExists()) {
				SystemNotification.display(SystemNotificationType.ERROR, "Invalid credentials.");
			}
		} catch (Exception e) {
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + e.getClass().getName() + "\nIn: " + this.getClass().getName());
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
