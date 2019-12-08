package userInterfaces;


import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLTableElement;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.*;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

/**
 * A very toned down browser whose only purpose, currently, is to display course
 * descriptions if a user requests it
 */
public class Browser {

	/**
	 * The stage for storing everything
	 */
	private Stage stage = null;
	/**
	 * The WebEngine the controls the WebView in order to display things within the
	 * Browser
	 */
	private WebEngine webEngine = null;

	/**
	 * Creates a Browser object that is essentially just a WebView
	 */
	public Browser() {
		Stage stage = new Stage();
		stage.setOnCloseRequest(e -> stage.hide());
		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(makeHtmlView());

		Scene scene = new Scene(borderPane, 600, 600);
		stage.setScene(scene);
		this.stage = stage;
	}

	/**
	 * Loads the specified URL in to the browser
	 *
	 * @param url The URL to load
	 */
	public void loadURL(String url) {
		Platform.runLater(() -> {
			stage.show();
			stage.requestFocus();
			webEngine.load(url);
		});
	}

	/**
	 * Exits the stage by hiding it
	 */
	public void exit() {
		stage.hide();
	}

	/**
	 * Creates a WebView which handles mouse and some keyboard events, and manages
	 * scrolling automatically, so there's no need to put it into a ScrollPane. The
	 * associated WebEngine is created automatically at construction time.
	 *
	 * @return browser - a WebView container for the WebEngine.
	 */
	private WebView makeHtmlView() {
		WebView view = new WebView();
		webEngine = view.getEngine();
		addWebEngineEvents();
		return view;
	}

	/**
	 * Adds the EventHandlers for the WebEngine that will update the title of the
	 * browser as well as once a page has loaded manipulate it some so that elements
	 * from the page may be removed
	 */
	private void addWebEngineEvents() {
		webEngine.getLoadWorker().workDoneProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.doubleValue() != 100.0) {
				stage.setTitle("Wicked Scheduler Browser - Loading: " + newValue.intValue() + "%");
			} else {
				stage.setTitle("Wicked Scheduler Browser");

				if (webEngine.getLocation().contains("disp_listcrse")) {
					// Removes Sign In and Help button
					run(() -> webEngine.getDocument().getElementById("globalNav").getParentNode().removeChild(webEngine.getDocument().getElementById("globalNav")));

					// Removes Home button
					run(() -> webEngine.getDocument().getElementById("crumb").getParentNode().removeChild(webEngine.getDocument().getElementById("crumb")));

					// Removes Scheduled Meeting Times data and the "Return to Previous Button
					run(() -> {
						JSObject tables = (JSObject) webEngine.executeScript("document.getElementsByClassName(\"datadisplaytable\")");
						((HTMLTableElement) tables.call("item", 1)).getParentNode().removeChild((Node) tables.call("item", 1));
						((HTMLTableElement) tables.call("item", 1)).getParentNode().removeChild((Node) tables.call("item", 1));
					});
				}
			}
		});
	}

	/**
	 * Takes an ExceptionalFunction and runs it so that I could use lambda
	 * expressions
	 * 
	 * @param func The function to execute
	 */
	private void run(ExceptionalFunction func) {
		func.run();
	}

	/**
	 * An interface defined as something that executes code in a try catch block. I
	 * do this when I want multiple lines of code to run without clogging up my code
	 * with try catches because I want the next line to run regardless of the
	 * Exception state of the last one.
	 */
	private interface ExceptionalFunction {
		public void execute() throws Exception;

		public default void run() {
			try {
				execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}