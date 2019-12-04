package headedTests;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jtimer.Runner;
import org.jtimer.Annotations.BeforeClass;
import org.jtimer.Annotations.Time;

import logic.GreedyQuickScheduleMaker;

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
	}

	@Time(repeat = 25)
	public void scheduleMaker() {
		courses = courses.parallelStream().filter(e -> courses.indexOf(e) < counter).collect(Collectors.toList());
		GreedyQuickScheduleMaker.build(courses, "202001");
	}
}
