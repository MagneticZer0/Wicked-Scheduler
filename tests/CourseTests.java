import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.Arrays;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CourseTests {

	@ParameterizedTest(name = "Test TBA no conflict with [{arguments}]")
	@ValueSource(strings = {"M", "T", "MT", "W", "MW", "TW", "MTW", "R", "MR", "TR", "MTR", "WR", "MWR", "TWR", "MTWR", "F", "MF", "TF", "MTF", "WF", "MWF", "TWF", "MTWF", "RF", "MRF", "TRF", "MTRF", "WRF", "MWRF", "TWRF", "MTWRF"})
	public void TBANoConflict(String days) throws ParseException {
		Course first = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "TBA", "12", "No one", "01/12-01/13|2019", 500);
		Course second = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "TBA", "12", "No one", "01/12-01/13|2019", 500);
		assertFalse("Courses at exact same times should be conflicting!", first.conflicts(second));
	}

	@ParameterizedTest(name = "Test exact course conflict with [{arguments}]")
	@ValueSource(strings = {"M", "T", "MT", "W", "MW", "TW", "MTW", "R", "MR", "TR", "MTR", "WR", "MWR", "TWR", "MTWR", "F", "MF", "TF", "MTF", "WF", "MWF", "TWF", "MTWF", "RF", "MRF", "TRF", "MTRF", "WRF", "MWRF", "TWRF", "MTWRF"})
	public void exactCourseConflict(String days) throws ParseException {
		Course first = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:15 pm-2:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		Course second = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:15 pm-2:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		assertTrue("Courses at exact same times should be conflicting!", first.conflicts(second));
	}

	@ParameterizedTest(name = "Test overlapping course conflict with [{arguments}]")
	@ValueSource(strings = {"M", "T", "MT", "W", "MW", "TW", "MTW", "R", "MR", "TR", "MTR", "WR", "MWR", "TWR", "MTWR", "F", "MF", "TF", "MTF", "WF", "MWF", "TWF", "MTWF", "RF", "MRF", "TRF", "MTRF", "WRF", "MWRF", "TWRF", "MTWRF"})
	public void overlapCourseConflict(String days) throws ParseException {
		Course first = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:15 pm-2:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		Course second = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:35 pm-2:35 pm", "12", "No one", "01/12-01/13|2019", 500);
		assertTrue("Courses at overlapping same times should be conflicting!", first.conflicts(second));
	}

	@ParameterizedTest(name = "Test super overlapping course conflict with [{arguments}]")
	@ValueSource(strings = {"M", "T", "MT", "W", "MW", "TW", "MTW", "R", "MR", "TR", "MTR", "WR", "MWR", "TWR", "MTWR", "F", "MF", "TF", "MTF", "WF", "MWF", "TWF", "MTWF", "RF", "MRF", "TRF", "MTRF", "WRF", "MWRF", "TWRF", "MTWRF"})
	public void superOverlapCourseConflict(String days) throws ParseException {
		Course first = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:15 pm-2:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		Course second = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "12:35 pm-3:35 pm", "12", "No one", "01/12-01/13|2019", 500);
		assertTrue("Courses at super overlapping same times should be conflicting!", first.conflicts(second));
	}

	@ParameterizedTest(name = "Test start/end course conflict with [{arguments}]")
	@ValueSource(strings = {"M", "T", "MT", "W", "MW", "TW", "MTW", "R", "MR", "TR", "MTR", "WR", "MWR", "TWR", "MTWR", "F", "MF", "TF", "MTF", "WF", "MWF", "TWF", "MTWF", "RF", "MRF", "TRF", "MTRF", "WRF", "MWRF", "TWRF", "MTWRF"})
	public void startEndCourseConflict(String days) throws ParseException {
		Course first = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:15 pm-2:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		Course second = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "2:15 pm-3:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		assertTrue("Courses at end when one starts should be conflicting!", first.conflicts(second));
	}

	@ParameterizedTest(name = "Test start/end walk time course conflict with [{arguments}]")
	@ValueSource(strings = {"M"})
	public void startEnd5CourseConflict(String days) throws ParseException {
		Course first = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:15 pm-2:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		Course second = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "2:20 pm-3:20 pm", "12", "No one", "01/12-01/13|2019", 500);
		assertTrue("Courses at end without 5 minutes in between should be conflicting!", first.conflicts(second));
	}

	@ParameterizedTest(name = "Test different dates no conflict with [{arguments}]")
	@ValueSource(strings = {"M", "T", "MT", "W", "MW", "TW", "MTW", "R", "MR", "TR", "MTR", "WR", "MWR", "TWR", "MTWR", "F", "MF", "TF", "MTF", "WF", "MWF", "TWF", "MTWF", "RF", "MRF", "TRF", "MTRF", "WRF", "MWRF", "TWRF", "MTWRF"})
	public void differentDatesNoConflict(String days) throws ParseException {
		Course first = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:15 pm-2:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		Course second = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "2:15 pm-3:15 pm", "12", "No one", "01/14-01/15|2019", 500);
		assertFalse("Courses should not be conflicting if on different dates!", first.conflicts(second));
	}

	@ParameterizedTest(name = "Test different days no conflict with [{arguments}]")
	@ValueSource(strings = {"M", "T", "MT", "W", "MW", "TW", "MTW", "R", "MR", "TR", "MTR", "WR", "MWR", "TWR", "MTWR", "F", "MF", "TF", "MTF", "WF", "MWF", "TWF", "MTWF", "RF", "MRF", "TRF", "MTRF", "WRF", "MWRF", "TWRF", "MTWRF"})
	public void differentDaysNoConflict(String days) throws ParseException {
		Course first = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:15 pm-2:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		Course second = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", invert(days), "2:15 pm-3:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		assertFalse("Courses should not be conflicting if on different days!", first.conflicts(second));
	}

	@ParameterizedTest(name = "Test same day no conflict with [{arguments}]")
	@ValueSource(strings = {"M", "T", "MT", "W", "MW", "TW", "MTW", "R", "MR", "TR", "MTR", "WR", "MWR", "TWR", "MTWR", "F", "MF", "TF", "MTF", "WF", "MWF", "TWF", "MTWF", "RF", "MRF", "TRF", "MTRF", "WRF", "MWRF", "TWRF", "MTWRF"})
	public void sameDaysNoConflict(String days) throws ParseException {
		Course first = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:15 pm-2:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		Course second = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "3:15 pm-4:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		assertFalse("Courses should not be conflicting if on same days with no conflicts!", first.conflicts(second));
	}

	@ParameterizedTest(name = "Test same day no conflict multiple times with [{arguments}]")
	@ValueSource(strings = {"M", "T", "MT", "W", "MW", "TW", "MTW", "R", "MR", "TR", "MTR", "WR", "MWR", "TWR", "MTWR", "F", "MF", "TF", "MTF", "WF", "MWF", "TWF", "MTWF", "RF", "MRF", "TRF", "MTRF", "WRF", "MWRF", "TWRF", "MTWRF"})
	public void sameDaysMultipleTimesNoConflict(String days) throws ParseException {
		Course first = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:15 pm-2:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		first.addDayandTime(days + "|5:15 pm-6:15 pm");
		Course second = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "3:15 pm-4:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		assertFalse("Courses should not be conflicting if on same days with multiple times and no conflicts!", first.conflicts(second));
	}


	private String invert(String input) {
		String result = "MTWRF";
		for(String s : input.split("")) {
			result = result.replaceAll(s, "");
		}
		return result;
	}
}
