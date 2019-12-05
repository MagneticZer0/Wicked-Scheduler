package themes;

import javafx.scene.paint.Color;

/**
 * This theme is the default theme for the scheduler
 */
public class DefaultTheme extends Theme {

	/**
	 * This is the background color for the window
	 */
	@Override
	public Color backgroundColor() {
		return Color.web("#E9EEFF");
	}

	/**
	 * This is the button color for the add course button
	 */
	@Override
	public Color addCourseColor() {
		return Color.web("#32CD32");
	}

	/**
	 * This is the button color for the remove course button
	 */
	@Override
	public Color removeCourseColor() {
		return Color.web("#FF0000").desaturate();
	}

	/**
	 * This is the button color for the schedule course button
	 */
	@Override
	public Color scheduleButtonColor() {
		return Color.web("#72A5FF");
	}

}
