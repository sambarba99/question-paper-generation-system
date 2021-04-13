package view.builders;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

import view.enums.SystemNotificationType;
import view.enums.UserAction;

import controller.UserConfirmation;

/**
 * This class utilises the builder pattern, and is used to generate Buttons used in UI pages.
 *
 * @author Sam Barba
 */
public class ButtonBuilder {

    private double width;

    private UserAction userAction;

    private EventHandler<ActionEvent> actionEvent;

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

    public ButtonBuilder withActionEvent(EventHandler<ActionEvent> actionEvent) {
        this.actionEvent = actionEvent;
        return this;
    }

    public Button build() {
        Button btn = new Button(userAction.getStrVal());
        btn.setPrefWidth(width);
        btn.setOnAction(actionEvent);
        return btn;
    }

    /**
     * Create button specifically for exiting application.
     * 
     * @param translateX - x value needed to place it in bottom-right corner
     * @param translateY - y value needed to place it in bottom-right corner
     * @return a button that exits the application when clicked
     */
    public Button buildExitBtn(int translateX, int translateY) {
        Button btn = new Button(UserAction.EXIT_APPLICATION.getStrVal());
        btn.setTranslateX(translateX);
        btn.setTranslateY(translateY);
        btn.setOnAction(e -> {
            UserConfirmation.confirm(SystemNotificationType.CONFIRM_EXIT_APPLICATION);
        });
        btn.getStyleClass().add("btn-exit");
        return btn;
    }
}
