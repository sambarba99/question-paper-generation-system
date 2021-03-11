package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

import view.enums.UserAction;

/**
 * This class is a singleton, the use of which is to generate Buttons used in UI pages.
 *
 * @author Sam Barba
 */
public class ButtonMaker {

	private static ButtonMaker instance;

	/**
	 * Make a Button with defined parameters
	 * 
	 * @param width      - the width of the button
	 * @param height     - the height of the button
	 * @param userAction - the UserAction of the button, used to get the text on the button
	 * @param action     - the action to trigger when clicked
	 * @return a Button with the specified parameters
	 */
	public Button makeButton(double width, double height, UserAction userAction, EventHandler<ActionEvent> action) {
		Button b = new Button(userAction.getStrVal());
		b.setMinWidth(width);
		b.setMaxWidth(width);
		b.setMinHeight(height);
		b.setMaxHeight(height);
		b.setOnAction(action);

		return b;
	}

	public synchronized static ButtonMaker getInstance() {
		if (instance == null) {
			instance = new ButtonMaker();
		}
		return instance;
	}

	private ButtonMaker() {
	}
}
