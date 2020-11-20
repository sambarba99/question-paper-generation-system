package tools;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import model.enums.BoxType;

public class BoxMaker {

	public static Pane makeBox(BoxType boxType, Pos alignment, double spacing, Node... nodes) {
		switch (boxType) {
			case HBOX:
				HBox hbox = new HBox();
				hbox.setAlignment(alignment);
				hbox.setSpacing(spacing);
				hbox.getChildren().addAll(nodes);
				return hbox;
			case VBOX:
				VBox vbox = new VBox();
				vbox.setAlignment(alignment);
				vbox.setSpacing(spacing);
				vbox.getChildren().addAll(nodes);
				return vbox;
			default:
				return null;
		}
	}
}
