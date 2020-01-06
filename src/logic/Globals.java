package logic;

import themes.DefaultTheme;
import themes.Theme;
import userInterfaces.Browser;
import userInterfaces.Popup;

/**
 * Stores various fields that are used globally throughout the scheduler
 */
public class Globals {

	/**
	 * The theme that the UIs are using
	 */
	private static Theme theme;
	/**
	 * The browser used to show course descriptions
	 */
	private static Browser browser;
	/**
	 * This is the popup that will log exceptions
	 */
	private static Popup popupException;
	/**
	 * This is the popup that will show messages to the user
	 */
	private static Popup popupText;
	/**
	 * The version number 
	 */
	private static final String version = "v1.2";

	/**
	 * Since the class is static for all intents and purposes there is no need to
	 * allow instantiation
	 */
	private Globals() {
		throw new UnsupportedOperationException("This is a static class and cannot be instantiated!");
	}

	/**
	 * Initializes all the global fields with the proper values
	 */
	public static void init() {
		theme = new DefaultTheme();
		browser = new Browser();
		popupException = new Popup("Error Catcher", "Ignore and Continue", "Save and Exit");
		popupText = new Popup("Information Display", "Continue");
		popupText.clear();
	}

	public static Theme theme() {
		return theme;
	}

	public static Browser browser() {
		return browser;
	}

	public static Popup popupException() {
		return popupException;
	}

	public static Popup popupText() {
		return popupText;
	}

	public static String getVersion() {
		return version;
	}
}
