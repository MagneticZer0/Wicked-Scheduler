import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;

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
			for (String str : desiredCoursesList) {
				try { // Test code right here
					for (Course c : Scraper.getAllClasses(Scraper.getAllSemesters().get(semesters.getValue())).get(str)) {
						System.out.printf("%s - %s - %s\n", c.toString(), Arrays.toString(c.getCredits()), c.getCRN());
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		// YES THIS CODE IS MESSY, I WILL CLEAN IT UP LATER... 
		// I'M STILL TRYING TO FIGURE OUT HOW TO USE CALENDARFX
		
		//Event<String> class1 = new Event<String>("Class 1");
		// the calendar view code should be its own function eventually
		//Tab tab2 = new Tab("Schedule 2");
		//Tab tab3 = new Tab("Schedule 3");
		
		//com.calendarfx.model.Calendar calendar = new com.calendarfx.model.Calendar("classes calendar");
		//CalendarSource source = new CalendarSource("classes source");
		//Entry<String> entry = new Entry<String>("test");
		//entry.changeStartDate(LocalDate.now());
		//entry.changeStartTime(LocalTime.now());
		//entry.setMinimumDuration(Duration.parse("PT2H"));;
		//Interval interval = new Interval(LocalDateTime.now(), LocalDateTime.now().plusHours(1));
		//entry.setInterval(interval);
		//calendar.addEntry(entry);
		//source.getCalendars().add(calendar);
		
		//CalendarView schedule2 = new CalendarView();
		//CalendarView schedule3 = new CalendarView();
		
		//Calendar classes;
		
		//Entry<String> class1 = new Entry<>("class 1");
		//Entry<String> class2 = new Entry<>("class 2");
		//Entry<String> class3 = new Entry<>("class 3");
		
		//tab2.setContent(schedule2);
		//tab3.setContent(schedule3);

		TabPane schedules = new TabPane();
		Tab tab1 = new Tab("Schedule 1");
		CalendarView calendarView = new CalendarView();
		calendarView.showWeekPage();
		String [] titles = { "CS3712","CS4760","CS3411","CS3000","CS3331","HU3350" };
		for ( int i = 0; i < 6; i++ ) {
			Entry<String> entry = (Entry<String>) calendarView.createEntryAt(ZonedDateTime.now().plusDays(i));
			entry.setTitle( titles[i] );
		}
		tab1.setContent(calendarView);
		
		schedules.getTabs().addAll( tab1 );
		grid.add(schedules, 6, 0, 5, 5);
		
		
		// display the GUI
		Scene scene1 = new Scene(grid, 200, 100);
		firststage.setScene(scene1);
		firststage.show();
	}

	@Override
	public void stop() {
		Scraper.saveCourses();
	}

	private void loadSemesters() {
		new Thread(() -> {
			Scraper.getAllSemesters();
			Platform.runLater(() -> {
				allSemestersList.addAll(Scraper.getAllSemesters().keySet());
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
