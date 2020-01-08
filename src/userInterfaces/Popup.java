package userInterfaces;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import logic.ExecutionCode;
import logic.Globals;

/**
 * A PopupDialogue may become more versatile in the future, although as of right
 * now its only purpose to to capture any of the exception, display them to the
 * user, and give the user an option to save the log into a ZIP file.
 */
public class Popup {

	/**
	 * The main stage for the PopupDialogue
	 */
	private Stage popupStage;
	/**
	 * The TextArea is where the exceptions are stored when they are caught.
	 */
	private TextArea textArea = new TextArea();
	/**
	 * Tracks the current state of the program
	 */
	private long executionState = 1l;

	/**
	 * Used to create popup view, for now only used for logging errors.
	 * 
	 * @param title   The title for the PopupDialogue
	 * @param btn1txt The text of the first button
	 * @param btn2txt The text of the second button
	 */
	public Popup(String title, String btn1txt, String btn2txt) {
		Platform.runLater(() -> {
			popupStage = new Stage();
			Pane pane = new Pane();
			popupStage.setTitle(title);
			popupStage.getIcons().add(Globals.theme().getIcon());
			popupStage.setResizable(false);
			popupStage.setAlwaysOnTop(true);
			popupStage.setScene(new Scene(pane, 435, 265));
			popupStage.setOnCloseRequest(e -> {
				Globals.popupException().writeInstruction(ExecutionCode.POPUPCLOSED);
				popupStage.hide();
			});

			textArea.setLayoutX(10);
			textArea.setLayoutY(11);
			textArea.setPrefWidth(424);
			textArea.setPrefHeight(219);
			textArea.setEditable(false);
			textArea.appendText("Well, looks like I've run into a problem!\n");
			textArea.appendText("Ignore and exit buttons will create a log file!\n");
			textArea.appendText("Please create a bug report and upload the file created, thanks!\n");
			textArea.appendText("Version: " + Globals.getVersion() + "\n");
			pane.getChildren().add(textArea);

			Button btnNumber1;
			Button btnNumber2;

			if (btn1txt != null) {
				btnNumber1 = new Button(btn1txt);
				btnNumber1.setOnMouseClicked(e -> popupStage.hide());
				btnNumber1.setLayoutX(10);
				btnNumber1.setLayoutY(237);
				btnNumber1.setPrefWidth(btn1txt != null ^ btn2txt != null ? 424d : 210d);
				btnNumber1.setPrefHeight(23);
				pane.getChildren().add(btnNumber1);
			}

			if (btn2txt != null) {
				btnNumber2 = new Button(btn2txt);
				btnNumber2.setOnMouseClicked(e -> {
					createLog();
					Platform.exit();
				});
				btnNumber2.setLayoutX(btn1txt != null ^ btn2txt != null ? 10d : 224d);
				btnNumber2.setLayoutY(237);
				btnNumber2.setPrefWidth(btn1txt != null ^ btn2txt != null ? 424d : 210d);
				btnNumber2.setPrefHeight(23);
				pane.getChildren().add(btnNumber2);
			}
		});
	}

	/**
	 * Create a popup view that only has one button that just exits
	 * 
	 * @param title   The title for the PopupDialogue
	 * @param btn1txt The text of the first button
	 */
	public Popup(String title, String btn1txt) {
		this(title, btn1txt, null);
	}

	/**
	 * Writes the instruction to the log, but does not display the log
	 * 
	 * @param executionCode The execution code to write to the log
	 */
	public void writeInstruction(ExecutionCode executionCode) {
		executionState *= executionCode.getValue();
		Platform.runLater(() -> {
			textArea.appendText(executionCode.toString() + "\n");
		});
	}

	/**
	 * All Exceptions are capture with this, this method makes the stage visible and
	 * makes the Exception visible in the textArea.
	 * 
	 * @param e The captured Exception
	 */
	public void writeError(Throwable e) {
		Platform.runLater(() -> {
			textArea.appendText("State: 0x" + String.format("%16s", Long.toHexString(executionState).toUpperCase()).replace(" ", "0") + "\n");
			textArea.appendText("Memory: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000l + " MB / " + Runtime.getRuntime().maxMemory() / 1000000l + " MB\n");
			if (e instanceof Error) {
				textArea.appendText("### BEGIN ERROR ###\n");
			} else {
				textArea.appendText("### BEGIN EXCEPTION ###\n");
			}
			StringBuilder sb = new StringBuilder(e.toString());
			for (StackTraceElement ste : e.getStackTrace()) {
				sb.append("\n");
				sb.append(ste);
			}
			textArea.appendText(sb.toString());
			if (e instanceof Error) {
				textArea.appendText("\n### END ERROR ###\n");
			} else {
				textArea.appendText("\n### END EXCEPTION ###\n");
			}
			popupStage.show();
			textArea.selectHome(); //
			textArea.deselect(); // This is to scroll to the top
		});
	}

	/**
	 * Clears the text area
	 */
	public void clear() {
		Platform.runLater(() -> textArea.setText(""));
	}

	/**
	 * Writes text to the popup. Automatically adds a \n character to the end
	 * 
	 * @param s The text to write
	 */
	public void write(String s) {
		Platform.runLater(() -> {
			popupStage.show();
			textArea.appendText(s + "\n");
		});
	}

	/**
	 * Makes the Popup frame appear
	 */
	public void show() {
		Platform.runLater(() -> popupStage.show());
	}

	/**
	 * Exits the popup
	 */
	public void exit() {
		Globals.popupException().writeInstruction(ExecutionCode.POPUPCLOSED);
		popupStage.hide();
	}

	/**
	 * Happens whenever you press the Ignore or Exit buttons, creates a zip file
	 * that contains a log with the Exception.
	 */
	protected void createLog() {
		String date = new SimpleDateFormat("MMddyyHHmmss").format(new Date());
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(date + "-Crash.zip"))) {
			ZipEntry ze = new ZipEntry("Crash.log");
			zos.putNextEntry(ze);
			byte[] data = textArea.getText().getBytes();
			zos.write(data);
			zos.closeEntry();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
