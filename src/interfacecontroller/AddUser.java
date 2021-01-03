package interfacecontroller;

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

import service.UserService;

import dto.UserTypeDTO;

import model.User;
import model.enums.BoxType;
import model.enums.SystemNotificationType;
import model.enums.UserType;

import utils.BoxMaker;
import utils.Constants;

/**
 * Allows admin users to add new tutor or admin users.
 *
 * @author Sam Barba
 */
public class AddUser {

	private static Stage stage = new Stage();

	private static boolean added;

	/**
	 * Add a new user.
	 * 
	 * @return whether or not a user has been added successfully
	 */
	public static boolean addUser() {
		added = false;

		Label lblEnterUsername = new Label("Enter their username:");
		TextField txtUsername = new TextField();
		Label lblEnterPass = new Label("Enter their temporary password:");
		PasswordField passField = new PasswordField();
		Label lblSelectType = new Label("Select their user type:");
		ChoiceBox cbUserType = new ChoiceBox();
		Button btnAddUser = new Button("Add user");

		cbUserType.getItems().addAll(UserTypeDTO.getInstance().getUserTypeChoiceBoxItems());
		cbUserType.getSelectionModel().selectFirst();
		txtUsername.textProperty().addListener((obs, oldText, newText) -> {
			txtUsername.setText(txtUsername.getText().toLowerCase());
		});
		btnAddUser.setOnAction(action -> {
			UserType userType = UserTypeDTO.getInstance().getSelectedUserType(cbUserType);
			addUser(txtUsername.getText(), passField.getText(), userType);
		});

		BoxMaker boxMaker = BoxMaker.getInstance();
		VBox vboxLbls = (VBox) boxMaker.makeBox(BoxType.VBOX, Pos.CENTER_RIGHT, 30, lblEnterUsername, lblEnterPass,
				lblSelectType);
		VBox vboxCreds = (VBox) boxMaker.makeBox(BoxType.VBOX, Pos.CENTER_LEFT, 20, txtUsername, passField, cbUserType);
		HBox hboxUserCreds = (HBox) boxMaker.makeBox(BoxType.HBOX, Pos.CENTER, 10, vboxLbls, vboxCreds);

		FlowPane pane = new FlowPane();
		pane.getStyleClass().add("flow-pane");
		pane.getChildren().addAll(hboxUserCreds, btnAddUser);

		Scene scene = new Scene(pane, 500, 300);
		scene.getStylesheets().add("style.css");
		stage.setScene(scene);
		stage.setTitle("Add User");
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
				User user = new User(username, password, userType);
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
