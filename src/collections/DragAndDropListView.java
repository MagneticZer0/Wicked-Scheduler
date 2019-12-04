package collections;

import collections.functions.UpdateFunction;
import javafx.collections.ObservableList;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class DragAndDropListView extends ListView<String> {
	private ObservableList<String> underlyingList;
	private static DragAndDropListView last;
	private static boolean correctDrop;
	private static String lastString;
	private UpdateFunction updateFunction = null;

	public DragAndDropListView(ObservableList<String> underlyingList, ObservableList<String> visibleItems) {
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

	public void setUpdateFunction(UpdateFunction updateFunction) {
		this.updateFunction = updateFunction;
	}

	private void setLast() {
		last = this;
	}
}
