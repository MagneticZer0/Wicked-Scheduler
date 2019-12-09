package userInterfaces;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Set;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;

import collections.CustomListView;
import impl.com.calendarfx.view.DateControlSkin;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import logic.BruteForceScheduleMaker;
import logic.Course;
import logic.GreedyQuickScheduleMaker;
import logic.Scraper;
import themes.DefaultTheme;
import themes.Theme;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.concurrent.CountDownLatch;

import org.joda.time.LocalDateTime;

/**
 * @author Alex Grant, Coleman Clarstein, Harley Merkaj
 */
public class UI extends Application {

	/**
	 * A list of all the courses
	 */
	private ObservableList<String> allCoursesList = FXCollections.observableArrayList();
	/**
	 * A list of all the desired courses
	 */
	private ObservableList<String> desiredCoursesList = FXCollections.observableArrayList();
	/**
	 * A list of all semesters
	 */
	private ObservableList<String> allSemestersList = FXCollections.observableArrayList();
	/**
	 * The list view used for all courses available
	 */
	private CustomListView allCoursesSelection = null;
	/**
	 * A loading box for when something is being loaded from the disc
	 */
	private VBox loadingBox = null;
	/**
	 * The combo box for the semesters
	 */
	private ComboBox<String> semesters = null;
	/**
	 * The theme that the UI is using
	 */
	private Theme theme = new DefaultTheme();
	/**
	 * The property that tracks the current credit load
	 */
	private final DoubleProperty creditLoad = new SimpleDoubleProperty(0);
	/**
	 * This is the popup that will log exceptions
	 */
	private static PopupException popupException = new PopupException("Ignore and Continue", "Save and Exit");
	/**
	 * The browser used to show course descriptions
	 */
	public static final Browser browser = new Browser();
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
		primaryStage.setOnCloseRequest(e -> {
			popupException.exit();
			browser.exit();
			Platform.exit();
		});

		// create a grid for GUI elements
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setAlignment(Pos.CENTER);
		grid.setStyle(Theme.toBackgroundStyle(theme.backgroundColor()));
		Scene scene = new Scene(grid, 200, 100);
		// grid.setGridLinesVisible(true);

		// semester list
		semesters = new ComboBox<>(allSemestersList.filtered(d -> allSemestersList.indexOf(d) < 5)); // Only do 5 most relevant
		semesters.setPromptText("Select Semester");
		semesters.setMaxWidth(primaryStage.getWidth() / 4);
		semesters.setOnAction(e -> {
			desiredCoursesList.clear();
			creditLoad.set(0);
			loadCourses(Scraper.getAllSemesters().get(semesters.getValue()));
		});
		grid.add(semesters, 2, 2, 1, 1);

		// elements regarding all courses
		Label allCoursesLabel = new Label("Offered Courses:");
		allCoursesLabel.setStyle(Theme.toTextStyle(theme.textColor()));
		grid.add(allCoursesLabel, 0, 1, 1, 1);
		TextField allCoursesSearch = new TextField();

		allCoursesSearch.setPromptText("Search Courses");
		allCoursesSearch.setMaxWidth(primaryStage.getWidth() / 4);
		grid.add(allCoursesSearch, 1, 1, 1, 1);

		FilteredList<String> allCoursesFilter = new FilteredList<>(allCoursesList, d -> true); // Make them all visible at first
		allCoursesSelection = new CustomListView(allCoursesList, allCoursesFilter.sorted(), semesters);
		allCoursesSelection.setUpdateFunction(this::updateCreditLoad);
		allCoursesSearch.textProperty().addListener((obs, oldVal, newVal) -> allCoursesFilter.setPredicate(d -> (newVal == null || newVal.isEmpty() || d.toLowerCase().contains(newVal.toLowerCase())))); // Display all values if it's empty and it's case insensitive
		allCoursesSelection.setPlaceholder(new Label("Nothing is here!"));
		allCoursesSelection.setMinWidth(primaryStage.getWidth() / 4);
		grid.add(allCoursesSelection, 0, 2, 2, 4);

		// elements regarding desired courses
		Label currentCredits = new Label();
		currentCredits.setStyle(Theme.toTextStyle(theme.textColor()));
		currentCredits.textProperty().bind(Bindings.concat("Current credits: ", creditLoad.asString()));
		currentCredits.textFillProperty().bind(Bindings.when(creditLoad.lessThan(12).or(creditLoad.greaterThan(18))).then(new ReadOnlyObjectWrapper<>(Paint.valueOf(theme.creditInvalidColor().toString()))).otherwise(new ReadOnlyObjectWrapper<>(Paint.valueOf(theme.creditValidColor().toString()))));
		grid.add(currentCredits, 4, 6, 1, 1);

		Label desiredCoursesLabel = new Label("Desired Courses:");
		desiredCoursesLabel.setStyle(Theme.toTextStyle(theme.textColor()));
		grid.add(desiredCoursesLabel, 3, 1, 1, 1);

		TextField desiredCoursesSearch = new TextField();
		desiredCoursesSearch.setPromptText("Search Desired Courses");
		desiredCoursesSearch.setMaxWidth(primaryStage.getWidth() / 4);
		grid.add(desiredCoursesSearch, 4, 1, 1, 1);

		FilteredList<String> desiredCoursesFilter = new FilteredList<>(desiredCoursesList, d -> true); // Make them all visible at first
		CustomListView desiredCoursesSelection = new CustomListView(desiredCoursesList, desiredCoursesFilter.sorted(), semesters);
		desiredCoursesSelection.setUpdateFunction(this::updateCreditLoad);
		desiredCoursesSearch.textProperty().addListener((obs, oldVal, newVal) -> desiredCoursesFilter.setPredicate(d -> newVal == null || newVal.isEmpty() || d.toLowerCase().contains(newVal.toLowerCase()))); // Display all values if it's empty and it's case insensitive
		desiredCoursesSelection.setPlaceholder(new Label("Nothing is here!"));
		desiredCoursesSelection.setMinWidth(primaryStage.getWidth() / 4);
		grid.add(desiredCoursesSelection, 3, 2, 2, 4);

		// identify the upcoming semester and load it by default
		loadSemesters();
		semesters.setValue(defaultSemester());

		// button for displaying the help page
		Button helpButton = new Button("Help");
		helpButton.setStyle(Theme.toBackgroundStyle(Color.CORAL.desaturate()));
		helpButton.setMaxWidth(primaryStage.getWidth() / 4);
		grid.add(helpButton, 0, 0, 1, 1);
		helpButton.setOnAction(action -> {
			browser.loadHelp();
		});

		// button for adding courses to the desired courses list
		Button addCourse = new Button("Add Course");
		addCourse.setStyle(Theme.toStyle(theme.addCourseColors()));
		addCourse.setMaxWidth(primaryStage.getWidth() / 4);
		grid.add(addCourse, 2, 3, 1, 1);
		addCourse.setOnAction(action -> {
			if (allCoursesSelection.getSelectionModel().getSelectedItem() != null) {
				desiredCoursesList.add(allCoursesSelection.getSelectionModel().getSelectedItem());
				allCoursesList.remove(allCoursesSelection.getSelectionModel().getSelectedItem());
				updateCreditLoad();
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
				updateCreditLoad();
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
			scheduleGridpane.setStyle(Theme.toBackgroundStyle(theme.backgroundColor()));
			scheduleGridpane.setHgap(10);
			scheduleGridpane.setVgap(10);
			scheduleGridpane.setAlignment(Pos.CENTER);
			scene.setRoot(scheduleGridpane);

			TabPane schedulesView = new TabPane();
			backgroundColorThread(schedulesView, ".tab-header-area .tab-header-background", theme.tabHeaderColor()); // There's something weird about the TabPane so this is the way I have to change the color
			schedulesView.minWidthProperty().bind(primaryStage.widthProperty().subtract(20));
			schedulesView.minHeightProperty().bind(primaryStage.heightProperty().subtract(100));
			GridPane.setValignment(schedulesView, VPos.BOTTOM);

			ArrayList<ArrayList<Course>> finalSchedule = GreedyQuickScheduleMaker.build(desiredCoursesList, semesters.getValue());
			HashMap<Integer, ArrayList<Course>> tabCourses = new HashMap<>();

			// display schedules
			for (int j = 0; j < finalSchedule.size(); j++) {
				// create the calendar
				Tab tab = new Tab("Schedule " + (j + 1));
				setInfo();
				CalendarView calendarView = new CalendarView();

				// if the schedule is empty, don't try to print it (the code will break)
				if (finalSchedule.get(j).isEmpty()) {
					continue;
				}

				tabCourses.put(j + 1, finalSchedule.get(j));

				calendarView.showDate(finalSchedule.get(j).get(0).getStartDate());
				calendarView.showWeekPage();
				CalendarSource sources = new CalendarSource("My Courses");
				calendarView.getCalendarSources().add(sources);

				// add entries to the calendar
				int i = 0;
				for (Course cur : finalSchedule.get(j)) {
					Calendar cal = new Calendar(cur.toString());
					cal.setReadOnly(true);
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
								}
							}
						}
					}
				}

				tab.setContent(calendarView);
				schedulesView.getTabs().addAll(tab);
			}

			// controls between the calendar and class select pages
			Button backButton = new Button("Back");
			backButton.setStyle(Theme.toStyle(theme.backButtonColors()));
			backButton.setOnAction(e -> scene.setRoot(grid));
			scheduleGridpane.add(backButton, 0, 0);
			GridPane.setHalignment(backButton, HPos.LEFT);
			GridPane.setMargin(backButton, new Insets(5, 0, 0, 0));

			Button moreSchedules = new Button("More Schedules");
			moreSchedules.setStyle(Theme.toStyle(theme.backButtonColors()));
			//moreSchedules.setOnAction(e -> /*call colemans scedule algo*/);
			scheduleGridpane.add(moreSchedules, 0, 0);
			GridPane.setHalignment(moreSchedules, HPos.CENTER);
			GridPane.setMargin(moreSchedules, new Insets(5, 0, 0, 0));

			Button crnButton = new Button("Get CRNs");
			crnButton.setStyle(Theme.toStyle(theme.backButtonColors()));
			crnButton.setOnAction(e -> {
				//browser.register(Scraper.getAllSemesters().get(semesters.getSelectionModel().getSelectedItem()), tabCourses.get(Integer.parseInt(schedulesView.getSelectionModel().getSelectedItem().getText().split(" ")[1])));
			});
			scheduleGridpane.add(crnButton, 0, 0);
			GridPane.setHalignment(crnButton, HPos.RIGHT);
			GridPane.setMargin(crnButton, new Insets(5, 0, 0, 0));

			scheduleGridpane.add(schedulesView, 0, 1);
			DONOTUSE.countDown();
		});

		// display the GUI
		primaryStage.setScene(scene);
		primaryStage.show();
		DONOTUSE.countDown();
	}

	/**
	 * This is want runs when JavaFX is exiting, currently all this does it save the
	 * loaded courses
	 */
	@Override
	public void stop() {
		Scraper.saveCourses();
	}

	/**
	 * Updates the credit load by putting all the courses into a parallel stream,
	 * mapping them to a double (their credits), and finally summing them up and
	 * updating the interval DoubleProperty.
	 */
	private void updateCreditLoad() {
		creditLoad.set(desiredCoursesList.parallelStream().mapToDouble(course -> {
			try {
				return Scraper.getAllClasses(Scraper.getAllSemesters().get(semesters.getValue())).get(course).get(0).getCredits()[0];
			} catch (IOException e1) {
				return 0; // Anything wrong getting credit value? Assume 0.
			}
		}).sum());
	}

	/**
	 * Used in case your IDE does not support JavaFX
	 */
	public static void main(String[] args) {
		Application.launch(args);
	}

	/**
	 * This is a hacky way to disable the thing that CalendarFX outputs to console
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
	 *
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
	 *
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
	 * Used to update a color for a node that is currently not visible, and will be
	 * visible at an unknown time in the future
	 *
	 * @param node   The node to change the color of
	 * @param lookup CSS object to lookup
	 * @param color  The color for it to be changed to
	 */
	private void backgroundColorThread(Node node, String lookup, Color color) {
		new Thread(() -> {
			while (node.lookup(lookup) == null) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			node.lookup(lookup).setStyle(Theme.toBackgroundStyle(color));
		}).start();
	}
}
