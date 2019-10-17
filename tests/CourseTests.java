import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CourseTests {

	@ParameterizedTest(name = "Test exact course conflict with [{arguments}]")
	@ValueSource(strings = {"M", "T", "MT", "W", "MW", "TW", "MTW", "R", "MR", "TR", "MTR", "WR", "MWR", "TWR", "MTWR", "F", "MF", "TF", "MTF", "WF", "MWF", "TWF", "MTWF", "RF", "MRF", "TRF", "MTRF", "WRF", "MWRF", "TWRF", "MTWRF"})
	public void exactCourseConflict(String days) throws ParseException {
		Course first = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:15 pm-2:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		Course second = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:15 pm-2:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		assertTrue("Classses at exact same times should be conflicting!", first.conficts(second) && second.conficts(first));
	}

	@ParameterizedTest(name = "Test start/end course conflict with [{arguments}]")
	@ValueSource(strings = {"M", "T", "MT", "W", "MW", "TW", "MTW", "R", "MR", "TR", "MTR", "WR", "MWR", "TWR", "MTWR", "F", "MF", "TF", "MTF", "WF", "MWF", "TWF", "MTWF", "RF", "MRF", "TRF", "MTRF", "WRF", "MWRF", "TWRF", "MTWRF"})
	public void startEndCourseConflict(String days) throws ParseException {
		Course first = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:15 pm-2:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		Course second = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "2:15 pm-3:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		assertTrue("Classses at end when one starts should be conflicting!", first.conficts(second) && second.conficts(first));
	}

	@ParameterizedTest(name = "Test different dates no conflict with [{arguments}]")
	@ValueSource(strings = {"M", "T", "MT", "W", "MW", "TW", "MTW", "R", "MR", "TR", "MTR", "WR", "MWR", "TWR", "MTWR", "F", "MF", "TF", "MTF", "WF", "MWF", "TWF", "MTWF", "RF", "MRF", "TRF", "MTRF", "WRF", "MWRF", "TWRF", "MTWRF"})
	public void differentDatesNoConflict(String days) throws ParseException {
		Course first = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:15 pm-2:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		Course second = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "2:15 pm-3:15 pm", "12", "No one", "01/14-01/15|2019", 500);
		assertFalse("Courses should not be conflicting if on different dates!", first.conficts(second) && second.conficts(first));
	}

	@ParameterizedTest(name = "Test different days no conflict with [{arguments}]")
	@ValueSource(strings = {"M", "T", "MT", "W", "MW", "TW", "MTW", "R", "MR", "TR", "MTR", "WR", "MWR", "TWR", "MTWR", "F", "MF", "TF", "MTF", "WF", "MWF", "TWF", "MTWF", "RF", "MRF", "TRF", "MTRF", "WRF", "MWRF", "TWRF", "MTWRF"})
	public void differentDaysNoConflict(String days) throws ParseException {
		Course first = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", days, "1:15 pm-2:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		Course second = new Course("0", "EV", "0", false, Arrays.asList(1d), "Test", invert(days), "2:15 pm-3:15 pm", "12", "No one", "01/12-01/13|2019", 500);
		assertFalse("Courses should not be conflicting if on different days!", first.conficts(second) && second.conficts(first));
	}

	private String invert(String input) {
		String result = "MTWRF";
		for(String s : input.split("")) {
			result = result.replaceAll(s, "");
		}
		return result;
	}
}
