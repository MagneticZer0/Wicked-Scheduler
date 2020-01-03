package headedTests;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jtimer.Runner;
import org.jtimer.Annotations.BeforeClass;
import org.jtimer.Annotations.DisplayName;
import org.jtimer.Annotations.Settings;
import org.jtimer.Annotations.Time;
import org.jtimer.Misc.Setting;

import logic.BruteForceScheduleMaker;
import logic.GreedyQuickScheduleMaker;

@Settings({ Setting.BEST_FIT })
public class ScheduleTimeTests {

	List<String> courses = new ArrayList<>();
	long counter = 0; // If you want some variable to keep the repetition that you're on.

	public static void main(String[] args) throws Throwable {
		Runner.time(ScheduleTimeTests.class);
	}

	@BeforeClass
	public void setup() {
		courses.clear();
		courses.add("EC4050 - Game Theory/Strategic Behavior");
		courses.add("AF3020 - Effective Com II - Non AFROTC");
		courses.add("BL4450 - Limnology");
		courses.add("CH1151 - University Chemistry Lab I Lab");
		courses.add("MA1161 - Calculus Plus w/ Technology I Lab");
		courses.add("FA3400 - Keweenaw Symphony Orchestra Lab");
		courses.add("CEE4507 - Distribution and Collection Lab");
		courses.add("CEE5350 - Life Cycle Engineering Lab");
		courses.add("CS3311 - Formal Models of Computation");
		courses.add("BL4530 - Senior Research Capstone Exp");
		courses.add("MA3160 - Multivariable Calc with Tech Lab");
		courses.add("BL4752 - Cancer Biology");
		courses.add("CH1150 - University Chemistry I");
		courses.add("MA1161 - Calculus Plus w/ Technology I");
		courses.add("CH1163 - University Chem Recitation II");
		courses.add("BL3611 - Phlebotomy Lab");
		courses.add("MA2720 - Statistical Methods");
		courses.add("MA3160 - Multivariable Calc with Tech");
		courses.add("ACC2000 - Accounting Principles I");
		courses.add("EE4800 - Antennas");
		courses.add("ACC4600 - Advanced Tax Topics");
		courses.add("EE2174 - Digital Logic and Lab Lab");
		courses.add("ENT1960 - Wireless Communication Lab");
		courses.add("PE0121 - Beginning Snowboarding Lab");
		courses.add("BL3190 - Evolution");
		courses.add("BL5038 - Epigenetics");
		courses.add("UN5953 - Graduate Continuous Enrollment");
		courses.add("UN5951 - Grad Cont. Enrl't-Special Circ");
		courses.add("UN5390 - Scientific Computing");
		courses.add("UN5100 - Water and Society Colloquium");
		courses.add("UN5004 - Graduate Co-op Education IV");
		courses.add("UN5003 - Graduate Co-op Education III");
		courses.add("UN5002 - Graduate Co-op Education II");
		courses.add("UN5000 - Graduate Co-op Education I");
		courses.add("UN3990 - Entrepreneurship Indep. Study");
		courses.add("UN3005 - Undergrad Co-op Education IV");
		courses.add("UN3004 - Undergrad Co-op Education III");
		courses.add("UN3003 - Undergrad Co-op Education II");
		courses.add("UN3002 - Undergrad Co-op Education I");
		courses.add("UN2600 - Fund Nanoscale Science & Eng");
		courses.add("UN1025 - Global Issues");
		courses.add("UN1015 - Composition");
		courses.add("UN0500 - Effective Scholarship");
		courses.add("SU5999 - Thesis Research in IGT");
		courses.add("SU5998 - Practical Experience in IGT");
		courses.add("SU5800 - Master's Graduate Seminar");
		courses.add("SU5541 - Close-range Photogrammetry");
		courses.add("SU5023 - Geospatial Positioning");
		courses.add("SU5013 - Hydrographic Mapping Lab");
		courses.add("SU5013 - Hydrographic Mapping");
		courses.add("SU5012 - Geospatial Data MIning Lab");
		courses.add("SU5012 - Geospatial Data MIning");
		courses.add("SU5010 - Geospatia Concepts, Tech, Data");
		courses.add("SU4999 - Professional Practice Review");
		courses.add("SU4900 - Capstone Design Project Lab");
		courses.add("SU4900 - Capstone Design Project");
		courses.add("SU4180 - Land Subdivision Design");
		courses.add("SU4060 - Geodesy Lab");
		courses.add("SU4060 - Geodesy");
		courses.add("SU4013 - Hydrographic Mapping Lab");
		courses.add("SU4013 - Hydrographic Mapping");
		courses.add("SU4012 - Geospatial Data Mining Lab");
		courses.add("SU4012 - Geospatial Data Mining");
		courses.add("SU4010 - Geospatia Concepts, Tech, Data");
		courses.add("SU3210 - Site Planning and Development Lab");
		courses.add("SU3210 - Site Planning and Development");
		courses.add("SU2220 - Route/Construction Surveying");
		courses.add("SU2000 - Introduction to Surveying Lab");
		courses.add("SU2000 - Introduction to Surveying");
		courses.add("SS6600 - PhD. Dissertation Research");
		courses.add("SS6500 - Directed Reading");
		courses.add("SS6002 - Research Design");
		courses.add("SS5990 - Graduate Research");
		courses.add("SS5950 - Professional Development");
		courses.add("SS5502 - Historical Archaeology");
		courses.add("SS5015 - VISTA Field Service Lab");
		courses.add("SS5010 - Directed Study");
		courses.add("SS4990 - Directed Study in Anthropology");
		courses.add("SS4920 - Internship Experience");
		courses.add("SS4910 - Prof Devel for Soc Sciences");
		courses.add("SS4900 - Memory and Heritage");
		courses.add("SS4630 - Adv Research in Soc Sci");
		courses.add("SS4552 - Historical Archaeology");
	}

	@Time(repeat = 82)
	@DisplayName("Greedy Scheduler")
	public void scheduleMaker1() {
		List<String> courses = this.courses.parallelStream().filter(e -> this.courses.indexOf(e) < counter).collect(Collectors.toList());
		GreedyQuickScheduleMaker.build(courses, "202001");
	}

	@Time(repeat = 82)
	@DisplayName("Brute-Force Scheduler")
	public void scheduleMaker2() {
		Set<String> courses = this.courses.parallelStream().filter(e -> this.courses.indexOf(e) < counter).collect(Collectors.toSet());
		BruteForceScheduleMaker.build(courses, "202001");
	}
}
