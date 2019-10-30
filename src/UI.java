import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

// use com.calendarfx.model.Calendar when instantiating a calendarfx calendar
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.model.Interval;
import com.calendarfx.view.CalendarView;

import impl.com.calendarfx.view.DateControlSkin;
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

import javax.management.ReflectionException;

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
	 * THIS FIELD IS ONLY USED FOR UNIT TESTING AND USED THROUGH REFLECTION
	 */
	private final CountDownLatch DONOTUSE = new CountDownLatch(2);

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
		allCoursesSearch.setPromptText("Search Courses");
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
		desiredCoursesSearch.setPromptText("Search Desired Courses");
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
		
		TabPane schedulesView = new TabPane();

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
			
			//pretend scheduler
			ArrayList<Course> finalSchedule = new ArrayList<Course>();
			for ( i = 0; i < desiredCourses.size(); i++ ) {
				finalSchedule.addAll(Scraper.courses.get(desiredCourses.get(i)));
			}
			
			for( int j = 0; j < 3; j++ ) {
				
				Tab tab = new Tab("Schedule " + (j+1) );
				CalendarView calendarView = new CalendarView();
				calendarView.showDate(finalSchedule.get(0).getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
				setInfo();
				calendarView.showWeekPage();
				
				for ( int k = 0; k < finalSchedule.size(); k++ ) {
					LocalTime timeTBA = LocalTime.parse("01:37"); // 01:37 time is TBA time
					// Jan 1st 1970 is TBA date
					
					// get the next course to display
					Course cur = finalSchedule.get(k);
					
					// if the class time is tba, don't display anything
					LocalTime classTime = cur.getStartTime(0);
					if ( classTime == timeTBA ) {
						System.out.println("Time for class " + cur + " is TBA");
						continue;
					}
					
					// if the class time is tba, don't display anything
					// NOT YET IMPLEMENTED
					Date classDateTime = cur.getStartDate();

					// set the correct time for the class
					ZonedDateTime classZonedDateTime = ZonedDateTime.now().withHour(classTime.getHour()); 
					classZonedDateTime = classZonedDateTime.withMinute(classTime.getMinute());
					classZonedDateTime = classZonedDateTime.withSecond(0);
					classZonedDateTime = classZonedDateTime.withNano(0);
					
					// set the correct date for the class
					Calendar cal = Calendar.getInstance();
					cal.setTime(classDateTime);
					cal.get(Calendar.DAY_OF_WEEK);
					classZonedDateTime = classZonedDateTime.withDayOfMonth(cal.get(Calendar.DAY_OF_MONTH));
					classZonedDateTime = classZonedDateTime.withMonth(cal.get(Calendar.MONTH)+1);
					classZonedDateTime = classZonedDateTime.withYear(cal.get(Calendar.YEAR));
									
					// create the entry/entries for the class
					List<String> days = cur.getDays();
					Entry<String> entry = null;
					for(int d = 0; d < days.size(); d++) {
						
						String dotw = days.get(d); // dotw stands for day of the week
						switch (dotw) {
						case "Monday": 
							entry = (Entry<String>) calendarView.createEntryAt(classZonedDateTime);
							break;
						case "Tuesday":
							entry = (Entry<String>) calendarView.createEntryAt(classZonedDateTime.plusDays(1));
							break;
						case "Wednesday":
							entry = (Entry<String>) calendarView.createEntryAt(classZonedDateTime.plusDays(2));
							break;
						case "Thursday":
							entry = (Entry<String>) calendarView.createEntryAt(classZonedDateTime.plusDays(3));
							break;
						case "Friday":
							entry = (Entry<String>) calendarView.createEntryAt(classZonedDateTime.plusDays(4));
							break;
						}
						entry.setTitle( finalSchedule.get(k).toString() + " CRN: " + cur.getCRN());
					}
				}

				tab.setContent(calendarView);
				schedulesView.getTabs().addAll( tab );
			}
			
		});
		
		grid.add(schedulesView, 6, 0, 5, 5);
		
		// display the GUI
		Scene scene = new Scene(grid, 200, 100);
		firststage.setScene(scene);
		firststage.show();
		DONOTUSE.countDown();
	}

	/**
	 * This is a hacky way to disable the thing that CalendarFX ouputs to console
	 * because I'm slightly annoyed by it.
	 */
	private void setInfo() {
		try {
			Field info = DateControlSkin.class.getDeclaredField("infoShown");
			info.setAccessible(true);
			info.set(null, true);
		} catch (ReflectiveOperationException e) {
			// Ignore
		}
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
				DONOTUSE.countDown();
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
