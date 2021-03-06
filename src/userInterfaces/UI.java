package userInterfaces;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;

import collections.CustomListView;
import collections.MultiMap;
import impl.com.calendarfx.view.DateControlSkin;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import logic.BruteForceScheduleMaker;
import logic.Course;
import logic.ExecutionCode;
import logic.Globals;
import logic.Scraper;
import themes.Theme;

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
	 * The property that tracks the current credit load
	 */
	private final DoubleProperty creditLoad = new SimpleDoubleProperty(0);
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
	@Override
	public void start(Stage primaryStage) {
		Globals.init();
		Theme theme = Globals.theme(); // This is just to make things easier

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
			Globals.popupException().exit();
			Globals.browser().exit();
			Platform.exit();
		});

		// create a grid for GUI elements
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setAlignment(Pos.CENTER);
		grid.setStyle(Theme.toBackgroundStyle(theme.backgroundColor()));
		Scene scene = new Scene(grid, 200, 100);

		// semester list
		semesters = new ComboBox<>(allSemestersList.filtered(d -> allSemestersList.indexOf(d) < 5)); // Only do 5 most relevant
		semesters.setPromptText("Select Semester");
		semesters.setMaxWidth(primaryStage.getWidth() / 4);
		semesters.setOnAction(e -> {
			Globals.popupException().writeInstruction(ExecutionCode.SEMESTERCHANGED);
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
		allCoursesSearch.textProperty().addListener((obs, oldVal, newVal) -> allCoursesFilter.setPredicate(d -> {
			if (newVal.matches("credits:\\[(<|>|=)[=]?[0-9]+[.]?[0-9]*\\]")) {
				try {
					double[] credits = Scraper.getAllClasses(Scraper.getAllSemesters().get(semesters.getValue())).get(d).get(0).getCredits();
					boolean creditsCheck = false;
					if (newVal.charAt(newVal.indexOf("credits:[") + 9) == '<') {
						if (newVal.charAt(newVal.indexOf("credits:[") + 10) == '=') {
							creditsCheck = credits[credits.length - 1] <= Double.parseDouble(newVal.substring(newVal.indexOf("credits:[") + 11, newVal.indexOf("]")));
						} else {
							creditsCheck = credits[credits.length - 1] < Double.parseDouble(newVal.substring(newVal.indexOf("credits:[") + 10, newVal.indexOf("]")));
						}
					} else if (newVal.charAt(newVal.indexOf("credits:[") + 9) == '>') {
						if (newVal.charAt(newVal.indexOf("credits:[") + 10) == '=') {
							creditsCheck = credits[0] >= Double.parseDouble(newVal.substring(newVal.indexOf("credits:[") + 11, newVal.indexOf("]")));
						} else {
							creditsCheck = credits[0] > Double.parseDouble(newVal.substring(newVal.indexOf("credits:[") + 10, newVal.indexOf("]")));
						}
					} else if (newVal.charAt(newVal.indexOf("credits:[") + 9) == '=') {
						creditsCheck = credits[0] == Double.parseDouble(newVal.substring(newVal.indexOf("credits:[") + 10, newVal.indexOf("]")));
					}
					String rest = newVal.replaceAll("credits:\\[(<|>|=)[=]?[0-9]+[.]?[0-9]*\\]", "").trim();
					if (rest.length() == 0) {
						return creditsCheck;
					} else {
						return creditsCheck && d.toLowerCase().contains(rest.toLowerCase());
					}
				} catch (IOException e) {
					Globals.popupException().writeError(e);
				}
			} else {
				return (newVal == null || newVal.isEmpty() || d.toLowerCase().contains(newVal.trim().toLowerCase()));
			}
			return true;
		})); // Display all values if it's empty and it's case insensitive
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

		desiredCoursesList.addListener(new ListChangeListener<String>() {
			@Override
			public void onChanged(Change<? extends String> arg0) {
				updateCreditLoad();
			}
		});
		FilteredList<String> desiredCoursesFilter = new FilteredList<>(desiredCoursesList, d -> true); // Make them all visible at first
		CustomListView desiredCoursesSelection = new CustomListView(desiredCoursesList, desiredCoursesFilter.sorted(), semesters);
		desiredCoursesSearch.textProperty().addListener((obs, oldVal, newVal) -> desiredCoursesFilter.setPredicate(d -> newVal == null || newVal.isEmpty() || d.toLowerCase().contains(newVal.toLowerCase()))); // Display all values if it's empty and it's case insensitive
		desiredCoursesSelection.setPlaceholder(new Label("Nothing is here!"));
		desiredCoursesSelection.setMinWidth(primaryStage.getWidth() / 4);
		grid.add(desiredCoursesSelection, 3, 2, 2, 4);

		// identify the upcoming semester and load it by default
		loadSemesters();
		semesters.setValue(defaultSemester());

		// button for displaying the help page
		Button helpButton = new Button("Help");
		helpButton.setStyle(Theme.toStyle(theme.helpButtonColors()));
		helpButton.setMaxWidth(primaryStage.getWidth() / 4);
		grid.add(helpButton, 0, 0, 1, 1);
		helpButton.setOnAction(action -> {
			Globals.popupException().writeInstruction(ExecutionCode.HELPBUTTONPRESSED);
			Globals.browser().loadHelp();
		});

		// button for adding courses to the desired courses list
		Button addCourse = new Button("Add Course");
		addCourse.setStyle(Theme.toStyle(theme.addCourseColors()));
		addCourse.setMaxWidth(primaryStage.getWidth() / 4);
		grid.add(addCourse, 2, 3, 1, 1);
		addCourse.setOnAction(action -> {
			if (allCoursesSelection.getSelectionModel().getSelectedItem() != null) {
				Globals.popupException().writeInstruction(ExecutionCode.ADDCOURSEPRESSED);
				desiredCoursesList.add(allCoursesSelection.getSelectionModel().getSelectedItem());
				allCoursesList.remove(allCoursesSelection.getSelectionModel().getSelectedItem());
			}
		});

		// button for removing courses to the desired courses list
		Button removeCourse = new Button("Remove Course");
		removeCourse.setStyle(Theme.toStyle(theme.removeCourseColors()));
		removeCourse.setMaxWidth(primaryStage.getWidth() / 4);
		grid.add(removeCourse, 2, 4, 1, 1);
		removeCourse.setOnAction(action -> {
			if (desiredCoursesSelection.getSelectionModel().getSelectedItem() != null) {
				Globals.popupException().writeInstruction(ExecutionCode.REMOVECOURSEPRESSED);
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
			Globals.popupException().writeInstruction(ExecutionCode.CREATESCHEDULEPRESSED);
			scene.setCursor(Cursor.WAIT);
			new Thread(() -> {
				Platform.runLater(() -> {
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

					Globals.popupException().writeInstruction(ExecutionCode.SCHEDULECREATIONSTART);
					Set<Set<Course>> validSchedules = BruteForceScheduleMaker.build(new HashSet<>(desiredCoursesSelection.getItems()), Scraper.getAllSemesters().get(semesters.getValue()));
					Globals.popupException().writeInstruction(ExecutionCode.SCHEDULECREATIONEND);
					List<Set<Course>> temp = new ArrayList<>(validSchedules);
					validSchedules = temp.stream().filter(s -> temp.indexOf(s) < 3).collect(Collectors.toSet());
					temp.removeIf(e -> true); // Some cleaning up, for memory saving purposes

					HashMap<Integer, Iterable<Course>> tabCourses = new HashMap<>();
					// display schedules
					int j = 0;
					for (Set<Course> indvSchedule : validSchedules) {

						tabCourses.put(j++, indvSchedule);

						// create the calendar
						Tab tab = new Tab("Potential Schedule");
						setInfo();
						CalendarView calendarView = new CalendarView();
						calendarView.showDate(indvSchedule.stream().findFirst().get().getStartDate());

						CalendarSource sources = new CalendarSource("My Courses");
						calendarView.getCalendarSources().add(sources);

						// add entries to the calendar
						int i = 0;
						for (Course cur : indvSchedule) {
							Calendar cal = new Calendar(cur.toString());
							cal.setReadOnly(true);
							sources.getCalendars().add(cal);
							cal.setStyle(Style.getStyle(i++));
							if (!cur.isTBAClass()) {
								if (!cur.isSplitClass()) {
									if (!cur.getStartTime(0).equals(Course.TBA_TIME) && !cur.getEndTime(0).equals(Course.TBA_TIME)) {
										Entry<String> entry = new Entry<>(cur.toString() + " CRN: " + cur.getCRN());
										entry.changeStartDate(cur.getStartDate().with(TemporalAdjusters.nextOrSame(cur.firstDay())));
										entry.changeStartTime(cur.getStartTime(0)); // ZonedDateTime doesn't have any precision for minutes?
										entry.changeEndTime(cur.getEndTime(0));
										entry.setRecurrenceRule("RRULE:FREQ=WEEKLY;BYDAY=" + cur.getDays().toString().replaceAll("\\[|\\]", "").replace(" ", "") + ";INTERVAL=1;UNTIL=" + cur.getEndDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "T235959Z");
										entry.changeEndDate(entry.getStartDate()); // By default the end date is todays date, so it messes up if this isn't here.
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
											entry.changeEndDate(entry.getStartDate()); // By default the end date is todays date, so it messes up if this isn't here.
											cal.addEntry(entry);
										}
									}
								}
							}
							if (cal.findEntries(LocalDate.MIN, LocalDate.MAX, ZoneId.systemDefault()).size() > 0) {
								calendarView.showDateTime(LocalDateTime.ofInstant(cal.getEarliestTimeUsed(), ZoneOffset.MAX));
							}
						}
						calendarView.showWeekPage();
						tab.setContent(calendarView);
						schedulesView.getTabs().addAll(tab);
					}

					// controls between the calendar and class select pages
					Button backButton = new Button("Back");
					backButton.setStyle(Theme.toStyle(theme.backButtonColors()));
					backButton.setOnAction(e -> {
						Globals.popupException().writeInstruction(ExecutionCode.BACKBUTTONPRESSED);
						scene.setRoot(grid);
					});
					scheduleGridpane.add(backButton, 0, 0);
					GridPane.setHalignment(backButton, HPos.LEFT);
					GridPane.setMargin(backButton, new Insets(5, 0, 0, 0));

					Button crnButton = new Button("Get CRNs");
					crnButton.setStyle(Theme.toStyle(theme.backButtonColors()));
					crnButton.setOnAction(e -> {
						Globals.popupException().writeInstruction(ExecutionCode.GETCRNS);
						Globals.popupText().clear();
						for (Course c : tabCourses.get(schedulesView.getSelectionModel().getSelectedIndex())) {
							Globals.popupText().write("CRN: " + c.getCRN() + " -> " + c.toString());
						}
					});
					scheduleGridpane.add(crnButton, 0, 0);
					GridPane.setHalignment(crnButton, HPos.RIGHT);
					GridPane.setMargin(crnButton, new Insets(5, 0, 0, 0));

					// handle if there are no schedules
					if (validSchedules.isEmpty()) {
						Globals.popupException().writeInstruction(ExecutionCode.NOVALIDSCHEDULES);
						GridPane.setHalignment(backButton, HPos.CENTER);
						crnButton.setVisible(false);
						MultiMap<Course, Course> conflicts = Course.getConflicts(desiredCoursesSelection.getItems().parallelStream().map(s -> {
							try {
								return Scraper.getAllClasses(Scraper.getAllSemesters().get(semesters.getValue())).get(s);
							} catch (IOException e1) {
								Globals.popupException().writeError(e1);
								return new ArrayList<Course>();
							}
						}).collect(Collectors.toList()).parallelStream().flatMap(List::stream).collect(Collectors.toList()));
						TreeMap<Integer, Course> numConflicts = new TreeMap<>();
						for (Course c : conflicts.keySet()) {
							numConflicts.put(conflicts.get(c).size(), c);
						}
						Label noSchedulesLabel = new Label("There are no feasible schedules that include all selected courses!\nTry removing " + numConflicts.lastEntry().getValue() + " it conflicts the most!");
						noSchedulesLabel.setStyle(Theme.toTextStyle(theme.textColor()));
						GridPane.setHalignment(noSchedulesLabel, HPos.CENTER);
						scheduleGridpane.add(noSchedulesLabel, 0, 1);
					} else {
						scheduleGridpane.add(schedulesView, 0, 1);
					}
					DONOTUSE.countDown();
				});
				scene.setCursor(Cursor.DEFAULT);
			}).start();
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
				Globals.popupException().writeError(e);
			}
			Platform.runLater(() -> {
				try {
					allSemestersList.addAll(Scraper.getAllSemesters().keySet());
				} catch (Exception e) {
					Globals.popupException().writeError(e);
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
				Globals.popupException().writeError(e);
			}
			Platform.runLater(() -> {
				try {
					allCoursesList.addAll(Scraper.getAllClasses(semesterID).keySet());
				} catch (Exception e) {
					Globals.popupException().writeError(e);
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
		Globals.popupException().writeInstruction(ExecutionCode.SETDEFAULTSEMESTER);
		LocalDateTime now = LocalDateTime.now();
		if (now.getMonthValue() >= 8 && now.getMonthValue() <= 12) {
			if (Scraper.getAllSemesters().containsKey("Spring " + (now.getYear() + 1))) {
				return "Spring " + (now.getYear() + 1);
			} else {
				return "Fall " + now.getYear();
			}
		} else {
			if (Scraper.getAllSemesters().containsKey("Fall " + now.getYear())) {
				return "Fall " + now.getYear();
			} else {
				return "Spring " + now.getYear();
			}
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
		Thread visualUpdate = new Thread(() -> {
			while (node.lookup(lookup) == null) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// Do nothing
				}
			}
			node.lookup(lookup).setStyle(Theme.toBackgroundStyle(color));
		});
		visualUpdate.setDaemon(true);
		visualUpdate.start();
	}
}
