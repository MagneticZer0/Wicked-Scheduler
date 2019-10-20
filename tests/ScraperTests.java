import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.*;

import collections.MultiMap;

public class ScraperTests {

	static Map<String, String> semesters;
	static Map<String, String> editSemesters;
	static List<String> categories;
	static MultiMap<String, Course> courses;

	/**
	 * This code may look complex, but it's just so that all 4 lines of code (The
	 * ones that have Scraper) are executed in parallel as opposed to sequentially
	 * just to save some testing time.
	 */
	@BeforeAll
	public static void setup() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(4);
		new Thread(() -> {
			try {
				semesters = Scraper.getAllSemesters();
			} catch (IOException e) {
				e.printStackTrace();
			}
			latch.countDown();
		}).start();
		new Thread(() -> {
			try {
				editSemesters = Scraper.getEditableSemesters();
			} catch (IOException e) {
				e.printStackTrace();
			}
			latch.countDown();
		}).start();
		new Thread(() -> {
			try {
				categories = Scraper.getCategories("200108");
			} catch (IOException e) {
				e.printStackTrace();
			}
			latch.countDown();
		}).start();
		new Thread(() -> {
			try {
				courses = Scraper.getAllClasses("200108");
			} catch (IOException | ParseException e) {
				e.printStackTrace();
			}
			latch.countDown();
		}).start();
		latch.await();
	}

	@Test
	public void getAllSemesters() throws IOException {
		assertAll("getAllSemesters map is incorrect", () -> assertTrue(semesters.keySet().contains("Fall 2001 (View only)"), "Fall 2001 is not in semesters!"), () -> assertEquals("200108", semesters.get("Fall 2001 (View only)"), "Fall 2001 id does not match expected!"));
	}

	@Test
	public void getEditableSemesters() {
		assertFalse(editSemesters.keySet().stream().anyMatch(e -> e.contains("view only")), "getEditableSemesters contains a key with view only!");
	}

	@Test
	public void getCategories() {
		assertEquals(30, categories.size(), "getCategories size is incorrect!");
	}

	@Test
	public void getAllClasses() {
		assertEquals(3515, courses.allValues().size(), "getAllClasses didn't get all classes!");
	}
}
