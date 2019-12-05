package themes;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * An interface the defines the various colors that the Wicked Scheduler user
 * interface can take on.
 */
public abstract class Theme {

	/**
	 * This is the icon for all windows that the Wicked Scheduler will open
	 * 
	 * @return An Image representing the icon
	 */
	public Image getIcon() {
		return new Image(Theme.class.getResourceAsStream("icon.png"));
	}

	/**
	 * This is the background color for the UI
	 * 
	 * @return A color for the background
	 */
	public abstract Color backgroundColor();

	/**
	 * Color for the Tab header area. By default this is a lighter of darker
	 * [depending on the perceived brightness] color of the
	 * {@link #backgroundColor()}
	 * 
	 * @return The Tab header area color
	 */
	public Color tabHeaderColor() {
		if (perceivedBrightness(backgroundColor()) > 0.5) {
			return backgroundColor().darker().darker();
		} else {
			return backgroundColor().brighter().brighter();
		}
	}

	/**
	 * Returns the text color for anything directly on the main area. By default
	 * this is either black or white depending on the perceived brightness of
	 * {@link #backgroundColor()}
	 * 
	 * @return
	 */
	public Color textColor() {
		return textColor(backgroundColor());
	}

	/**
	 * The button color for the add course button
	 * 
	 * @return The button color
	 */
	public abstract Color addCourseColor();

	/**
	 * The text color for the add course button. By default this is black or white
	 * depending on the perceived brightness of the {@link #addCourseColor()}
	 * 
	 * @return The text color for the button
	 */
	public Color addCourseTextColor() {
		return textColor(addCourseColor());
	}

	/**
	 * Returns both {@link #addCourseColor()} and {@link #addCourseTextColor()} in a
	 * Color[]
	 * 
	 * @return Color[] representing the colors for the add course button
	 */
	public Color[] addCourseColors() {
		return new Color[] { addCourseColor(), addCourseTextColor() };
	}

	/**
	 * The button color for the remove course button. But default this is the
	 * inverse of {@link #addCourseColor()}
	 * 
	 * @return The button color
	 */
	public Color removeCourseColor() {
		return addCourseColor().invert();
	}

	/**
	 * The text color for the remove course button. By default this is black or
	 * white depending on the perceived brightness of the
	 * {@link #removeCourseColor()}
	 * 
	 * @return The text color
	 */
	public Color removeCourseTextColor() {
		return textColor(removeCourseColor());
	}

	/**
	 * Returns both {@link #removeCourseColor()} and
	 * {@link #removeCourseTextColor()} in a Color[]
	 * 
	 * @return Color[] representing the colors for the remove course button
	 */
	public Color[] removeCourseColors() {
		return new Color[] { removeCourseColor(), removeCourseTextColor() };
	}

	/**
	 * The button color for the schedule course button
	 * 
	 * @return The button color
	 */
	public abstract Color scheduleButtonColor();

	/**
	 * The text color for the schedule course button. By default this is black or
	 * white depending on the perceived brightness of the
	 * {@link #scheduleButtonColor()}
	 * 
	 * @return The text color
	 */
	public Color scheduleButtonTextColor() {
		return textColor(scheduleButtonColor());
	}

	/**
	 * Returns both {@link #scheduleButtonColor()} and
	 * {@link #scheduleButtonTextColor()} in a Color[]
	 * 
	 * @return Color[] representing the colors for the schedule course button
	 */
	public Color[] scheduleButtonColors() {
		return new Color[] { scheduleButtonColor(), scheduleButtonTextColor() };
	}

	/**
	 * This is the text color for the credits if you are between 12-18 credits. By
	 * default this is just {@link #textColor()}
	 * 
	 * @return The credits label text color if "valid"
	 */
	public Color creditValidColor() {
		return textColor();
	}

	/**
	 * This is the text color for the credits if you are not between 12-18 credits.
	 * By default this is just {@link #removeCourseColor()}
	 * 
	 * @return The credits label text color if "invalid"
	 */
	public Color creditInvalidColor() {
		return removeCourseColor();
	}

	/**
	 * The button color for the back course button
	 * 
	 * @return The button color
	 */
	public Color backButtonColor() {
		return scheduleButtonColor();
	}

	/**
	 * The text color for the back course button. By default this is black or white
	 * depending on the perceived brightness of the {@link #backButtonColor()}
	 * 
	 * @return The text color
	 */
	public Color backButtonTextColor() {
		return textColor(backButtonColor());
	}

	/**
	 * Returns both {@link #backButtonColor()} and {@link #backButtonTextColor()} in
	 * a Color[]
	 * 
	 * @return Color[] representing the colors for the back course button
	 */
	public Color[] backButtonColors() {
		return new Color[] { backButtonColor(), backButtonTextColor() };
	}

	/**
	 * Converts a color object into a hexadecimal string representational view
	 * 
	 * @param c The color to convert
	 * @return The color in hexadecimal form
	 */
	private static String toString(Color c) {
		return "#" + (toHex(c.getRed()) + toHex(c.getGreen()) + toHex(c.getBlue()));
	}

	/**
	 * Turns a Color into a background style string
	 * 
	 * @param c The color to convert
	 * @return A JavaFX background color CSS style string
	 */
	public static String toBackgroundStyle(Color c) {
		return "-fx-background-color: " + toString(c) + ";";
	}

	/**
	 * Turns a Color[] into a button and text color style string
	 * 
	 * @param c The colors to convert
	 * @return A JavaFX background color and text CSS style string
	 */
	public static String toStyle(Color[] c) {
		return "-fx-background-color: " + toString(c[0]) + ";" + "-fx-text-background-color: " + toString(c[1]) + ";";
	}

	/**
	 * Turns a Color into a text background color style string
	 * 
	 * @param c The color to convert
	 * @return A JavaFX text background CSS style string
	 */
	public static String toTextStyle(Color c) {
		return "-fx-text-background-color: " + toString(c) + ";";
	}

	/**
	 * Converts a double value from 0-1 to 0-255 and then into a hexadecimal value
	 * 
	 * @param val The [0-1] double value
	 * @return The [00-FF] hexadecimal value
	 */
	private static String toHex(double val) {
		String conv = Integer.toHexString((int) Math.round(val * 255));
		conv = conv.toUpperCase();
		return conv.length() == 1 ? "0" + conv : conv; // Single digits, may need to add 0 to beginning if so
	}

	/**
	 * Returns a text color that will be readable against the given color c. This
	 * uses {@link #perceivedBrightness(Color)} to get the perceived brightness of
	 * the input color and returns black if its bright and white if not
	 * 
	 * @param c The color to return a valid text color for
	 * @return A readable text color for the given color
	 */
	private static Color textColor(Color c) {
		double perBri = perceivedBrightness(c);
		if (perBri > 0.5) {
			return Color.BLACK;
		} else {
			return Color.WHITE;
		}
	}

	/**
	 * Returns the perceived brightness of a color based on a weighted distance
	 * within the 3D RGB color space based on the formula
	 * sqrt(0.299*R^2+0.587*G^2+0.114*B^2)
	 * 
	 * @param c The color to get the perceived brightness of
	 * @return The perceived brightness as a double from [0-1]
	 */
	private static double perceivedBrightness(Color c) {
		return Math.sqrt(0.299 * c.getRed() * c.getRed() + 0.587 * c.getGreen() * c.getGreen() + 0.114 * c.getBlue() * c.getBlue());
	}

}
