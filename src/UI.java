import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;

import impl.com.calendarfx.view.DateControlSkin;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import themes.DefaultTheme;
import themes.Theme;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.joda.time.LocalDateTime;

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
	private Theme theme = new DefaultTheme();
	private int creditLoad;
	/**
	 * THIS FIELD IS ONLY USED FOR UNIT TESTING AND USED THROUGH REFLECTION
	 */
	private final CountDownLatch DONOTUSE = new CountDownLatch(2);

	/**
	 * this function builds the GUI and displays it to the user once everything has
	 * been initialized
	 *
	 * @param primaryStage - a pre-made stage created by Application.launch
	 */
	public void start(Stage primaryStage) {

		// set window properties
		primaryStage.setTitle("Wicked Scheduler");
		primaryStage.setX(250);
		primaryStage.setY(50);
		primaryStage.setWidth(1000);
		primaryStage.setMinWidth(650);
		primaryStage.setHeight(700);
		primaryStage.setMinHeight(486);
		primaryStage.initStyle(StageStyle.DECORATED);
		primaryStage.getIcons().add(theme.getIcon());

		// create a grid for GUI elements
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setAlignment(Pos.CENTER);
		grid.setStyle(Theme.toBackgroundStyle(theme.backgroundColor()));
		Scene scene = new Scene(grid, 200, 100);
		// grid.setGridLinesVisible(true);

		// elements regarding all courses
		Label allCoursesLabel = new Label("Offered Courses:");
		allCoursesLabel.setStyle(Theme.toTextStyle(theme.textColor()));
		grid.add(allCoursesLabel, 0, 1, 1, 1);
		TextField allCoursesSearch = new TextField();

		allCoursesSearch.setPromptText("Search Courses");
		allCoursesSearch.setMaxWidth(primaryStage.getWidth() / 4);
		grid.add(allCoursesSearch, 1, 1, 1, 1);

		FilteredList<String> allCoursesFilter = new FilteredList<>(allCoursesList, d -> true); // Make them all visible at first
		allCoursesSelection = new ListView<>(allCoursesFilter.sorted());
		allCoursesSearch.textProperty().addListener((obs, oldVal, newVal) -> {
			allCoursesFilter.setPredicate(d -> newVal == null || newVal.isEmpty() || d.toLowerCase().contains(newVal.toLowerCase()) // Display all values if it's empty and it's case insensitive
			);
		});
		allCoursesSelection.setPlaceholder(new Label("Nothing is here!"));
		allCoursesSelection.setMinWidth(primaryStage.getWidth() / 4);
		grid.add(allCoursesSelection, 0, 2, 2, 4);

		// elements regarding desired courses
		Label currentCredits = new Label("Current credits: 0");
		currentCredits.setStyle(Theme.toTextStyle(theme.textColor()));
		//currentCredits.textProperty().bind(Bindings.concat(creditLoad)); NOT WORKING YET
		grid.add(currentCredits, 4, 6, 1, 1);
		
		Label desiredCoursesLabel = new Label("Desired Courses:");
		desiredCoursesLabel.setStyle(Theme.toTextStyle(theme.textColor()));
		grid.add(desiredCoursesLabel, 3, 1, 1, 1);

		TextField desiredCoursesSearch = new TextField();
		desiredCoursesSearch.setPromptText("Search Desired Courses");
		desiredCoursesSearch.setMaxWidth(primaryStage.getWidth() / 4);
		grid.add(desiredCoursesSearch, 4, 1, 1, 1);

		ObservableList<String> desiredCoursesList = FXCollections.observableArrayList();
		FilteredList<String> desiredCoursesFilter = new FilteredList<>(desiredCoursesList, d -> true); // Make them all visible at first
		ListView<String> desiredCoursesSelection = new ListView<>(desiredCoursesFilter.sorted());
		desiredCoursesSearch.textProperty().addListener((obs, oldVal, newVal) -> {
			desiredCoursesFilter.setPredicate(d -> newVal == null || newVal.isEmpty() || d.toLowerCase().contains(newVal.toLowerCase()) // Display all values if it's empty and it's case insensitive
			);
		});
		desiredCoursesSelection.setPlaceholder(new Label("Nothing is here!"));
		desiredCoursesSelection.setMinWidth(primaryStage.getWidth() / 4);
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
						value = 1;
						break;
					case "Summer":
						value = 2;
						break;
					case "Fall":
						value = 3;
						break;
					default:
						value = 4;
				}
				return value + Integer.parseInt(spl[1]) * 10;
			}
		});
		semesters = new ComboBox<>(sortedSemesters.filtered(d -> sortedSemesters.indexOf(d) < 5)); // Only do 5 most relevant
		semesters.setPromptText("Select Semester");
		semesters.setMaxWidth(primaryStage.getWidth() / 4);
		semesters.setOnAction(e -> {
			desiredCoursesList.clear();
			loadCourses(Scraper.getAllSemesters().get(semesters.getValue()));
		});
		grid.add(semesters, 2, 2, 1, 1);

		// identify the upcoming semester and load it by deafult
		loadSemesters();
		semesters.setValue(defaultSemester());

		// button for adding courses to the desired courses list
		Button addCourse = new Button("Add Course");
		addCourse.setStyle(Theme.toStyle(theme.addCourseColors()));
		addCourse.setMaxWidth(primaryStage.getWidth() / 4);
		grid.add(addCourse, 2, 3, 1, 1);
		addCourse.setOnAction(action -> {
			if (allCoursesSelection.getSelectionModel().getSelectedItem() != null) {
				desiredCoursesList.add(allCoursesSelection.getSelectionModel().getSelectedItem());
				allCoursesList.remove(allCoursesSelection.getSelectionModel().getSelectedItem());
				//Scraper.getAllClasses(semesters.getValue()).;
				
				/*try {
					creditLoad += Scraper.getAllClasses(semesters.getValue()).get(allCoursesSelection.getSelectionModel().getSelectedItem()).get(0).getCredits()[0];
				} catch (IOException e1) {
					e1.printStackTrace();
				}*/
				
				//currentCredits.setText("Current Credits: %d", creditLoad);
			}
		});

		// button for removing courses to the desired courses list
		Button removeCourse = new Button("Remove Course");
		removeCourse.setStyle(Theme.toStyle(theme.removeCourseColors()));
		removeCourse.setMaxWidth(primaryStage.getWidth() / 4);
		grid.add(removeCourse, 2, 4, 1, 1);
		removeCourse.setOnAction(action -> {
			if (desiredCoursesSelection.getSelectionModel().getSelectedItem() != null) {
				allCoursesList.add(desiredCoursesSelection.getSelectionModel().getSelectedItem());
				desiredCoursesList.remove(desiredCoursesSelection.getSelectionModel().getSelectedItem());
			}
		});

		// control for creating the schedule
		Button schedule = new Button("Create Schedule");
		schedule.setStyle(Theme.toStyle(theme.scheduleButtonColors()));
		schedule.setMaxWidth(primaryStage.getWidth() / 4);
		grid.add(schedule, 2, 5, 1, 1);
		GridPane.setValignment(schedule, VPos.BOTTOM);
		schedule.setOnAction(action -> {
			GridPane scheduleGridpane = new GridPane();
			scheduleGridpane.setHgap(10);
			scheduleGridpane.setVgap(10);
			scheduleGridpane.setAlignment(Pos.CENTER);
			scene.setRoot(scheduleGridpane);

			TabPane schedulesView = new TabPane();
			schedulesView.minWidthProperty().bind(primaryStage.widthProperty().subtract(20));
			schedulesView.minHeightProperty().bind(primaryStage.heightProperty().subtract(100));
			GridPane.setValignment(schedulesView, VPos.BOTTOM);

			//List<String> desiredCourses = desiredCoursesSelection.getItems();
			Set<String> desiredCourses = new HashSet<>(desiredCoursesSelection.getItems());

			Set<Set<Course>> validSchedules = ScheduleMaker.build(desiredCourses, Scraper.getAllSemesters().get(semesters.getValue()));	
			List<Set<Course>> temp = new ArrayList<>(validSchedules);
			temp.stream().filter(s -> temp.indexOf(s) < 3);
			// display schedules
			for ( Set<Course> indvSchedule : temp.stream().filter(s -> temp.indexOf(s) < 3).collect(Collectors.toList())) {
				
				if (validSchedules.isEmpty()) {
					System.out.println("there are no valid schedules!!! :(");
					break;
				}

				// create the calendar
				Tab tab = new Tab("Potential Schedule");
				setInfo();
				CalendarView calendarView = new CalendarView();
				
				// if the schedule is empty, don't try to print it (the code will break)
				if ( indvSchedule.isEmpty() ) {
					System.out.println("schedule is empty!!! :(");
					continue;
				}
				
				//calendarView.showDate(indvSchedule.toArray()[0].g);
				//calendarView.showWeekPage();
				CalendarSource sources = new CalendarSource("My Courses");
				calendarView.getCalendarSources().add(sources);

				// add entries to the calendar
				int i = 0;
				for (Course cur : indvSchedule) {
					Calendar cal = new Calendar(cur.toString());
					sources.getCalendars().add(cal);
					cal.setStyle(Style.getStyle(i++));
					if (!cur.getStartDate().equals(Course.TBA_DATE) && !cur.getEndDate().equals(Course.TBA_DATE)) {
						if (!cur.isSplitClass()) {
							if (!cur.getStartTime(0).equals(Course.TBA_TIME) && !cur.getEndTime(0).equals(Course.TBA_TIME)) {
								Entry<String> entry = new Entry<>(cur.toString() + " CRN: " + cur.getCRN());
								entry.changeStartDate(cur.getStartDate().with(TemporalAdjusters.nextOrSame(cur.firstDay())));
								entry.changeStartTime(cur.getStartTime(0)); // ZonedDateTime doesn't have any precision for minutes?
								entry.changeEndTime(cur.getEndTime(0));
								entry.setRecurrenceRule("RRULE:FREQ=WEEKLY;BYDAY=" + cur.getDays().toString().replaceAll("\\[|\\]", "").replace(" ", "") + ";INTERVAL=1;UNTIL=" + cur.getEndDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "T235959Z");
								cal.addEntry(entry);
								calendarView.showDate(entry.getStartDate());
							}
						} else {
							Course.CourseTimeIterator it = (Course.CourseTimeIterator) cur.iterator();
							for (List<LocalTime[]> times = it.next(); it.hasNext(); times = it.next()) {
								for (LocalTime[] time : times) {
									Entry<String> entry = new Entry<>(cur.toString() + " CRN: " + cur.getCRN());
									entry.changeStartDate(cur.getStartDate().with(TemporalAdjusters.nextOrSame(it.getDayEnum())));
									entry.changeStartTime(time[0]); // ZonedDateTime doesn't have any precision for minutes?
									entry.changeEndTime(time[1]);
									entry.setRecurrenceRule("RRULE:FREQ=WEEKLY;BYDAY=" + it.getRRuleDay() + ";INTERVAL=1;UNTIL=" + cur.getEndDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "T235959Z");
									cal.addEntry(entry);
									calendarView.showDate(entry.getStartDate());
								}
							}
						}
					}
				}
				
				calendarView.showWeekPage();
				tab.setContent(calendarView);
				schedulesView.getTabs().addAll(tab);
			}

			// controls between the calendar and class select pages
			Button backButton = new Button("BACK");
			backButton.setOnAction(e -> {
				scene.setRoot(grid);
			});
			scheduleGridpane.add(backButton, 0, 0);
			GridPane.setHalignment(backButton, HPos.LEFT);
			scheduleGridpane.add(schedulesView, 0, 1);
			DONOTUSE.countDown();
		});

		// display the GUI
		primaryStage.setScene(scene);
		primaryStage.show();
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

	/**
	 * Loads the list of all semester codes from the scraper
	 */
	private void loadSemesters() {
		new Thread(() -> {
			try {
				Scraper.getAllSemesters();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Platform.runLater(() -> {
				try {
					allSemestersList.addAll(Scraper.getAllSemesters().keySet());
				} catch (Exception e) {
		 			e.printStackTrace();
				}
				loadCourses(Scraper.getAllSemesters().get(semesters.getValue()));
			});
		}).start();
	}

	/**
	 * Loads all the course information for a given semester
	 * @param semesterID - the semester from which the courses will be loaded
	 */
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

	/**
	 * Displays the loading circle for when loadCourses() is called
	 */
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

	/**
	 * Determines which semester is the next semester in the academic calendar
	 * @return the semester code for the upcoming semester
	 */
	private String defaultSemester() {
		LocalDateTime now = LocalDateTime.now();
		if (now.getMonthOfYear() >= 8 && now.getMonthOfYear() <= 12) {
			return "Spring " + (now.getYear() + 1);
		} else {
			return "Fall " + now.getYear();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Application.launch(args);
	}
}
