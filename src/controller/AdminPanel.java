package controller;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import model.dto.UserDTO;
import model.persisted.User;
import model.service.UserService;

import view.SystemNotification;
import view.builders.ButtonBuilder;
import view.builders.PaneBuilder;
import view.enums.BoxType;
import view.enums.SystemNotificationType;
import view.enums.UserAction;
import view.utils.Constants;
import view.utils.LogoMaker;

/**
 * Allows admin users to modify/delete existing users.
 *
 * @author Sam Barba
 */
public class AdminPanel {

	private static Stage stage;

	private static List<UserDTO> userDTOs;

	private static TableView tblUsers = new TableView();

	/**
	 * Display the system high-level user actions to the admin user.
	 * 
	 * @param currentUser - the user currently in session
	 */
	public static void display(User currentUser) {
		stage = new Stage();

		Label lblUsers = new Label("System Users");

		Button btnAddUser = new ButtonBuilder().withWidth(200)
			.withUserAction(UserAction.ADD_NEW_USER)
			.withActionEvent(e -> {
				if (AddUser.addUser()) {
					// if added a new user, refresh users TableView
					refreshUsersTbl();
					SystemNotification.display(SystemNotificationType.SUCCESS, "User added!");
				}
			})
			.build();
		Button btnDelUser = new ButtonBuilder().withWidth(200)
			.withUserAction(UserAction.DELETE_USER)
			.withActionEvent(e -> {
				deleteUser(currentUser);
			})
			.build();
		Button btnOpenAcademicMaterial = new ButtonBuilder().withWidth(200)
			.withUserAction(UserAction.OPEN_ACADEMC_MATERIAL)
			.withActionEvent(e -> {
				AcademicMaterialManagement.display(currentUser);
			})
			.build();
		Button btnUpdatePassword = new ButtonBuilder().withWidth(200)
			.withUserAction(UserAction.UPDATE_PASSWORD)
			.withActionEvent(e -> {
				UpdatePassword.updatePassword(currentUser);
			})
			.build();

		VBox vboxUsersView = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(10)
			.withNodes(lblUsers, tblUsers)
			.build();
		VBox vboxActions = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(10)
			.withNodes(btnAddUser, btnDelUser, btnOpenAcademicMaterial, btnUpdatePassword,
				new ButtonBuilder().buildExitBtn(112, 92))
			.build();
		HBox hboxViewAndActions = (HBox) new PaneBuilder().withBoxType(BoxType.HBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(20)
			.withNodes(vboxUsersView, vboxActions)
			.build();
		VBox vboxMain = (VBox) new PaneBuilder().withBoxType(BoxType.VBOX)
			.withAlignment(Pos.CENTER)
			.withSpacing(30)
			.withNodes(LogoMaker.makeLogo(300), hboxViewAndActions)
			.build();

		setup();

		Scene scene = new Scene(vboxMain, 750, 550);
		scene.getStylesheets().add(Constants.CSS_STYLE_PATH);
		stage.setScene(scene);
		stage.setTitle("Administration");
		stage.setResizable(false);
		stage.show();
	}

	/**
	 * Delete a user with confirmation.
	 * 
	 * @param currentUser - the user currently in session
	 */
	private static void deleteUser(User currentUser) {
		if (tblUsers.getSelectionModel().getSelectedItems().size() != 1) {
			SystemNotification.display(SystemNotificationType.ERROR, "Please select 1 user.");
		} else {
			UserDTO userDto = (UserDTO) tblUsers.getSelectionModel().getSelectedItem();
			String username = userDto.getUsername();

			if (username.equals(currentUser.getUsername())) {
				SystemNotification.display(SystemNotificationType.ERROR, "You can't delete yourself!");
			} else if (UserConfirmation.confirm(SystemNotificationType.CONFIRM_DELETION, "user")) {
				UserService.getInstance().deleteUserByUsername(username);
				refreshUsersTbl();
				SystemNotification.display(SystemNotificationType.SUCCESS, "User '" + username + "' deleted.");
			}
		}
	}

	/**
	 * Set up window.
	 */
	private static void setup() {
		TableColumn<UserDTO, String> colUsername = new TableColumn<>("Username");
		TableColumn<UserDTO, String> colPrivilege = new TableColumn<>("Privilege");
		TableColumn<UserDTO, String> colDateCreated = new TableColumn<>("Date created");

		colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
		colPrivilege.setCellValueFactory(new PropertyValueFactory<>("privilege"));
		colDateCreated.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));

		colUsername.setPrefWidth(150);
		colPrivilege.setPrefWidth(150);
		colDateCreated.setPrefWidth(150);

		tblUsers.getColumns().addAll(colUsername, colPrivilege, colDateCreated);
		tblUsers.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		tblUsers.setPrefSize(452, 300);
		tblUsers.setEditable(false);

		refreshUsersTbl();
	}

	/**
	 * Refresh TableView of users.
	 */
	private static void refreshUsersTbl() {
		userDTOs = UserService.getInstance().getAllUserDTOs();
		tblUsers.getItems().clear();
		tblUsers.getItems().addAll(userDTOs);
	}
}
