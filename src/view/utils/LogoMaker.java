package view.utils;

import java.io.File;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * This class is used to add logos wherever necessary.
 *
 * @author Sam Barba
 */
public class LogoMaker {

	/**
	 * Create a logo image view object given a width (height not needed, as aspect ratio is always
	 * 2:5).
	 * 
	 * @param width - the width of the ImageView
	 * @return a generated ImageView object containing the logo
	 */
	public static ImageView makeLogo(int width) {
		File logoFile = new File(Constants.LOGO_PATH);
		ImageView imgView = new ImageView(new Image(logoFile.toURI().toString()));
		imgView.setFitWidth(width);
		imgView.setFitHeight(width * 0.4);
		return imgView;
	}
}
