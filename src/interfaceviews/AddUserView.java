package interfaceviews;

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

import dao.UserDAO;

import model.User;
import model.enums.BoxType;
import model.enums.SystemMessageType;
import model.enums.UserType;

import tools.BoxMaker;
import tools.SecurityTools;

public class AddUserView {

	private static UserDAO userDao = new UserDAO();

	private static boolean added;

	public static boolean addUser() {
		added = false;
		Stage stage = new Stage();

		Label lblEnterUsername = new Label("Enter their username:");
		TextField txtUsername = new TextField();
		Label lblEnterPass = new Label("Enter their temporary password:");
		PasswordField passField = new PasswordField();
		Label lblSelectType = new Label("Select their user type:");
		ChoiceBox cbUserType = new ChoiceBox();
		Button btnAddUser = new Button("Add user");

		cbUserType.getItems().addAll("TUTOR", "ADMIN");
		cbUserType.getSelectionModel().selectFirst();
		txtUsername.textProperty().addListener((obs, oldText, newText) -> {
			txtUsername.setText(txtUsername.getText().toLowerCase());
		});
		btnAddUser.setOnAction(action -> {
			try {
				if (SecurityTools.validateAddNewUserCreds(txtUsername.getText(), passField.getText())) {
					UserType userType = cbUserType.getSelectionModel().getSelectedItem().equals("TUTOR")
							? UserType.TUTOR
							: UserType.ADMIN;
					User user = new User(txtUsername.getText(), passField.getText(), userType);
					userDao.addUser(user);
					added = true;
					stage.close();
				}
			} catch (Exception e) {
				SystemMessageView.display(SystemMessageType.ERROR, "Unexpected error: " + e.getClass().getName());
			}
		});

		VBox vboxLbls = (VBox) BoxMaker.makeBox(BoxType.VBOX, Pos.CENTER_RIGHT, 30, lblEnterUsername, lblEnterPass,
				lblSelectType);
		VBox vboxCreds = (VBox) BoxMaker.makeBox(BoxType.VBOX, Pos.CENTER_LEFT, 20, txtUsername, passField, cbUserType);
		HBox hboxUserCreds = (HBox) BoxMaker.makeBox(BoxType.HBOX, Pos.CENTER, 10, vboxLbls, vboxCreds);

		FlowPane pane = new FlowPane();
		pane.getStyleClass().add("flow-pane");
		pane.getChildren().addAll(hboxUserCreds, btnAddUser);

		Scene scene = new Scene(pane, 500, 300);
		scene.getStylesheets().add("style.css");
		stage.setTitle("Add User");
		stage.setScene(scene);
		stage.setResizable(false);
		// so multiple instances of this window can't be opened
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
		return added;
	}
}