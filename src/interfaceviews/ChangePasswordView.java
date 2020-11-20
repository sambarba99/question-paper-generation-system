package interfaceviews;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import dao.UserDAO;

import model.User;
import model.enums.BoxType;
import model.enums.SystemMessageType;

import tools.BoxMaker;
import tools.SecurityTools;

public class ChangePasswordView {

	private static UserDAO userDao = new UserDAO();

	private static boolean changed;

	public static boolean changePassword(User currentUser) {
		changed = false;
		Stage stage = new Stage();

		Label lblEnterCurrentPass = new Label("Enter current password:");
		PasswordField passFieldCurrent = new PasswordField();
		Label lblEnterNewPass = new Label("Enter new password:");
		PasswordField passFieldNew = new PasswordField();
		Label lblRepeatNewPass = new Label("Repeat new password:");
		PasswordField passFieldRepeat = new PasswordField();
		Button btnUpdate = new Button("Update");

		btnUpdate.setOnAction(action -> {
			try {
				if (SecurityTools.validateResetPassword(currentUser, passFieldCurrent.getText(), passFieldNew.getText(),
						passFieldRepeat.getText())) {
					userDao.updatePassword(currentUser, passFieldNew.getText());
					String newPassHash = userDao.getUserByUsername(currentUser.getUsername()).getPassword();
					currentUser.setPassword(newPassHash);
					changed = true;
					stage.close();
				}
			} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
				SystemMessageView.display(SystemMessageType.ERROR, "Unexpected error: " + e.getClass().getName());
			}
		});

		VBox vboxLbls = (VBox) BoxMaker.makeBox(BoxType.VBOX, Pos.CENTER_RIGHT, 30, lblEnterCurrentPass,
				lblEnterNewPass, lblRepeatNewPass);
		VBox vboxPassFields = (VBox) BoxMaker.makeBox(BoxType.VBOX, Pos.CENTER_LEFT, 20, passFieldCurrent, passFieldNew,
				passFieldRepeat);
		HBox hbox = (HBox) BoxMaker.makeBox(BoxType.HBOX, Pos.CENTER, 5, vboxLbls, vboxPassFields);
		VBox vboxMain = (VBox) BoxMaker.makeBox(BoxType.VBOX, Pos.CENTER, 20, hbox, btnUpdate);

		FlowPane pane = new FlowPane();
		pane.getStyleClass().add("flow-pane");
		pane.getChildren().add(vboxMain);

		Scene scene = new Scene(pane, 600, 300);
		scene.getStylesheets().add("style.css");
		stage.setTitle("Change password");
		stage.setScene(scene);
		stage.setResizable(false);
		// so multiple instances of this window can't be opened
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
		return changed;
	}
}