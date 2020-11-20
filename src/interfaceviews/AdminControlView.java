package interfaceviews;

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

import dao.UserDAO;

import dto.UserDTO;

import model.User;
import model.enums.BoxType;
import model.enums.SystemMessageType;

import utils.BoxMaker;

public class AdminControlView {

	private static UserDAO userDao = new UserDAO();

	private static UserDTO userDto = new UserDTO();

	private static ListView<String> listViewUsers = new ListView<>();

	public static void display(User currentUser) {
		Label lblViewModifyUsers = new Label("Modify users");
		Button btnAddUser = new Button("Add user");
		Button btnDelUser = new Button("Delete user");
		Button btnTutorView = new Button("Open Tutor view");
		Button btnChangePassword = new Button("Change password");

		btnAddUser.setOnAction(action -> {
			if (AddUserView.addUser()) {
				listViewUsers.getItems().clear();
				listViewUsers.getItems().addAll(userDto.getUserListViewItems());
				SystemMessageView.display(SystemMessageType.SUCCESS, "User added!");
			}
		});
		btnDelUser.setOnAction(action -> {
			if (listViewUsers.getSelectionModel().isEmpty()) {
				SystemMessageView.display(SystemMessageType.ERROR, "Please select a user.");
			} else {
				String user = listViewUsers.getSelectionModel().getSelectedItem();
				String username = user.split(" ")[0];

				if (username.equals(currentUser.getUsername())) {
					SystemMessageView.display(SystemMessageType.ERROR, "You can't delete yourself.");
				} else if (DeletionConfirmView.confirmDelete("user")) {
					userDao.deleteUserByUsername(username);
					listViewUsers.getItems().clear();
					listViewUsers.getItems().addAll(userDto.getUserListViewItems());
					SystemMessageView.display(SystemMessageType.SUCCESS, "User deleted.");
				}
			}
		});
		btnTutorView.setOnAction(action -> {
			TutorControlView.display(currentUser);
		});
		btnChangePassword.setOnAction(action -> {
			if (ChangePasswordView.changePassword(currentUser)) {
				SystemMessageView.display(SystemMessageType.SUCCESS, "Password updated.");
			}
		});

		HBox hboxUserCtrlBtns = (HBox) BoxMaker.makeBox(BoxType.HBOX, Pos.CENTER, 5, btnAddUser, btnDelUser);
		VBox vboxUsersView = (VBox) BoxMaker.makeBox(BoxType.VBOX, Pos.CENTER, 10, lblViewModifyUsers, listViewUsers,
				hboxUserCtrlBtns);
		VBox vboxMiscOptions = (VBox) BoxMaker.makeBox(BoxType.VBOX, Pos.CENTER, 5, btnTutorView, btnChangePassword);

		FlowPane pane = new FlowPane();
		pane.getStyleClass().add("flow-pane");
		pane.getChildren().addAll(vboxUsersView, vboxMiscOptions);

		listViewUsers.getItems().addAll(userDto.getUserListViewItems());
		listViewUsers.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		Stage stage = new Stage();
		Scene scene = new Scene(pane, 500, 550);
		scene.getStylesheets().add("style.css");
		stage.setScene(scene);
		stage.setTitle("Admin Control");
		stage.setResizable(false);
		stage.show();
	}
}