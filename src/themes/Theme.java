package themes;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public abstract class Theme {

	public abstract Color backgroundColor();

	public Color textColor() {
		return textColor(backgroundColor());
	}

	public abstract Color addCourseColor();

	public Color addCourseTextColor() {
		return textColor(addCourseColor());
	}

	public Color[] addCourseColors() {
		return new Color[] {addCourseColor(), addCourseTextColor()};
	}

	public Color removeCourseColor() {
		return Color.rgb((int) Math.round(255-addCourseColor().getRed()*255), (int) Math.round(255-addCourseColor().getGreen()*255), (int) Math.round(255-addCourseColor().getBlue()*255));
	}

	public Color removeCourseTextColor() {
		return textColor(removeCourseColor());
	}

	public Color[] removeCourseColors() {
		return new Color[] {removeCourseColor(), removeCourseTextColor()};
	}

	public Color creditValidColor() {
		return textColor();
	}

	public Color creditInvalidColor() {
		return removeCourseColor();
	}

	public abstract Color scheduleButtonColor();

	public Color scheduleButtonTextColor() {
		return textColor(scheduleButtonColor());
	}

	public Color[] scheduleButtonColors() {
		return new Color[] {scheduleButtonColor(), scheduleButtonTextColor()};
	}

	public Image getIcon() {
		return new Image(Theme.class.getResourceAsStream("icon.png"));
	}

	private static String toString(Color c) {
		return "#" + (toHex(c.getRed()) + toHex(c.getGreen()) + toHex(c.getBlue()));
	}

	private static String toHex(double val) {
		String conv = Integer.toHexString((int) Math.round(val * 255));
		conv = conv.toUpperCase();
		return conv.length() == 1 ? "0" + conv : conv; // Single digits, ma need to add 0 to beginning if so
	}

	public static String toBackgroundStyle(Color c) {
		return "-fx-background-color: " + toString(c) + ";";
	}

	public static String toStyle(Color[] c) {
		return "-fx-background-color: " + toString(c[0]) + ";" + "-fx-text-background-color: " + toString(c[1]) + ";";
	}

	public static String toTextStyle(Color c) {
		return "-fx-text-background-color: " + toString(c) + ";";
	}

	private static Color textColor(Color c) {
		double perBri = perceivedBrightness(c);
		if (perBri > 0.5) {
			return Color.BLACK;
		} else {
			return Color.WHITE;
		}
	}

	private static double perceivedBrightness(Color c) {
		return 0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue();
	}

}
