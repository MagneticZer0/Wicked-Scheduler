import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

// use com.calendarfx.model.Calendar when instantiating a calendarfx calendar
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.model.Interval;
import com.calendarfx.view.CalendarView;
import impl.com.calendarfx.view.util.Util;
import com.calendarfx.*;
import java.time.Duration;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author Alex Grant, Coleman Clarstein, Harley Merkaj
 *
 */
public class UI extends Application {

	private ObservableList<String> allCoursesList = FXCollections.observableArrayList();
	private ObservableList<String> allSemestersList = FXCollections.observableArrayList();
	private ListView<String> allCoursesSelection = null;
	private VBox loadingBox = null;
	private ComboBox<String> semesters = null;
	private ArrayList<String> preScheduledClasses = new ArrayList<String>();

	/**
	 * this function builds the GUI and displays it to the user once everything has
	 * been initialized
	 *
	 * @param firststage - a pre-made stage created by Application.launch
	 */
	public void start(Stage firststage) {

		// set window properties
		firststage.setTitle("Wicked Scheduler");
		firststage.setX(250);
		firststage.setY(50);
		firststage.setWidth(1000);
		firststage.setMinWidth(650);
		firststage.setHeight(700);
		firststage.setMinHeight(486);
		firststage.initStyle(StageStyle.DECORATED);

		// create a grid for GUI elements
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setAlignment(Pos.CENTER);
		// grid.setGridLinesVisible(true);

		// elements regarding all courses
		Label allCoursesLabel = new Label("Offered Courses:");
		grid.add(allCoursesLabel, 0, 1, 1, 1);

		TextField allCoursesSearch = new TextField();
		allCoursesSearch.setPromptText("Input Course Code");
		allCoursesSearch.setMaxWidth(firststage.getWidth() / 4);
		grid.add(allCoursesSearch, 1, 1, 1, 1);

		FilteredList<String> allCoursesFilter = new FilteredList<>(allCoursesList, d -> true); // Make them all visible at first
		allCoursesSelection = new ListView<>(allCoursesFilter.sorted());
		allCoursesSearch.textProperty().addListener((obs, oldVal, newVal) -> {
			allCoursesFilter.setPredicate(d -> newVal == null || newVal.isEmpty() || d.toLowerCase().contains(newVal.toLowerCase()) // Display all values if it's empty and it's case insensitive
			);
		});
		allCoursesSelection.setPlaceholder(new Label("Nothing is here!"));
		allCoursesSelection.setMinWidth(firststage.getWidth() / 4);
		grid.add(allCoursesSelection, 0, 2, 2, 4);

		// elements regarding desired courses
		Label desiredCoursesLabel = new Label("Desired Courses:");
		grid.add(desiredCoursesLabel, 3, 1, 1, 1);

		TextField desiredCoursesSearch = new TextField();
		desiredCoursesSearch.setPromptText("Input Course Code");
		desiredCoursesSearch.setMaxWidth(firststage.getWidth() / 4);
		grid.add(desiredCoursesSearch, 4, 1, 1, 1);

		ObservableList<String> desiredCoursesList = FXCollections.observableArrayList();
		FilteredList<String> desiredCoursesFilter = new FilteredList<>(desiredCoursesList, d -> true); // Make them all visible at first
		ListView<String> desiredCoursesSelection = new ListView<>(desiredCoursesFilter.sorted());
		desiredCoursesSearch.textProperty().addListener((obs, oldVal, newVal) -> {
			desiredCoursesFilter.setPredicate(d -> newVal == null || newVal.isEmpty() || d.toLowerCase().contains(newVal.toLowerCase()) // Display all values if it's empty and it's case insensitive
			);
		});
		desiredCoursesSelection.setPlaceholder(new Label("Nothing is here!"));
		desiredCoursesSelection.setMinWidth(firststage.getWidth() / 4);
		grid.add(desiredCoursesSelection, 3, 2, 2, 4);

		// semester list
		SortedList<String> sortedSemesters = allSemestersList.sorted(new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				return value(arg1) - value(arg0); // Normally arg0 - arg1, but I want reverse order
			}

			private int value(String str) {
				int value = 0;
				String[] spl = str.split(" ");
				switch (spl[0]) {
					case "Spring":
						value += 1;
						break;
					case "Summer":
						value += 2;
						break;
					case "Fall":
						value += 3;
						break;
					case "Winter":
						value += 4;
				}
				return value + Integer.parseInt(spl[1]) * 10;
			}
		});
		semesters = new ComboBox<>(sortedSemesters.filtered(d -> sortedSemesters.indexOf(d) < 5)); // Only do 5 most relevant
		semesters.setPromptText("Select Semester");
		semesters.setMaxWidth(firststage.getWidth() / 4);
		semesters.setOnAction(e -> {
			desiredCoursesList.clear();
			loadCourses(Scraper.getAllSemesters().get(semesters.getValue()));
		});
		grid.add(semesters, 2, 2, 1, 1);

		loadSemesters();
		semesters.setValue(defaultSemester());

		Button addCourse = new Button("Add Course");
		addCourse.setStyle("-fx-background-color: #32CD32;");
		addCourse.setMaxWidth(firststage.getWidth() / 4);
		grid.add(addCourse, 2, 3, 1, 1);

		Button removeCourse = new Button("Remove Course");
		removeCourse.setStyle("-fx-background-color: #FF0000;");
		removeCourse.setMaxWidth(firststage.getWidth() / 4);
		grid.add(removeCourse, 2, 4, 1, 1);

		Button schedule = new Button("Create Schedule");
		schedule.setStyle("-fx-background-color: #ADD8E6;");
		schedule.setMaxWidth(firststage.getWidth() / 4);
		grid.add(schedule, 2, 5, 1, 1);
		GridPane.setValignment(schedule, VPos.BOTTOM);
		
		TabPane schedules = new TabPane();

		// buttons
		addCourse.setOnAction(action -> {
			if (allCoursesSelection.getSelectionModel().getSelectedItem() != null) {
				desiredCoursesList.add(allCoursesSelection.getSelectionModel().getSelectedItem());
				allCoursesList.remove(allCoursesSelection.getSelectionModel().getSelectedItem());
			}
		});
		removeCourse.setOnAction(action -> {
			if (desiredCoursesSelection.getSelectionModel().getSelectedItem() != null) {
				allCoursesList.add(desiredCoursesSelection.getSelectionModel().getSelectedItem());
				desiredCoursesList.remove(desiredCoursesSelection.getSelectionModel().getSelectedItem());
			}
		});
		schedule.setOnAction(action -> {
			
			List<String> desiredCourses = desiredCoursesSelection.getItems();

			int i = 0;
			while ( !desiredCourses.isEmpty() && i < desiredCourses.size() ) {
				preScheduledClasses.add(desiredCourses.get(i));
				i++;
			}
			
			//send classes to alex
			//recieve schedules
			//do stuff with schedules
			
			for( int j = 0; j < 3; j++ ) {
				
				Tab tab = new Tab("Schedule " + (j+1) );
				CalendarView calendarView = new CalendarView();
				calendarView.showWeekPage();

				for ( int k = 0; k < desiredCourses.size(); k++ ) {
					Entry<String> entry = (Entry<String>) calendarView.createEntryAt(ZonedDateTime.now().plusDays(k+j));
					entry.setTitle( desiredCourses.get(k) );
				}

				tab.setContent(calendarView);
				schedules.getTabs().addAll( tab );
			}
			
		});
		
		grid.add(schedules, 6, 0, 5, 5);
		
		// display the GUI
		Scene scene = new Scene(grid, 200, 100);
		firststage.setScene(scene);
		firststage.show();
	}

	@Override
	public void stop() {
		Scraper.saveCourses();
	}

	private void loadSemesters() {
		new Thread(() -> {
			try {
			Scraper.getAllSemesters();
			} catch (Exception e){
				e.printStackTrace();
			}
			Platform.runLater(() -> {
				try {
				allSemestersList.addAll(Scraper.getAllSemesters().keySet());
				} catch (Exception e){
					e.printStackTrace();
				}
				loadCourses(Scraper.getAllSemesters().get(semesters.getValue()));
			});
		}).start();
	}

	private void loadCourses(String semesterID) {
		checkLoading();
		allCoursesList.clear();
		allCoursesSelection.setPlaceholder(loadingBox);

		new Thread(() -> {
			Scraper.loadCourses();
			try {
				Scraper.getAllClasses(semesterID);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Platform.runLater(() -> {
				try {
					allCoursesList.addAll(Scraper.getAllClasses(semesterID).keySet());
				} catch (Exception e) {
					e.printStackTrace();
				}
				allCoursesSelection.setPlaceholder(new Label("Nothing is here!"));
			});
		}).start();
	}

	private void checkLoading() {
		if (loadingBox == null) {
			loadingBox = new VBox();
			loadingBox.getChildren().add(new Label("Loading classes..."));
			loadingBox.getChildren().add(new ProgressIndicator());
			loadingBox.setAlignment(Pos.CENTER);
			loadingBox.setSpacing(10);
			allCoursesSelection.setPlaceholder(loadingBox);
		}
	}

	private String defaultSemester() {
		Calendar date = Calendar.getInstance();
		if (date.get(Calendar.MONTH) >= 8 && date.get(Calendar.MONTH) <= 12) {
			return "Spring " + (date.get(Calendar.YEAR) + 1);
		} else {
			return "Fall " + date.get(Calendar.YEAR);
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Application.launch(args);
	}
}
