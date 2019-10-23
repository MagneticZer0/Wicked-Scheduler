import collections.MultiMap;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author Alex Grant, Coleman Clarstein
 *
 */
public class UI extends Application {

	/**
	 * this function builds the GUI and displays it to the user once everything has
	 * been initialized
	 *
	 * @param firststage - a pre-made stage created by Application.launch
	 */
	public void start(Stage firststage) throws Exception {

		// set window properties
		firststage.setTitle("Wicked Scheduler");
		firststage.setX(250);
		firststage.setY(50);
		firststage.setWidth(1000);
		firststage.setHeight(700);
		firststage.setResizable(false);
		firststage.initStyle(StageStyle.DECORATED);

		// create a grid for GUI elements
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setAlignment(Pos.CENTER);

		// elements regarding all courses
		Label allCoursesLabel = new Label("Offered Courses:");
		grid.add(allCoursesLabel, 0, 0, 1, 1);

		TextField allCoursesSearch = new TextField();
		allCoursesSearch.setPromptText("Input Course Code");
		allCoursesSearch.setMaxWidth(firststage.getWidth() / 8);
		grid.add(allCoursesSearch, 0, 1, 1, 1);

		ObservableList<String> allCoursesList = FXCollections.observableArrayList();
		
		// this block should be added in the semester select action where "200108" is replaced by the desired semester  //
		MultiMap<String,Course> allCourses = Scraper.getAllClasses("200108");											//	
		for(String code : allCourses.keySet() ) {																		//
			allCoursesList.add( code );																					//
		}																												//
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		FilteredList<String> allCoursesFilter = new FilteredList<>(allCoursesList, d -> true); // Make them all visible at first
		ListView<String> allCoursesSelection = new ListView<>(allCoursesFilter.sorted());
		allCoursesSearch.textProperty().addListener((obs, oldVal, newVal) -> {
			allCoursesFilter.setPredicate(d -> {
				for (String s : d.split(" - ")) { // Allows you to search by code or title (CS1121 or Intro)
					if (newVal == null || newVal.isEmpty() || s.toLowerCase().contains(newVal.toLowerCase())) { // Case Insensitive
						return true; // Display all values if it's empty
					}
				}
				return false;
			});
		});
		allCoursesSelection.setPlaceholder(new Label("Nothing is here!"));
		allCoursesSelection.setMaxWidth(firststage.getWidth() / 8);
		grid.add(allCoursesSelection, 0, 2, 1, 4);

		// elements regarding desired courses
		Label desiredCoursesLabel = new Label("Desired Courses:");
		grid.add(desiredCoursesLabel, 2, 0, 1, 1);

		TextField desiredCoursesSearch = new TextField();
		desiredCoursesSearch.setPromptText("Input Course Code");
		desiredCoursesSearch.setMaxWidth(firststage.getWidth() / 8);
		grid.add(desiredCoursesSearch, 2, 1, 1, 1);

		ObservableList<String> desiredCoursesList = FXCollections.observableArrayList();
		FilteredList<String> desiredCoursesFilter = new FilteredList<>(desiredCoursesList, d -> true); // Make them all visible at first
		ListView<String> desiredCoursesSelection = new ListView<>(desiredCoursesFilter.sorted());
		desiredCoursesSearch.textProperty().addListener((obs, oldVal, newVal) -> {
			desiredCoursesFilter.setPredicate(d -> {
				for (String s : d.split(" - ")) { // Allows you to search by code or title (CS1121 or Intro)
					if (newVal == null || newVal.isEmpty() || s.toLowerCase().contains(newVal.toLowerCase())) { // Case Insensitive
						return true; // Display all values if it's empty
					}
				}
				return false;
			});
		});
		desiredCoursesSelection.setPlaceholder(new Label("Nothing is here!"));
		desiredCoursesSelection.setMaxWidth(firststage.getWidth() /8 );
		grid.add(desiredCoursesSelection, 2, 2, 1, 4);

		Button addCourse = new Button("Add Course");
		addCourse.setStyle("-fx-background-color: #32CD32;");
		addCourse.setMaxWidth(firststage.getWidth() / 4);
		grid.add(addCourse, 1, 3, 1, 1);

		Button removeCourse = new Button("Remove Course");
		removeCourse.setStyle("-fx-background-color: #ff0000;");
		removeCourse.setMaxWidth(firststage.getWidth() / 4);
		grid.add(removeCourse, 1, 4, 1, 1);
		
		Button schedule = new Button("Create Schedule");
		schedule.setMaxWidth(firststage.getWidth()/4);
		grid.add(schedule, 1, 7, 1, 1);
		
		//semester list
		MenuItem semester0 = new MenuItem("Fall 2019");
		MenuItem semester1 = new MenuItem("Spring 2020");
		MenuItem semester2 = new MenuItem("Summer 2020");
		MenuItem semester3 = new MenuItem("Fall 2020");
		MenuItem semester4 = new MenuItem("Spring 2021");
		MenuButton semesters = new MenuButton("Select Semester", null, semester0, semester1, semester2, semester3, semester4);
		semesters.setMaxWidth(firststage.getWidth()/4);
		grid.add(semesters, 1, 2 ,1, 1);

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
			if (desiredCoursesSelection.getSelectionModel().getSelectedItem() != null) {
				//implement scheduling logic
			}
		});
		// display the GUI
		Scene scene1 = new Scene(grid, 200, 100);
		firststage.setScene(scene1);
		firststage.show();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Application.launch(args);
	}
}
