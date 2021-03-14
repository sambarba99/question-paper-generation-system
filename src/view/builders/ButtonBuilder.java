package view.builders;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

import view.enums.UserAction;

/**
 * This class utilises the builder pattern, and is used to generate Buttons used in UI pages.
 *
 * @author Sam Barba
 */
public class ButtonBuilder {

	private double width;

	private UserAction userAction;

	private EventHandler<ActionEvent> clickAction;

	public ButtonBuilder() {
	}

	public ButtonBuilder withWidth(double width) {
		this.width = width;
		return this;
	}

	public ButtonBuilder withUserAction(UserAction userAction) {
		this.userAction = userAction;
		return this;
	}

	public ButtonBuilder withClickAction(EventHandler<ActionEvent> clickAction) {
		this.clickAction = clickAction;
		return this;
	}

	public Button build() {
		Button b = new Button(userAction.getStrVal());
		b.setMinWidth(width);
		b.setMaxWidth(width);
		b.setOnAction(clickAction);
		return b;
	}
}
