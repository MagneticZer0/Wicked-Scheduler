package userInterfaces;

import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLTableElement;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.*;
import javafx.stage.Stage;
import logic.Globals;
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
		stage.getIcons().add(Globals.theme().getIcon());
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

	public void loadHelp() {
		Platform.runLater(() -> {
			stage.show();
			stage.requestFocus();
			webEngine.loadContent("<h1>Wicked Scheduler Help Page</h1>\r\n" + 
					"<h3>How to select the proper semester:</h3>\r\n" + 
					"<p>Using the drop down list found above the green \"Add Course\" button, click on the desired semester and wait for the courses to load in the left \"Offered Courses\" list.</p>\r\n" + 
					"<h3>How to select classes to schedule:</h3>\r\n" + 
					"<p>First, find the class you wish to add from the \"Offered Courses\" list via the search bar above the list or by scrolling down the list. Classes are displayed in alphabetical order of course codes.</p>\r\n" + 
					"<p>Once you found the course you wish to add, select the course. You will know you selected a course when the name of the course is highlighted blue. Next, drag and drop the course into the \"Desired Courses\" list or click the green \"Add Course\" button.</p>\r\n" + 
					"<h3>How to remove classes to schedule:</h3>\r\n" + 
					"<p>First, find the class you wish to add from the \"Desired Courses\" list via the search bar above the list or by scrolling down the list. Classes are displayed in alphabetical order of course codes.</p>\r\n" + 
					"<p>Once you found the course you wish to remove, select the course. You will know you selected a course when the name of the course is highlighted blue. Next, drag and drop the course into the \"Offered Courses\" list or click the red \"Remove Course\" button.</p>\r\n" + 
					"<h3>How to see potential schedules:</h3>\r\n" + 
					"<p>After you have all your desired courses to schedule in the \"Desired Courses\" list, click the blue \"Create Schedule\" button and wait for the page to load.</p>\r\n" + 
					"<h3>How to return to the course selection screen from the calendar page.</h3>\r\n" + 
					"<p>Click the \"Back\" button in the top left corner of the application.</p>\r\n" + 
					"<h3>A class you selected did not appear on the calendar:</h3>\r\n" + 
					"<p>A class may not appear on the calendar if:</p>\r\n" + 
					"<ul>\r\n" + 
					"<li>The class is not in session during the default week on the calendar view. Note that many PE classes will start half way through a semester.</li>\r\n" + 
					"<li>The class's instruction time is TBA.</li>\r\n" + 
					"<li>There is no possible schedule that contains all desired classes. If this is the case, the schedule displayed will contain as many classes as possible.</li>\r\n" + 
					"</ul>\r\n" + 
					"<h3>How to request for more potential schedules:</h3>\r\n" + 
					"<p>More schedules can be requested using the \"GIVE ME MORE\" button in the top right of the application. Note that this button will create a new tab that must be click in order to view the additional schedules.</p>\r\n" + 
					"<h3>How to exit the program:</h3>\r\n" + 
					"<p>Click the \"X\" button in the top right corner of the window.</p>");
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
				// I don't want it to do anything
			}
		}
	}
}