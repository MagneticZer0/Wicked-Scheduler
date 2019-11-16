package headedTests;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;

import frontEnd.UI;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static CustomMatchers.CaseInsensitiveSubstringMatcher.*;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This class is used for testing UI elements. Please be very careful when
 * editing things in here or the UI case this uses Java's Reflection and things
 * can be messed up very easily casuing unintended behavior.
 */
@TestMethodOrder(OrderAnnotation.class)
public class UITests {

	private static UI ui = null;
	private static Scene scene = null;
	private FxRobot robot = new FxRobot();

	@BeforeAll
	public static void setup() throws Exception {
		Stage stage = FxToolkit.registerPrimaryStage();
		ui = (UI) FxToolkit.setupApplication(UI.class);

		Field latch = UI.class.getDeclaredField("DONOTUSE");
		latch.setAccessible(true);

		scene = stage.getScene();

		((CountDownLatch) latch.get(ui)).await(); // Wait for everything to be finished inside the UI
	}

	@Test
	public void defSem() throws InterruptedException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, NoSuchMethodException, InvocationTargetException {
		ObservableList<String> list = getUIField("allSemestersList", ObservableList.class);

		assertThat("Default semester not in semesters list!", list, hasItem(execMethod("defaultSemester", String.class))); // Check if the semesters list has the "default" semester
	}

	@Test
	@Order(1)
	public void addCourse() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, InterruptedException {
		Button addCourse = (Button) scene.getRoot().getChildrenUnmodifiable().filtered(e -> e.toString().contains("Add Course")).get(0);

		ListView<String> allCourses = (ListView<String>) scene.getRoot().getChildrenUnmodifiable().filtered(e -> e instanceof ListView).filtered(e -> ((ListView) e).getItems().size() > 0).get(0);
		ListView<String> desiredCourses = (ListView<String>) scene.getRoot().getChildrenUnmodifiable().filtered(e -> e instanceof ListView).filtered(e -> ((ListView) e).getItems().size() == 0).get(0);

		for (int i = 0; i < 5; i++) {
			allCourses.getSelectionModel().selectFirst();
			robot.moveTo(addCourse).clickOn();
		}
		assertAll("Adding course did not work properly!", () -> assertThat("Course not removed from all courses list", allCourses.getItems(), not(hasItems(desiredCourses.getItems().toArray(new String[0])))), () -> assertThat("Desired courses list not correct size!", desiredCourses.getItems(), hasSize(5)));
	}

	@Test
	@Order(2)
	public void filterDesiredCourses() {
		TextField desiredSearch = (TextField) scene.getRoot().getChildrenUnmodifiable().filtered(e -> e instanceof TextField).filtered(e -> ((TextField) e).getPromptText().contains("Desired")).get(0);
		ListView<String> desiredCourses = (ListView<String>) scene.getRoot().getChildrenUnmodifiable().filtered(e -> e instanceof ListView).filtered(e -> ((ListView) e).getItems().size() == 5).get(0);

		robot.moveTo(desiredSearch).clickOn().write("CS");
		assertThat(desiredCourses.getItems(), everyItem(containsIgnoringCase("cs")));
		robot.eraseText(2);
	}

	@Test
	@Order(3)
	public void removeCourse() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, InterruptedException {
		Button removeCourse = (Button) scene.getRoot().getChildrenUnmodifiable().filtered(e -> e.toString().contains("Remove Course")).get(0);

		ListView<String> desiredCourses = (ListView<String>) scene.getRoot().getChildrenUnmodifiable().filtered(e -> e instanceof ListView).filtered(e -> ((ListView) e).getItems().size() == 5).get(0);

		for (int i = 0; i < 5; i++) {
			desiredCourses.getSelectionModel().selectFirst();
			robot.moveTo(removeCourse).clickOn();
		}
		assertThat("Desired courses list not correct size!", desiredCourses.getItems(), empty());
	}

	@Test
	@Order(4)
	public void filterAllCourses() {
		TextField allSearch = (TextField) scene.getRoot().getChildrenUnmodifiable().filtered(e -> e instanceof TextField).filtered(e -> !((TextField) e).getPromptText().contains("Desired")).get(0);

		robot.moveTo(allSearch).clickOn().write("CS");

		ListView<String> allCourses = (ListView<String>) scene.getRoot().getChildrenUnmodifiable().filtered(e -> e instanceof ListView).filtered(e -> ((ListView) e).getItems().size() > 0).get(0);
		assertThat(allCourses.getItems(), everyItem(containsIgnoringCase("cs")));
		robot.eraseText(2);
	}

	@Test
	@Order(5)
	public void schedule() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InterruptedException {
		Button sched = (Button) scene.getRoot().getChildrenUnmodifiable().filtered(e -> e instanceof Button).filtered(e -> ((Button) e).getText().equals("Create Schedule")).get(0);

		Field latch = UI.class.getDeclaredField("DONOTUSE");
		latch.setAccessible(true);
		latch.set(ui, new CountDownLatch(1));

		Button addCourse = (Button) scene.getRoot().getChildrenUnmodifiable().filtered(e -> e.toString().contains("Add Course")).get(0);
		ListView<String> allCourses = (ListView<String>) scene.getRoot().getChildrenUnmodifiable().filtered(e -> e instanceof ListView).filtered(e -> ((ListView) e).getItems().size() > 0).get(0);

		for (int i = 0; i < 3; i++) {
			allCourses.getSelectionModel().selectFirst();
			robot.moveTo(addCourse).clickOn();
		}

		robot.moveTo(sched).clickOn();
		((CountDownLatch) latch.get(ui)).await();
		TabPane schedules = (TabPane) scene.getRoot().getChildrenUnmodifiable().filtered(e -> e instanceof TabPane).get(0);
		assertTrue(schedules.getChildrenUnmodifiable().size() <= 3);
	}

	@Test
	@Order(6)
	public void goBack() {
		Button back = (Button) scene.getRoot().getChildrenUnmodifiable().filtered(e -> e instanceof Button).filtered(e -> ((Button) e).getText().equals("BACK")).get(0);
		robot.moveTo(back).clickOn();
	}

	@Test
	@Order(7)
	public void semList() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		assertTrue(((ComboBox) scene.getRoot().getChildrenUnmodifiable().filtered(e -> e instanceof ComboBox).get(0)).getItems().size() <= 5, "Semesters list contains more than 5 items!");
	}

	@AfterAll
	public static void tearDown() throws TimeoutException {
		FxToolkit.cleanupApplication(ui);
	}

	private static <T> T getUIField(String name, Class<T> type) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		Field field = UI.class.getDeclaredField(name);
		field.setAccessible(true);
		return (T) field.get(ui);
	}

	public static <T> T execMethod(String name, Class<T> returnType, Object... parameters) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class<?>[] parameterTypes = new Class[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			parameterTypes[i] = parameters[i].getClass();
		}

		Method method = UI.class.getDeclaredMethod(name, parameterTypes);
		method.setAccessible(true);

		return (T) method.invoke(ui, parameters);
	}

}