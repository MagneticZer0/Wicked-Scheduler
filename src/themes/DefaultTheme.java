package themes;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class DefaultTheme extends Theme {

	@Override
	public Color backgroundColor() {
		return Color.web("#E9EEFF");
	}

	@Override
	public Color addCourseColor() {
		return Color.web("#32CD32");
	}

	@Override
	public Color removeCourseColor() {
		return Color.web("#FF0000");
	}

	@Override
	public Color scheduleButtonColor() {
		return Color.web("#72A5FF");
	}

}
