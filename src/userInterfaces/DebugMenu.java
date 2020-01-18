package userInterfaces;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import logic.Globals;
import logic.Scraper;

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
	public DebugMenu(String title, UI ui) {
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
			stage.setOnCloseRequest(e -> stage.hide());

			Label heapUsageLabel = new Label("Heap Usage: ");
			grid.add(heapUsageLabel, 0, 0);
			ProgressBar heapUsage = new ProgressBar();
			heapUsage.setOnMouseEntered(action -> {
				stage.getScene().setCursor(Cursor.HAND);
			});
			heapUsage.setOnMouseExited(action -> {
				stage.getScene().setCursor(Cursor.DEFAULT);
			});
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
			memoryUsage.setOnMouseEntered(action -> {
				stage.getScene().setCursor(Cursor.HAND);
			});
			memoryUsage.setOnMouseExited(action -> {
				stage.getScene().setCursor(Cursor.DEFAULT);
			});
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

			Button fullSchedule = new Button("Add 26 Courses");
			fullSchedule.setOnAction(action -> {
				try {
					Field semestersField = UI.class.getDeclaredField("semesters");
					semestersField.setAccessible(true);
					ComboBox<String> semesters = (ComboBox<String>) semestersField.get(ui);
					semesters.setValue("Spring 2020");

					Scraper.getAllClasses(Scraper.getAllSemesters().get("Spring 2020"));

					Field coursesField = UI.class.getDeclaredField("desiredCoursesList");
					coursesField.setAccessible(true);
					ObservableList<String> courses = (ObservableList<String>) coursesField.get(ui);
					courses.add("EC4050 - Game Theory/Strategic Behavior");
					courses.add("AF3020 - Effective Com II - Non AFROTC");
					courses.add("BL4450 - Limnology");
					courses.add("CH1151 - University Chemistry Lab I Lab");
					courses.add("MA1161 - Calculus Plus w/ Technology I Lab");
					courses.add("FA3400 - Keweenaw Symphony Orchestra Lab");
					courses.add("CEE4507 - Distribution and Collection Lab");
					courses.add("CS3311 - Formal Models of Computation");
					courses.add("BL4530 - Senior Research Capstone Exp");
					courses.add("MA3160 - Multivariable Calc with Tech Lab");
					courses.add("BL4752 - Cancer Biology");
					courses.add("CH1150 - University Chemistry I");
					courses.add("MA1161 - Calculus Plus w/ Technology I");
					courses.add("CH1163 - University Chem Recitation II");
					courses.add("BL3611 - Phlebotomy Lab");
					courses.add("MA2720 - Statistical Methods");
					courses.add("MA3160 - Multivariable Calc with Tech");
					courses.add("ACC2000 - Accounting Principles I");
					courses.add("EE4800 - Antennas");
					courses.add("ACC4600 - Advanced Tax Topics");
					courses.add("EE2174 - Digital Logic and Lab Lab");
					courses.add("ENT1960 - Wireless Communication Lab");
					courses.add("PE0121 - Beginning Snowboarding Lab");
					courses.add("BL3190 - Evolution");
					courses.add("BL5038 - Epigenetics");
					courses.add("ENT1960 - Supermileage Systems Lab");
				} catch (ReflectiveOperationException | IOException e) {
					e.printStackTrace();
				}
			});
			grid.add(fullSchedule, 1, 2);

			Button viewThreads = new Button("View Threads");
			viewThreads.setOnAction(action -> {
				Stage threadStage = new Stage();
				threadStage.getIcons().add(Globals.theme().getIcon());
				threadStage.setTitle(title + " - Threads");

				GridPane threadsGrid = new GridPane();
				threadsGrid.setHgap(10);
				threadsGrid.setVgap(10);
				threadsGrid.setAlignment(Pos.CENTER_LEFT);
				threadStage.setScene(new Scene(threadsGrid, 435, 265));

				Set<Thread> allThreads = Thread.getAllStackTraces().keySet();
				ComboBox<Thread> threadSelection = new ComboBox<>(FXCollections.observableArrayList(allThreads));
				threadSelection.getSelectionModel().select(0);
				threadSelection.setConverter(new StringConverter<Thread>() {
					@Override
					public Thread fromString(String threadName) {
						return Thread.getAllStackTraces().keySet().parallelStream().filter(e -> e.getName().equals(threadName)).findFirst().get();
					}

					@Override
					public String toString(Thread thread) {
						return thread.getName();
					}
				});
				threadSelection.setOnAction(e -> {
					Thread selectedThread = threadSelection.getSelectionModel().getSelectedItem();
					// Allow name change and stopping thread
					System.out.println("ID: " + selectedThread.getId());
					System.out.println("Priority: " + selectedThread.getPriority()); // Allow change
					System.out.println("State: " + selectedThread.getState());
					System.out.println("Thread Group: " + selectedThread.getThreadGroup());
					System.out.println("Exception Handler: " + selectedThread.getUncaughtExceptionHandler()); // Allow change
					System.out.println("Daemon: " + selectedThread.isDaemon()); //  Allow change
					System.out.println(Arrays.deepToString(selectedThread.getStackTrace()));
				});
				threadsGrid.add(threadSelection, 0, 0);

				threadStage.show();
			});
			grid.add(viewThreads, 1, 3);

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
