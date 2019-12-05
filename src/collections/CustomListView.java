package collections;

import java.io.IOException;

import collections.functions.UpdateFunction;
import javafx.collections.ObservableList;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import logic.Course;
import logic.Scraper;
import userInterfaces.UI;

/**
 * A custom list view is defined as a list that that will allow you to drag
 * elements to other custom list views as well a right click on a cell will open
 * the web browser to see the course description.
 */
public class CustomListView extends ListView<String> {

	/**
	 * The underlying internal list, this it to modify the ListView
	 */
	private ObservableList<String> underlyingList;
	/**
	 * Stores the last list view from which a cell was dragged from
	 */
	private static CustomListView last;
	/**
	 * If the drop of a cell was in a valid location
	 */
	private static boolean correctDrop;
	/**
	 * Storing the string of the cell between transfer
	 */
	private static String lastString;
	/**
	 * The function to execute after a cell is dragged to another CustomListView
	 */
	private UpdateFunction updateFunction = null;

	/**
	 * Creates a CustomListView that the class's description as its properties
	 * 
	 * @param underlyingList The underlying list backing the list view
	 * @param visibleItems   The items currently visible in the list
	 * @param semesters      The ComboBox that has the semesters
	 */
	public CustomListView(ObservableList<String> underlyingList, ObservableList<String> visibleItems, ComboBox<String> semesters) {
		super(visibleItems);
		this.underlyingList = underlyingList;

		this.setCellFactory(param -> {
			ListCell<String> listCell = new ListCell<String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					setText(item);
				}
			};

			listCell.setOnDragDetected(event -> {
				setLast();
				correctDrop = false;
				lastString = listCell.getItem();
				Dragboard db = listCell.startDragAndDrop(TransferMode.MOVE);
				listCell.updateSelected(false);
				db.setDragView(listCell.snapshot(new SnapshotParameters(), null), event.getX(), event.getY());
				ClipboardContent content = new ClipboardContent();
				content.putString(listCell.getItem());
				db.setContent(content);
				underlyingList.remove(listCell.getItem());
				event.consume();
			});

			listCell.setOnDragDone(event -> {
				if (!correctDrop) {
					underlyingList.add(lastString);
				}
			});

			listCell.setOnMousePressed(event -> {
				if (event.getButton() == MouseButton.SECONDARY) {
					try {
						Course currentCourse = Scraper.getAllClasses(Scraper.getAllSemesters().get(semesters.getValue())).get(listCell.getItem()).get(0);
						UI.browser.loadURL("https://www.banweb.mtu.edu/owassb/bwckschd.p_disp_listcrse?term_in=" + Scraper.getAllSemesters().get(semesters.getValue()) + "&subj_in=" + currentCourse.getSubject() + "&crse_in=" + currentCourse.getCourseCode() + "&crn_in=" + currentCourse.getCRN());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			return listCell;
		});

		this.setOnDragEntered(event -> {
			if (this != last) {
				this.setStyle("-fx-box-border: #000000");
			}
		});

		this.setOnDragExited(event -> this.setStyle(""));

		this.setOnDragOver(event -> {
			Dragboard db = event.getDragboard();
			if (db.hasString()) {
				event.acceptTransferModes(TransferMode.MOVE);
			}
			event.consume();
		});

		this.setOnDragDropped(event -> {
			Dragboard db = event.getDragboard();
			boolean success = false;
			if (db.hasString()) {
				underlyingList.add(db.getString());
				if (this != last) {
					last.underlyingList.remove(db.getString());
				}
				if (updateFunction != null) {
					updateFunction.update();
				}
				correctDrop = true;
				success = true;
			}
			event.setDropCompleted(success);
			event.consume();
		});
	}

	/**
	 * Sets the update function.
	 * 
	 * @param updateFunction
	 */
	public void setUpdateFunction(UpdateFunction updateFunction) {
		this.updateFunction = updateFunction;
	}

	/**
	 * Sets the last ListView from which a cell was dragged from
	 */
	private void setLast() {
		last = this;
	}
}
