package interfacecontroller;

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

import service.UserService;

import model.User;
import model.enums.BoxType;
import model.enums.SystemNotificationType;
import model.enums.UserType;

import utils.BoxMaker;
import utils.Constants;

/**
 * Allows the user to access the system with correct username and password.
 * 
 * @author Sam Barba
 */
public class Login extends Application {

	@Override
	public void start(Stage primaryStage) {
		if (!UserService.getInstance().usersFileExists()) {
			SystemNotification.display(SystemNotificationType.NEUTRAL,
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
			login(txtUsername.getText(), passField.getText(), primaryStage);
		});

		BoxMaker boxMaker = BoxMaker.getInstance();
		HBox hboxUsername = (HBox) boxMaker.makeBox(BoxType.HBOX, Pos.CENTER, 5, lblEnterUsername, txtUsername);
		HBox hboxPass = (HBox) boxMaker.makeBox(BoxType.HBOX, Pos.CENTER, 5, lblEnterPass, passField);
		VBox vboxMain = (VBox) boxMaker.makeBox(BoxType.VBOX, Pos.CENTER, 20, hboxUsername, hboxPass, btnLogin);

		FlowPane pane = new FlowPane();
		pane.getStyleClass().add("flow-pane");
		pane.getChildren().add(vboxMain);

		Scene scene = new Scene(pane, 650, 300);
		scene.getStylesheets().add("style.css");
		primaryStage.setScene(scene);
		primaryStage.setTitle("Login");
		primaryStage.setResizable(false);
		primaryStage.show();
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
			User currentUser = UserService.getInstance().login(username, password);
			if (currentUser != null) {
				stage.close();
				if (currentUser.getType().equals(UserType.ADMIN)) {
					AdminControl.display(currentUser);
				} else { // TUTOR
					AcademicMaterial.display(currentUser);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			SystemNotification.display(SystemNotificationType.ERROR,
					Constants.UNEXPECTED_ERROR + e.getClass().getName());
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
