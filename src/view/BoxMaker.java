package view;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import view.enums.BoxType;
import view.enums.SystemNotificationType;

import controller.SystemNotification;

/**
 * This class is a singleton, the use of which is to generate HBox or VBox panes used in UI pages.
 *
 * @author Sam Barba
 */
public class BoxMaker {

	private static BoxMaker instance;

	/**
	 * Make either a HBox or VBox with user-defined parameters
	 * 
	 * @param boxType   - the type of Pane, either HBox or VBox
	 * @param alignment - the alignment of the box
	 * @param spacing   - the spacing of the nodes in the box
	 * @param nodes     - the nodes to be placed in the box (if any)
	 * @return a Box Pane with the specified parameters
	 */
	public Pane makeBox(BoxType boxType, Pos alignment, double spacing, Node... nodes) {
		switch (boxType) {
			case HBOX:
				HBox hbox = new HBox();
				hbox.setAlignment(alignment);
				hbox.setSpacing(spacing);
				if (nodes.length > 0) {
					hbox.getChildren().addAll(nodes);
				}
				return hbox;
			case VBOX:
				VBox vbox = new VBox();
				vbox.setAlignment(alignment);
				vbox.setSpacing(spacing);
				if (nodes.length > 0) {
					vbox.getChildren().addAll(nodes);
				}
				return vbox;
			default:
				SystemNotification.display(SystemNotificationType.ERROR,
					Constants.UNEXPECTED_ERROR + "Invalid box type passed: " + boxType.toString());
				throw new IllegalArgumentException("Invalid box type passed: " + boxType.toString());
		}
	}

	public synchronized static BoxMaker getInstance() {
		if (instance == null) {
			instance = new BoxMaker();
		}
		return instance;
	}

	private BoxMaker() {
	}
}