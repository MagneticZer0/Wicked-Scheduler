package headlessTests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import logic.Course;
import logic.ScheduleMaker;
import logic.Scraper;

public class ScheduleTests {

	private static final String semesterID = "202001";
	public static ArrayList<String> courses;

	@BeforeAll
	public static void setup() throws IOException {
		Scraper.getAllClasses(semesterID); // Preemptively load all the classes
		courses = new ArrayList<>();
	}

	@BeforeEach
	public void reset() {
		courses.clear();
	}

//	@Test
//	public void manySections() throws IOException {
//		courses.add("CH1150 - University Chemistry I");
//		courses.add("University Chemistry Lab I Lab");
//		courses.add("MA1161 - Calculus Plus W/ Technology I");
//		courses.add("MA1161 - Calculus Plus w/ Technology I Lab");
//		ArrayList<ArrayList<Course>> schedules = ScheduleMaker.build(courses, semesterID);
//		assertThat("", courses, hasSize(greaterThan(0)));
//		for(Collection<Course> schedule : schedules) {
//			Iterator<String> courseIterator = courses.iterator();
//			// Check - Remove
//			while (courseIterator.hasNext()) {
//				boolean found = false;
//				String courseCheck = courseIterator.next();
//				if (schedule.toString().contains(courseCheck)) {
//					courseIterator.remove();
//					courses.remove(courseCheck.toString());
//					found = true;
//				}
//			}
//		}
//	}
//
//	@Test
//	public void schedule1() throws IOException {
//		courses.add("CS3000 - Ethical/Social Aspects of Comp");
//		courses.add("CS3311 - Formal Models of Computation");
//		courses.add("CS4821 - Data Mining");
//		courses.add("EC4050 - Game Theory/Strategic Behavior");
//		courses.add("HON3150 - Pavlis Seminar II Lab");
//		courses.add("MA3450 - Introduction to Real Analysis");
//		courses.add("PE1170 - TaeKwonDo Lab");
//	}
}
