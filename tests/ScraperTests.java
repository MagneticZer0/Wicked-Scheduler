import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ScraperTests {

	static Map<String, String> semesters;
	static Map<String, String> editSemesters;
	static List<String> categories;
	static MultiMap<String, Course> courses;

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
		assertAll("getAllSemesters map is incorrect", () -> assertTrue("Fall 2001 is not in semesters!", semesters.keySet().contains("Fall 2001 (View only)")), () -> assertEquals("Fall 2001 id does not match expected!", "200108", semesters.get("Fall 2001 (View only)")));
	}

	@Test
	public void getEditableSemesters() {
		assertFalse("getEditableSemesters contains a key with view only!", editSemesters.keySet().stream().anyMatch(e -> e.contains("view only")));
	}

	@Test
	public void getCategories() {
		assertEquals("getCategories size is incorrect!", 30, categories.size());
	}

	@Test
	public void getAllClasses() {
		assertEquals("getAllClasses didn't get all classes!", 3515, courses.allValues().size());
	}
}
