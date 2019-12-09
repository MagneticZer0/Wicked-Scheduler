package logic;

import userInterfaces.Browser;
import userInterfaces.Popup;

public class Globals {
	/**
	 * The browser used to show course descriptions
	 */
	private static Browser browser;
	/**
	 * This is the popup that will log exceptions
	 */
	private static Popup popupException;
	private static Popup popupText;

	public static void init() {
		browser = new Browser();
		popupException = new Popup("Error Catcher", "Ignore and Continue", "Save and Exit");
		popupText = new Popup("Information Display", "Continue");
		popupText.clear();
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
}
