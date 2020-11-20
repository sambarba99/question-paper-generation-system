package interfaceviews;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import dao.UserDAO;

import model.User;
import model.enums.BoxType;
import model.enums.SystemMessageType;
import model.enums.UserType;

import tools.BoxMaker;
import tools.SecurityTools;

/**
 * Login page
 * 
 * @author Sam Barba
 *
 */
public class LoginView extends Application {

	private UserDAO userDao = new UserDAO();

	@Override
	public void start(Stage primaryStage) {
		if (!SecurityTools.usersFileExists()) {
			SystemMessageView.display(SystemMessageType.NEUTRAL,
					"You are the first user (an admin). Set a secure password.");
		}

		Label lblEnterUsername = new Label("Enter username:");
		TextField txtUsername = new TextField();
		Label lblEnterPass = new Label("Enter password:");
		PasswordField passField = new PasswordField();
		Button btnLogin = new Button("Login");

		txtUsername.textProperty().addListener((obs, oldText, newText) -> {
			txtUsername.setText(txtUsername.getText().toLowerCase());
		});
		btnLogin.setOnAction(action -> {
			try {
				User currentUser = login(txtUsername.getText(), passField.getText());
				if (currentUser != null) {
					if (currentUser.getType().equals(UserType.ADMIN)) {
						primaryStage.close();
						AdminControlView.display(currentUser);
					} else if (currentUser.getType().equals(UserType.TUTOR)) {
						primaryStage.close();
						TutorControlView.display(currentUser);
					}
				}
			} catch (Exception e) {
				SystemMessageView.display(SystemMessageType.ERROR, "Unexpected error: " + e.getClass().getName());
			}
		});

		HBox hboxUsername = (HBox) BoxMaker.makeBox(BoxType.HBOX, Pos.CENTER, 5, lblEnterUsername, txtUsername);
		HBox hboxPass = (HBox) BoxMaker.makeBox(BoxType.HBOX, Pos.CENTER, 5, lblEnterPass, passField);
		VBox vboxMain = (VBox) BoxMaker.makeBox(BoxType.VBOX, Pos.CENTER, 20, hboxUsername, hboxPass, btnLogin);

		FlowPane pane = new FlowPane();
		pane.getStyleClass().add("flow-pane");
		pane.getChildren().add(vboxMain);

		Scene scene = new Scene(pane, 650, 300);
		scene.getStylesheets().add("style.css");
		primaryStage.setTitle("Login");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	/**
	 * Get validated user
	 * 
	 * @param username - the entered username
	 * @param pass     - the entered password
	 * @return the validated user, with hashed password
	 */
	private User login(String username, String pass)
			throws FileNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException {

		if (username.length() != 0 && pass.length() != 0 && SecurityTools.usersFileExists()) {
			User user = new User(username, pass, null);
			User validatedUser = SecurityTools.checkUserExists(user);
			if (validatedUser == null) {
				SystemMessageView.display(SystemMessageType.ERROR, "Invalid username or password.");
			} else {
				return validatedUser;
			}
		} else if (username.length() != 0 && pass.length() != 0 && !SecurityTools.usersFileExists()) {
			// if it's first user of system, make them admin
			User user = new User(username, pass, UserType.ADMIN);
			if (SecurityTools.validateFirstTimeLogin(username, pass)) {
				userDao.addUser(user);
				// instead of returning only 'user', we must return user with now hashed
				// password, ass userDao.addUser(user) hashes the password
				return SecurityTools.checkUserExists(user);
			}
		} else {
			SystemMessageView.display(SystemMessageType.ERROR, "Please enter credentials.");
		}
		return null;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
