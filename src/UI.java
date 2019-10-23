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
		allCoursesList.addAll("CS1121 - Intro", "CS1122 - Intro Pt. 2", "CS2311 - Discrete", "CS2321 - Data");
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
		desiredCoursesSelection.setMaxWidth(firststage.getWidth() / 8);
		grid.add(desiredCoursesSelection, 2, 2, 1, 4);

		Button addCourse = new Button("Add Course");
		addCourse.setStyle("-fx-background-color: #32CD32;");
		addCourse.setMaxWidth(firststage.getWidth() / 8);
		grid.add(addCourse, 1, 3, 1, 1);

		Button removeCourse = new Button("Remove Course");
		removeCourse.setStyle("-fx-background-color: #ff0000;");
		removeCourse.setMaxWidth(firststage.getWidth() / 8);
		grid.add(removeCourse, 1, 4, 1, 1);

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
