package controller;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import model.dto.UserDTO;
import model.persisted.User;
import model.service.UserService;

import view.BoxMaker;
import view.ButtonMaker;
import view.Constants;
import view.enums.BoxType;
import view.enums.SystemNotificationType;
import view.enums.UserAction;

/**
 * Allows admin users to modify/delete existing users.
 *
 * @author Sam Barba
 */
public class AdminControl {

	private static Stage stage;

	private static ListView<String> listViewUsers = new ListView<>();

	/**
	 * Display the system high-level options to the admin user.
	 * 
	 * @param currentUser - the user currently in session
	 */
	public static void display(User currentUser) {
		stage = new Stage();

		Label lblActions = new Label("Actions");
		Label lblUsers = new Label("System Users");

		Button btnAddUser = ButtonMaker.getInstance().makeButton(200, Constants.BTN_HEIGHT, UserAction.ADD_NEW_USER,
			action -> {
				addUser();
			});
		Button btnDelUser = ButtonMaker.getInstance().makeButton(200, Constants.BTN_HEIGHT, UserAction.DELETE_USER,
			action -> {
				deleteUser(currentUser);
			});
		Button btnViewAcademicMaterial = ButtonMaker.getInstance().makeButton(200, Constants.BTN_HEIGHT,
			UserAction.GO_ACADEMC_MATERIAL, action -> {
				AcademicMaterialManagement.display(currentUser);
			});
		Button btnUpdatePassword = ButtonMaker.getInstance().makeButton(200, Constants.BTN_HEIGHT,
			UserAction.UPDATE_PASSWORD, action -> {
				if (UpdatePassword.updatePassword(currentUser)) {
					SystemNotification.display(SystemNotificationType.SUCCESS, "Password updated.");
				}
			});

		BoxMaker boxMaker = BoxMaker.getInstance();
		VBox vboxUsersView = (VBox) boxMaker.makeBox(BoxType.VBOX, Pos.CENTER, 10, lblUsers, listViewUsers);
		VBox vboxActions = (VBox) boxMaker.makeBox(BoxType.VBOX, Pos.TOP_CENTER, 10, lblActions, btnAddUser, btnDelUser,
			btnViewAcademicMaterial, btnUpdatePassword);

		FlowPane pane = new FlowPane();
		pane.getStyleClass().add("flow-pane");
		pane.getChildren().addAll(vboxUsersView, vboxActions);

		listViewUsers.getItems().addAll(UserDTO.getInstance().getUserListViewItems());
		listViewUsers.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		Scene scene = new Scene(pane, 550, 500);
		scene.getStylesheets().add("style.css");
		stage.setScene(scene);
		stage.setTitle("Admin Control");
		stage.setResizable(false);
		stage.show();
	}

	/**
	 * Open the 'Add User' window.
	 */
	private static void addUser() {
		if (AddUser.addUser()) {
			listViewUsers.getItems().clear();
			listViewUsers.getItems().addAll(UserDTO.getInstance().getUserListViewItems());
			SystemNotification.display(SystemNotificationType.SUCCESS, "User added!");
		}
	}

	/**
	 * Delete a user with confirmation.
	 * 
	 * @param currentUser - the user currently in session
	 */
	private static void deleteUser(User currentUser) {
		if (listViewUsers.getSelectionModel().isEmpty()) {
			SystemNotification.display(SystemNotificationType.ERROR, "Please select a user.");
		} else {
			String user = listViewUsers.getSelectionModel().getSelectedItem();
			String username = user.split(Constants.SPACE)[0];

			if (username.equals(currentUser.getUsername())) {
				SystemNotification.display(SystemNotificationType.ERROR, "You can't delete yourself.");
			} else if (DeletionConfirm.confirmDelete("user")) {
				UserService.getInstance().deleteUserByUsername(username);
				listViewUsers.getItems().clear();
				listViewUsers.getItems().addAll(UserDTO.getInstance().getUserListViewItems());
				SystemNotification.display(SystemNotificationType.SUCCESS, "User deleted.");
			}
		}
	}
}
