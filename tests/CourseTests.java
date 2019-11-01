import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CourseTests {

	Course tester;

	@BeforeEach
	public void setup() throws ParseException {
		tester = new Course("4", "FA", "20", true, Arrays.asList(48d), "Yang Gang", "MWR", "4:00 am-6:00 pm", "2", "Gang, Yang", "01/18-01/17|2020", 47000);
	}

	@ParameterizedTest(name = "Test TBA no conflict with [{arguments}]")
	@ValueSource(strings = { "M", "T", "MT", "W", "MW", "TW", "MTW", "R", "MR", "TR", "MTR", "WR", "MWR", "TWR", "MTWR", "F", "MF", "TF", "MTF", "WF", "MWF", "TWF", "MTWF", "RF", "MRF", "TRF", "MTRF", "WRF", "MWRF", "TWRF", "MTWRF" })
	public void TBANoConflict(String days) throws ParseException {
		Course first = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "TBA", "12", "No one", "01/12-01/13|2019", 500);
		Course second = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "TBA", "12", "No one", "01/12-01/13|2019", 500);
		assertFalse(first.conflicts(second), "Courses at exact same times should be conflicting!");
	}

	@ParameterizedTest(name = "Test exact course conflict with [{arguments}]")
	@ValueSource(strings = { "M", "T", "MT", "W", "MW", "TW", "MTW", "R", "MR", "TR", "MTR", "WR", "MWR", "TWR", "MTWR", "F", "MF", "TF", "MTF", "WF", "MWF", "TWF", "MTWF", "RF", "MRF", "TRF", "MTRF", "WRF", "MWRF", "TWRF", "MTWRF" })
	public void exactCourseConflict(String days) throws ParseException {
		Course first = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:15 pm-2:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		Course second = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:15 pm-2:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		assertTrue(first.conflicts(second), "Courses at exact same times should be conflicting!");
	}

	@ParameterizedTest(name = "Test overlapping course conflict with [{arguments}]")
	@ValueSource(strings = { "M", "T", "MT", "W", "MW", "TW", "MTW", "R", "MR", "TR", "MTR", "WR", "MWR", "TWR", "MTWR", "F", "MF", "TF", "MTF", "WF", "MWF", "TWF", "MTWF", "RF", "MRF", "TRF", "MTRF", "WRF", "MWRF", "TWRF", "MTWRF" })
	public void overlapCourseConflict(String days) throws ParseException {
		Course first = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:15 pm-2:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		Course second = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:35 pm-2:35 pm", "12", "No one", "01/12-01/13|2019", 500);
		assertTrue(first.conflicts(second), "Courses at overlapping same times should be conflicting!");
	}

	@ParameterizedTest(name = "Test super overlapping course conflict with [{arguments}]")
	@ValueSource(strings = { "M", "T", "MT", "W", "MW", "TW", "MTW", "R", "MR", "TR", "MTR", "WR", "MWR", "TWR", "MTWR", "F", "MF", "TF", "MTF", "WF", "MWF", "TWF", "MTWF", "RF", "MRF", "TRF", "MTRF", "WRF", "MWRF", "TWRF", "MTWRF" })
	public void superOverlapCourseConflict(String days) throws ParseException {
		Course first = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:15 pm-2:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		Course second = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "12:35 pm-3:35 pm", "12", "No one", "01/12-01/13|2019", 500);
		assertTrue(first.conflicts(second), "Courses at super overlapping same times should be conflicting!");
	}

	@ParameterizedTest(name = "Test start/end course conflict with [{arguments}]")
	@ValueSource(strings = { "M", "T", "MT", "W", "MW", "TW", "MTW", "R", "MR", "TR", "MTR", "WR", "MWR", "TWR", "MTWR", "F", "MF", "TF", "MTF", "WF", "MWF", "TWF", "MTWF", "RF", "MRF", "TRF", "MTRF", "WRF", "MWRF", "TWRF", "MTWRF" })
	public void startEndCourseConflict(String days) throws ParseException {
		Course first = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:15 pm-2:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		Course second = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "2:15 pm-3:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		assertTrue(first.conflicts(second), "Courses at end when one starts should be conflicting!");
	}

	@ParameterizedTest(name = "Test start/end walk time course conflict with [{arguments}]")
	@ValueSource(strings = { "M", "T", "MT", "W", "MW", "TW", "MTW", "R", "MR", "TR", "MTR", "WR", "MWR", "TWR", "MTWR", "F", "MF", "TF", "MTF", "WF", "MWF", "TWF", "MTWF", "RF", "MRF", "TRF", "MTRF", "WRF", "MWRF", "TWRF", "MTWRF" })
	public void startEnd5CourseConflict(String days) throws ParseException {
		Course first = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:15 pm-2:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		Course second = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "2:20 pm-3:20 pm", "12", "No one", "01/12-01/13|2019", 500);
		assertTrue(first.conflicts(second), "Courses at end without 5 minutes in between should be conflicting!");
	}

	@ParameterizedTest(name = "Test different dates no conflict with [{arguments}]")
	@ValueSource(strings = { "M", "T", "MT", "W", "MW", "TW", "MTW", "R", "MR", "TR", "MTR", "WR", "MWR", "TWR", "MTWR", "F", "MF", "TF", "MTF", "WF", "MWF", "TWF", "MTWF", "RF", "MRF", "TRF", "MTRF", "WRF", "MWRF", "TWRF", "MTWRF" })
	public void differentDatesNoConflict(String days) throws ParseException {
		Course first = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:15 pm-2:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		Course second = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "2:15 pm-3:15 pm", "12", "No one", "01/14-01/15|2019", 500);
		assertFalse(first.conflicts(second), "Courses should not be conflicting if on different dates!");
	}

	@ParameterizedTest(name = "Test different days no conflict with [{arguments}]")
	@ValueSource(strings = { "M", "T", "MT", "W", "MW", "TW", "MTW", "R", "MR", "TR", "MTR", "WR", "MWR", "TWR", "MTWR", "F", "MF", "TF", "MTF", "WF", "MWF", "TWF", "MTWF", "RF", "MRF", "TRF", "MTRF", "WRF", "MWRF", "TWRF", "MTWRF" })
	public void differentDaysNoConflict(String days) throws ParseException {
		Course first = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:15 pm-2:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		Course second = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", invert(days), "2:15 pm-3:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		assertFalse(first.conflicts(second), "Courses should not be conflicting if on different days!");
	}

	@ParameterizedTest(name = "Test same day no conflict with [{arguments}]")
	@ValueSource(strings = { "M", "T", "MT", "W", "MW", "TW", "MTW", "R", "MR", "TR", "MTR", "WR", "MWR", "TWR", "MTWR", "F", "MF", "TF", "MTF", "WF", "MWF", "TWF", "MTWF", "RF", "MRF", "TRF", "MTRF", "WRF", "MWRF", "TWRF", "MTWRF" })
	public void sameDaysNoConflict(String days) throws ParseException {
		Course first = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:15 pm-2:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		Course second = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "3:15 pm-4:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		assertFalse(first.conflicts(second), "Courses should not be conflicting if on same days with no conflicts!");
	}

	@ParameterizedTest(name = "Test same day no conflict multiple times with [{arguments}]")
	@ValueSource(strings = { "M", "T", "MT", "W", "MW", "TW", "MTW", "R", "MR", "TR", "MTR", "WR", "MWR", "TWR", "MTWR", "F", "MF", "TF", "MTF", "WF", "MWF", "TWF", "MTWF", "RF", "MRF", "TRF", "MTRF", "WRF", "MWRF", "TWRF", "MTWRF" })
	public void sameDaysMultipleTimesNoConflict(String days) throws ParseException {
		Course first = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:15 pm-2:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		first.addDayandTime(days + "|5:15 pm-6:15 pm");
		Course second = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "3:15 pm-4:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		assertFalse(first.conflicts(second), "Courses should not be conflicting if on same days with multiple times and no conflicts!");
	}

	@ParameterizedTest(name = "Test same day conflict multiple times with [{arguments}]")
	@ValueSource(strings = { "M", "T", "MT", "W", "MW", "TW", "MTW", "R", "MR", "TR", "MTR", "WR", "MWR", "TWR", "MTWR", "F", "MF", "TF", "MTF", "WF", "MWF", "TWF", "MTWF", "RF", "MRF", "TRF", "MTRF", "WRF", "MWRF", "TWRF", "MTWRF" })
	public void sameDaysMultipleTimesConflict(String days) throws ParseException {
		Course first = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:15 pm-2:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		first.addDayandTime(days + "|4:15 pm-6:15 pm");
		Course second = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "3:15 pm-4:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		assertTrue(first.conflicts(second), "Courses should be conflicting if on same days with multiple times and conflicts!");
	}

	@Test
	public void courseCRN() {
		assertEquals(4, tester.getCRN(), "Course CRN is not correct!");
	}

	@Test
	public void getCredits() {
		assertEquals(48, tester.getCredits()[0], 0, "Course credits is not correct!");
	}

	@Test
	public void getDays() {
		List<String> results = tester.getDays();
		assertTrue(results.containsAll(Arrays.asList("MO", "WE", "TH")), "Course getDays does not return all days!");
	}

	@Test
	public void getRemaining() {
		assertEquals(2, tester.getRemaining(), "Course getRemaining doesn't return the correct value");
	}

	@Test
	public void getInstructor() {
		assertEquals("Gang, Yang", tester.getInstructor(), "Course getInstructor doesn't return the correct value");
	}

	@Test
	public void getStartDate() {
		assertEquals("2020-01-18", tester.getStartDate().toString(), "Course getStartDate doesn't return the correct value");
	}

	@Test
	public void getEndDate() {
		assertEquals("2020-01-17", tester.getEndDate().toString(), "Course getEndDate doesn't return the correct value");
	}

	@Test
	public void getFee() {
		assertEquals(47000, tester.getFee(), 0, "Course getFee doesn't return the correct value");
	}

	@Test
	public void tostring() {
		assertEquals("FA20 - Yang Gang Lab", tester.toString(), "Course toString doesn't return correct representation!");
	}

	@Test
	public void firstDay() {
		assertEquals(DayOfWeek.MONDAY, tester.firstDay(), "First day is not the Monday enum!");
	}

	@Test
	public void isSplitClass() {
		assertFalse(tester.isSplitClass(), "The tester is not a split class yet!");
		tester.addDayandTime("T|1:05 am-1:06 pm");
		assertTrue(tester.isSplitClass(), "The tester is supposed to be a split class now!");
	}

	@Test
	public void iterator() {
		ArrayList<DayOfWeek> days = new ArrayList<>();
		ArrayList<String> daysRRule = new ArrayList<>();
		for (Course.CourseTimeIterator it = (Course.CourseTimeIterator) tester.iterator(); it.hasNext(); it.next()) {
			days.add(it.getDayEnum());
			daysRRule.add(it.getRRuleDay());
		}
		assertTrue(days.contains(DayOfWeek.THURSDAY));
		assertTrue(daysRRule.contains("TH"));
	}

	private String invert(String input) {
		String result = "MTWRF";
		for (String s : input.split("")) {
			result = result.replaceAll(s, "");
		}
		return result;
	}
}
