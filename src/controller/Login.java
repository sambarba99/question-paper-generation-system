package controller;

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

import model.persisted.User;
import model.service.UserService;

import view.Constants;
import view.builders.ButtonBuilder;
import view.builders.PaneBuilder;
import view.enums.BoxType;
import view.enums.SystemNotificationType;
import view.enums.UserAction;
import view.enums.UserType;

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

		txtUsername.textProperty().addListener((obs, oldText, newText) -> {
			txtUsername.setText(newText.toLowerCase());
		});

		Button btnLogin = new ButtonBuilder().withWidth(76).withUserAction(UserAction.LOG_IN)
			.withActionEvent(action -> {
				login(txtUsername.getText(), passField.getText(), primaryStage);
			}).build();

		HBox hboxUsername = (HBox) new PaneBuilder().withBoxType(BoxType.HBOX).withAlignment(Pos.CENTER).withSpacing(5)
			.withNodes(lblEnterUsername, txtUsername).build();
		HBox hboxPass = (HBox) new PaneBuilder().withBoxType(BoxType.HBOX).withAlignment(Pos.CENTER).withSpacing(5)
			.withNodes(lblEnterPass, passField).build();
		VBox vboxMain = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX).withAlignment(Pos.CENTER).withSpacing(20)
			.withNodes(hboxUsername, hboxPass, btnLogin).build();

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
				UserType userType = currentUser.getType();
				switch (userType) {
					case ADMIN:
						AdminControl.display(currentUser);
						break;
					case TUTOR:
						AcademicMaterialManagement.display(currentUser);
						break;
					default:
						SystemNotification.display(SystemNotificationType.ERROR,
							Constants.UNEXPECTED_ERROR + "Invalid User Type passed: " + userType.toString());
						throw new IllegalArgumentException("Invalid User Type passed: " + userType.toString());
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
