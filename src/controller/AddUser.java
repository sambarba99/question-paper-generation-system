package controller;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.builders.UserBuilder;
import model.persisted.User;
import model.service.UserService;

import view.SystemNotification;
import view.builders.ButtonBuilder;
import view.builders.PaneBuilder;
import view.enums.BoxType;
import view.enums.SystemNotificationType;
import view.enums.UserAction;
import view.enums.UserType;
import view.utils.Constants;

/**
 * Allows admin users to add new tutor or admin users.
 *
 * @author Sam Barba
 */
public class AddUser {

	private static Stage stage;

	private static boolean added;

	/**
	 * Add a new user.
	 * 
	 * @return whether or not a user has been added successfully
	 */
	public static boolean addUser() {
		stage = new Stage();
		added = false;

		Label lblEnterUsername = new Label("Enter their username:");
		TextField txtUsername = new TextField();
		Label lblEnterPass = new Label("Enter their temporary password:");
		PasswordField passField = new PasswordField();
		Label lblSelectType = new Label("Select their user type:");
		ChoiceBox choiceUserType = new ChoiceBox();

		txtUsername.textProperty().addListener((obs, oldText, newText) -> {
			txtUsername.setText(newText.toLowerCase());
		});
		choiceUserType.getItems().addAll(UserService.getInstance().getUserTypeChoiceBoxItems());
		choiceUserType.getSelectionModel().selectFirst();
		choiceUserType.setMinWidth(100);
		choiceUserType.setMaxWidth(100);

		Button btnAddUser = new ButtonBuilder().withWidth(100)
			.withUserAction(UserAction.ADD)
			.withClickAction(action -> {
				UserType userType = UserService.getInstance().getSelectedUserType(choiceUserType);
				addUser(txtUsername.getText(), passField.getText(), userType);
			})
			.build();

		VBox vboxLbls = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.CENTER_RIGHT)
			.withSpacing(30)
			.withNodes(lblEnterUsername, lblEnterPass, lblSelectType)
			.build();
		VBox vboxCreds = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.CENTER_LEFT)
			.withSpacing(20)
			.withNodes(txtUsername, passField, choiceUserType)
			.build();
		HBox hboxUserCreds = (HBox) new PaneBuilder().withBoxType(BoxType.HBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(10)
			.withNodes(vboxLbls, vboxCreds)
			.build();

		FlowPane pane = new FlowPane();
		pane.getStyleClass().add("flow-pane");
		pane.getChildren().addAll(hboxUserCreds, btnAddUser);

		Scene scene = new Scene(pane, 500, 300);
		scene.getStylesheets().add("style.css");
		stage.setScene(scene);
		stage.setTitle("Add New User");
		stage.setResizable(false);
		// so multiple instances of this window can't be opened
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
		return added;
	}

	/**
	 * Add a new user.
	 * 
	 * @param username - the new user's username
	 * @param password - the new user's raw password
	 * @param userType - the new user's user type, i.e. tutor or admin
	 */
	private static void addUser(String username, String password, UserType userType) {
		try {
			if (UserService.getInstance().validateAddNewUserCreds(username, password)) {
				User user = new UserBuilder().withUsername(username).withPassword(password).withType(userType).build();
				user.encryptPassword(); // apply SHA-512 before adding
				UserService.getInstance().addUser(user);
				added = true;
				stage.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + e.getClass().getName());
		}
	}
}
