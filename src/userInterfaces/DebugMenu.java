package userInterfaces;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.Globals;

public class DebugMenu {

	/**
	 * The main stage for the PopupDialogue
	 */
	private Stage stage;

	/**
	 * Used to create popup view, for now only used for logging errors.
	 * 
	 * @param title The title for the PopupDialogue
	 */
	public DebugMenu(String title) {
		Platform.runLater(() -> {
			stage = new Stage();
			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setAlignment(Pos.CENTER);
			stage.setTitle(title);
			stage.getIcons().add(Globals.theme().getIcon());
			stage.setResizable(false);
			stage.setScene(new Scene(grid, 435, 265));
			stage.setOnCloseRequest(e -> {
				stage.hide();
			});

			Label heapUsageLabel = new Label("Heap Usage: ");
			grid.add(heapUsageLabel, 0, 0);
			ProgressBar heapUsage = new ProgressBar();
			grid.add(heapUsage, 0, 1);

			final Runtime runtime = Runtime.getRuntime();
			Timeline heapUsageUpdater = new Timeline(new KeyFrame(Duration.millis(100), e -> {
				heapUsageLabel.setText("Heap Usage (" + toReadable(runtime.totalMemory() - runtime.freeMemory()) + " / " + toReadable(runtime.totalMemory()) + "):");
				heapUsage.setProgress((runtime.totalMemory() - runtime.freeMemory()) / (double) runtime.totalMemory());
			}));
			heapUsageUpdater.setCycleCount(Animation.INDEFINITE);
			heapUsageUpdater.play();

			Label memoryUsageLabel = new Label("Memory Usage: ");
			grid.add(memoryUsageLabel, 0, 2);
			ProgressBar memoryUsage = new ProgressBar();
			grid.add(memoryUsage, 0, 3);

			Timeline memoryUsageUpdater = new Timeline(new KeyFrame(Duration.millis(100), e -> {
				memoryUsageLabel.setText("Memory Usage (" + toReadable(runtime.totalMemory()) + " / " + toReadable(runtime.maxMemory()) + "):");
				memoryUsage.setProgress(runtime.totalMemory() / (double) runtime.maxMemory());
			}));
			memoryUsageUpdater.setCycleCount(Animation.INDEFINITE);
			memoryUsageUpdater.play();

			Button garbageCollect = new Button("Perform Garbage Collection");
			garbageCollect.setOnAction(action -> {
				System.gc();
			});
			grid.add(garbageCollect, 1, 0);

			Button throwException = new Button("Throw Null Pointer Exception");
			throwException.setOnAction(action -> {
				Globals.popupException().writeError(new NullPointerException());
			});
			grid.add(throwException, 1, 1);

			stage.show();
		});
	}

	private String toReadable(long bytes) {
		if (bytes < 750l) {
			return bytes + " B";
		} else if (bytes >= 750l && bytes < 750000l) {
			return String.format("%.2f KB", bytes / 1000.0);
		} else if (bytes >= 750000l && bytes < 750000000l) {
			return String.format("%.2f MB", bytes / 1000000.0);
		} else if (bytes >= 750000000l && bytes < 750000000000l) {
			return String.format("%.2f GB", bytes / 1000000000.0);
		} else if (bytes >= 750000000000l && bytes < 750000000000000l) {
			return String.format("%.2f TB", bytes / 1000000000000.0);
		} else {
			return String.format("%.2f PB", bytes / 1000000000000000.0);
		}
	}
}
