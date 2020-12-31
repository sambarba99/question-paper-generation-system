package interfacecontroller;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import service.UserService;

import dto.UserDTO;

import model.User;
import model.enums.BoxType;
import model.enums.SystemNotificationType;

import utils.BoxMaker;
import utils.Constants;

/**
 * Allows admin users to modify/delete existing users.
 *
 * @author Sam Barba
 */
public class AdminControl {

	private static Stage stage = new Stage();

	private static ListView<String> listViewUsers = new ListView<>();

	/**
	 * Display the system high-level options to the admin user.
	 * 
	 * @param currentUser - the user currently in session
	 */
	public static void display(User currentUser) {
		Label lblViewModifyUsers = new Label("Modify users");
		Button btnAddUser = new Button("Add user");
		Button btnDelUser = new Button("Delete user");
		Button btnViewAcademicMaterial = new Button("View Academic Material");
		Button btnUpdatePassword = new Button("Update password");

		btnAddUser.setOnAction(action -> {
			addUser();
		});
		btnDelUser.setOnAction(action -> {
			deleteUser(currentUser);
		});
		btnViewAcademicMaterial.setOnAction(action -> {
			AcademicMaterial.display(currentUser);
		});
		btnUpdatePassword.setOnAction(action -> {
			if (UpdatePassword.updatePassword(currentUser)) {
				SystemNotification.display(SystemNotificationType.SUCCESS, "Password updated.");
			}
		});

		BoxMaker boxMaker = BoxMaker.getInstance();
		HBox hboxUserCtrlBtns = (HBox) boxMaker.makeBox(BoxType.HBOX, Pos.CENTER, 5, btnAddUser, btnDelUser);
		VBox vboxUsersView = (VBox) boxMaker.makeBox(BoxType.VBOX, Pos.CENTER, 10, lblViewModifyUsers, listViewUsers,
				hboxUserCtrlBtns);
		VBox vboxMiscOptions = (VBox) boxMaker.makeBox(BoxType.VBOX, Pos.CENTER, 5, btnViewAcademicMaterial,
				btnUpdatePassword);

		FlowPane pane = new FlowPane();
		pane.getStyleClass().add("flow-pane");
		pane.getChildren().addAll(vboxUsersView, vboxMiscOptions);

		listViewUsers.getItems().addAll(UserDTO.getInstance().getUserListViewItems());
		listViewUsers.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		Scene scene = new Scene(pane, 500, 550);
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
