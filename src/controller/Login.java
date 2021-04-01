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
import model.rawquestiontransformation.RawQuestionTransformer;
import model.service.UserService;

import view.SystemNotification;
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

	@Override
	public void start(Stage primaryStage) {
		Label lblEnterUsername = new Label("Enter username:");
		TextField txtUsername = new TextField();
		Label lblEnterPass = new Label("Enter password:");
		PasswordField passField = new PasswordField();

		txtUsername.setMaxWidth(200);
		txtUsername.textProperty().addListener((obs, oldText, newText) -> {
			txtUsername.setText(newText.toLowerCase());
		});
		passField.setMaxWidth(200);

		Button btnLogin = new ButtonBuilder().withWidth(100).withUserAction(UserAction.LOG_IN).withActionEvent(e -> {
			login(txtUsername.getText(), passField.getText(), primaryStage);
		}).build();

		VBox vboxCredentials = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.CENTER_LEFT)
			.withSpacing(10)
			.withNodes(lblEnterUsername, txtUsername, lblEnterPass, passField)
			.build();
		HBox hboxCredsAndLogin = (HBox) new PaneBuilder().withBoxType(BoxType.HBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(30)
			.withNodes(vboxCredentials, btnLogin)
			.build();
		VBox vboxMain = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(40)
			.withNodes(LogoMaker.makeLogo(400), hboxCredsAndLogin)
			.build();

		Scene scene = new Scene(vboxMain, 600, 500);
		scene.getStylesheets().add(Constants.CSS_STYLE_PATH);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Login");
		primaryStage.setResizable(false);

		/*
		 * If this is first-time system access, the first user needs to be created, and questions and subjects CSV files
		 * (for demo purposes)
		 */
		if (!UserService.getInstance().usersFileExists()) {
			SystemNotification.display(SystemNotificationType.NEUTRAL, "Adding multiple-choice questions...");
			RawQuestionTransformer.transformAndSaveRawQuestions();
			SystemNotification.display(SystemNotificationType.NEUTRAL,
				"Welcome. You are the first user (an admin). Set a secure password.");
		}

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
			Optional<User> currentUser = UserService.getInstance().login(username, password);

			if (currentUser.isPresent()) {
				UserPrivilege userPrivilege = currentUser.get().getPrivilege();

				switch (userPrivilege) {
					case ADMIN:
						stage.close();
						AdminPanel.display(currentUser.get());
						break;
					case TUTOR:
						stage.close();
						AcademicMaterialManagement.display(currentUser.get());
						break;
					default:
						SystemNotification.display(SystemNotificationType.ERROR,
							Constants.UNEXPECTED_ERROR + "Invalid User Privilege passed: " + userPrivilege.toString());
						throw new IllegalArgumentException(
							"Invalid User Privilege passed: " + userPrivilege.toString());
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
