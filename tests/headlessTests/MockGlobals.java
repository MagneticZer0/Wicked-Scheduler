package headlessTests;

import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;

import logic.Globals;
import themes.DefaultTheme;
import userInterfaces.Browser;
import userInterfaces.Popup;

public class MockGlobals {

	private static Popup popup = mock(Popup.class);

	/**
	 * This is used to mock values within the Globals class because some tests can
	 * be run without the UI and if a value is needed from Globals then it will try
	 * and create Popups, but since it most likely isn't being run by a JavaFX
	 * thread it will fail. In this case, it mocks the browser and popup classes
	 * with no stubbing since returns are unimportant.
	 * 
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	public static void setup() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		Field field = Globals.class.getDeclaredField("theme");
		field.setAccessible(true);
		field.set(null, new DefaultTheme());

		field = Globals.class.getDeclaredField("browser");
		field.setAccessible(true);
		field.set(null, mock(Browser.class));

		field = Globals.class.getDeclaredField("popupException");
		field.setAccessible(true);
		field.set(null, popup);

		field = Globals.class.getDeclaredField("popupText");
		field.setAccessible(true);
		field.set(null, popup);
	}

	public static Popup getMockedPopup() {
		return popup;
	}
}
