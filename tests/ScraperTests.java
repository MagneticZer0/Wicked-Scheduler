import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.*;

import collections.MultiMap;

public class ScraperTests {

	static Map<String, String> semesters;
	static List<String> categories;
	static MultiMap<String, Course> courses;

	/**
	 * This code may look complex, but it's just so that all 4 lines of code (The
	 * ones that have Scraper) are executed in parallel as opposed to sequentially
	 * just to save some testing time.
	 */
	@BeforeAll
	public static void setup() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(3);
		new Thread(() -> {
			semesters = Scraper.getAllSemesters();
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
				Scraper.loadCourses();
				courses = Scraper.getAllClasses("200108");
			} catch (IOException e) {
				e.printStackTrace();
			}
			latch.countDown();
		}).start();
		latch.await();
	}

	@Test
	public void getAllSemesters() throws IOException {
		assertAll("getAllSemesters map is incorrect", () -> assertTrue(semesters.keySet().contains("Fall 2001"), "Fall 2001 is not in semesters!"), () -> assertEquals("200108", semesters.get("Fall 2001"), "Fall 2001 id does not match expected!"));
	}

	@Test
	public void getCategories() {
		assertEquals(30, categories.size(), "getCategories size is incorrect!");
	}

	@Test
	public void getAllClasses() {
		assertEquals(3515, courses.allValues().size(), "getAllClasses didn't get all classes!");
	}

	@Test
	public void saveCourses() {
		Scraper.saveCourses();
		assertTrue(new File(System.getProperty("user.home") + "\\Wicked-Scheduler\\coursesMap.ser").exists(), "File saving didn't work correctly");
	}
}
