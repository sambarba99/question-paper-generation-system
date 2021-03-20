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

import model.persisted.User;
import model.service.UserService;

import view.SystemNotification;
import view.builders.ButtonBuilder;
import view.builders.PaneBuilder;
import view.enums.BoxType;
import view.enums.SystemNotificationType;
import view.enums.UserAction;
import view.utils.Constants;

/**
 * Allows admin users to modify/delete existing users.
 *
 * @author Sam Barba
 */
public class AdminControl {

	private static Stage stage;

	private static ListView<String> listViewUsers = new ListView<>();

	/**
	 * Display the system high-level user actions to the admin user.
	 * 
	 * @param currentUser - the user currently in session
	 */
	public static void display(User currentUser) {
		stage = new Stage();

		Label lblActions = new Label("Actions");
		Label lblUsers = new Label("System Users");

		Button btnAddUser = new ButtonBuilder().withWidth(200)
			.withUserAction(UserAction.ADD_NEW_USER)
			.withClickAction(action -> {
				addUser();
			})
			.build();
		Button btnDelUser = new ButtonBuilder().withWidth(200)
			.withUserAction(UserAction.DELETE_USER)
			.withClickAction(action -> {
				deleteUser(currentUser);
			})
			.build();
		Button btnViewAcademicMaterial = new ButtonBuilder().withWidth(200)
			.withUserAction(UserAction.OPEN_ACADEMC_MATERIAL)
			.withClickAction(action -> {
				AcademicMaterialManagement.display(currentUser);
			})
			.build();
		Button btnUpdatePassword = new ButtonBuilder().withWidth(200)
			.withUserAction(UserAction.UPDATE_PASSWORD)
			.withClickAction(action -> {
				UpdatePassword.updatePassword(currentUser);
			})
			.build();

		VBox vboxUsersView = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(10)
			.withNodes(lblUsers, listViewUsers)
			.build();
		VBox vboxActions = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.TOP_CENTER)
			.withSpacing(10)
			.withNodes(lblActions, btnAddUser, btnDelUser, btnViewAcademicMaterial, btnUpdatePassword)
			.build();

		FlowPane pane = new FlowPane();
		pane.getStyleClass().add("flow-pane");
		pane.getChildren().addAll(vboxUsersView, vboxActions);

		listViewUsers.getItems().addAll(UserService.getInstance().getUserListViewItems());
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
			listViewUsers.getItems().addAll(UserService.getInstance().getUserListViewItems());
			SystemNotification.display(SystemNotificationType.SUCCESS, "User added!");
		}
	}

	/**
	 * Delete a user with confirmation.
	 * 
	 * @param currentUser - the user currently in session
	 */
	private static void deleteUser(User currentUser) {
		if (listViewUsers.getSelectionModel().getSelectedItems().size() != 1) {
			SystemNotification.display(SystemNotificationType.ERROR, "Please select 1 user.");
		} else {
			String user = listViewUsers.getSelectionModel().getSelectedItem();
			String username = user.split(Constants.SPACE)[0];

			if (username.equals(currentUser.getUsername())) {
				SystemNotification.display(SystemNotificationType.ERROR, "You can't delete yourself.");
			} else if (DeletionConfirm.confirmDelete("user")) {
				UserService.getInstance().deleteUserByUsername(username);
				listViewUsers.getItems().clear();
				listViewUsers.getItems().addAll(UserService.getInstance().getUserListViewItems());
				SystemNotification.display(SystemNotificationType.SUCCESS, "User '" + username + "' deleted.");
			}
		}
	}
}
