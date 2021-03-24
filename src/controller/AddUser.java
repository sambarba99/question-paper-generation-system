package controller;

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
import view.enums.UserPrivilege;
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
		Label lblSelectPrivilege = new Label("Select their privilege:");
		ChoiceBox choicePrivilege = new ChoiceBox();

		txtUsername.textProperty().addListener((obs, oldText, newText) -> {
			txtUsername.setText(newText.toLowerCase());
		});
		choicePrivilege.getItems()
			.addAll(Arrays.asList(UserPrivilege.values())
				.stream()
				.map(UserPrivilege::toString)
				.collect(Collectors.toList()));
		choicePrivilege.getSelectionModel().selectFirst();
		choicePrivilege.setPrefWidth(100);

		Button btnAddUser = new ButtonBuilder().withWidth(100).withUserAction(UserAction.ADD).withActionEvent(e -> {
			UserPrivilege privilege = UserPrivilege
				.getFromStr(choicePrivilege.getSelectionModel().getSelectedItem().toString());
			prepareDataAndAddUser(txtUsername.getText(), passField.getText(), privilege);
		}).build();

		VBox vboxLbls = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.CENTER_RIGHT)
			.withSpacing(30)
			.withNodes(lblEnterUsername, lblEnterPass, lblSelectPrivilege)
			.build();
		VBox vboxCreds = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.CENTER_LEFT)
			.withSpacing(20)
			.withNodes(txtUsername, passField, choicePrivilege)
			.build();
		HBox hboxUserCreds = (HBox) new PaneBuilder().withBoxType(BoxType.HBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(10)
			.withNodes(vboxLbls, vboxCreds)
			.build();
		VBox vboxMain = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(20)
			.withNodes(hboxUserCreds, btnAddUser)
			.build();

		Scene scene = new Scene(vboxMain, 500, 300);
		scene.getStylesheets().add(Constants.CSS_STYLE_PATH);
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
	 * @param username  - the new user's username
	 * @param password  - the new user's raw password
	 * @param privilege - the new user's privilege level
	 */
	private static void prepareDataAndAddUser(String username, String password, UserPrivilege privilege) {
		try {
			if (UserService.getInstance().validateAddNewUserCreds(username, password)) {
				User user = new UserBuilder().withUsername(username)
					.withPassword(password)
					.withPrivilege(privilege)
					.build();

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
