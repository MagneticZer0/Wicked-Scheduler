import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Course implements Serializable, Comparable<Course> {

	/**
	 * This is for serializable object compatability
	 */
	private static final long serialVersionUID = -8811383389305817678L; // This is for serializable object compatability

	/**
	 * The int used for tracking the crn, this is used to sign up for the class
	 */
	private int crn;
	/**
	 * The string used to track what department the class is part of
	 */
	private String subject;
	/**
	 * This is the "level" of the course, like 3141
	 */
	private int courseCode;
	/**
	 * Since some courses have a variable amount of credits this will store that
	 */
	private double[] credits;
	/**
	 * The name of the course like "Team Software Project"
	 */
	private String title;
	/**
	 * An array that corresponds to the days the course is on.
	 * This directly correlates with the start/endTime lists.
	 */
	private ArrayList<String> days;
	/**
	 * An array that corresponds to the time the course starts.
	 * This directly coorelates with the day/endTime lists.
	 */
	private ArrayList<LocalTime> startTime;
	/**
	 * An array that corresponds to the time the course ends.
	 * This directly coorelates with the day/startTime lists.
	 */
	private ArrayList<LocalTime> endTime;
	/**
	 * The remaining spots that the class has
	 */
	private int remaining;
	/**
	 * The instructor that teaches this course
	 */
	private String instructor;
	/**
	 * The date that the course starts on
	 */
	private Date startDate;
	/**
	 * The date that the course ends on
	 */
	private Date endDate;
	/**
	 * The fee of the course
	 */
	private double fee;

	// Date must be in format M1/D1-M2/D2|YEAR
	// Time must be in format h1:m1 a/pm1-h2:m2 a/pm2
	public Course(String CRN, String subject, String courseCode, double[] credits, String title, String days, String time, String remaining, String instructor, String date, double fee) throws ParseException {
		this.days = new ArrayList<>();
		this.startTime = new ArrayList<>();
		this.endTime = new ArrayList<>();

		this.crn = Integer.parseInt(CRN);
		this.subject = subject;
		this.courseCode = Integer.parseInt(courseCode);
		this.credits = credits;
		this.title = title;
		this.days.add(days);

		String[] timeSplit = time.split("-");
		if (!time.equals("TBA")) {
			startTime.add(parseTime(timeSplit[0]));
			endTime.add(parseTime(timeSplit[1]));
		} else {
			startTime.add(parseTime("1:37 am")); // This will be the TBA time
			endTime.add(parseTime("1:37 am"));
		}

		this.remaining = Integer.parseInt(remaining);
		this.instructor = instructor;

		String[] dateSplit1 = date.split("-");
		String[] dateSpllt2 = date.split("\\|");
		startDate = parseDate(dateSplit1[0] + "/" + dateSpllt2[1]);
		endDate = parseDate(dateSplit1[1].split("\\|")[0] + "/" + dateSpllt2[1]);

		this.fee = fee;
	}

	public int getCRN() {
		return crn;
	}

	public double[] getCredits() {
		return credits;
	}

	public List<String> getDays() {
		ArrayList<String> result = new ArrayList<>(6);
		if (!this.days.contains("TBA") || !this.days.contains(" ")) {
			for(String day : result) {
				if (day.contains("M")) {
					result.add("Monday");
				}
				if (day.contains("T")) {
					result.add("Tuesday");
				}
				if (day.contains("W")) {
					result.add("Wednesday");
				}
				if (day.contains("R")) {
					result.add("Thursday");
				}
				if (day.contains("F")) {
					result.add("Friday");
				}
			}
		}
		return Collections.unmodifiableList(result.stream().distinct().collect(Collectors.toList())); // Create an immutable list that has duplicates removed
	}

	public LocalTime getStartTime(int i) {
		return startTime.get(i);
	}

	public LocalTime getEndTime(int i) {
		return endTime.get(i);
	}

	public int getRemaining() {
		return remaining;
	}

	public String getInstructor() {
		return instructor;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public double getFee() {
		return fee;
	}

	/**
	 * Some classes happen at different times on different days
	 * Or maybe they have multiple class sessions in the same day,
	 * this is for adding the additional times for those classes.
	 * @param dayAndTimes Must be in format DAYS(MTWRF)|h1:m1 a/pm1-h2:m2 a/pm2
	 */
	public void addDayandTime(String dayAndTimes) {
		String[] data = dayAndTimes.split("\\|");
		days.add(data[0]);

		String[] timeSplit = data[1].split("-");
		if (!data[1].equals("TBA")) {
			startTime.add(parseTime(timeSplit[0]));
			endTime.add(parseTime(timeSplit[1]));
		} else {
			startTime.add(parseTime("1:37 am"));
			endTime.add(parseTime("1:37 am"));
		}
	}

	/**
	 * 
	 * @param date Must be in format MM/dd/yyyy
	 * @return
	 * @throws ParseException 
	 */
	private Date parseDate(String date) throws ParseException {
		return new SimpleDateFormat("MM/dd/yyyy").parse(date);
	}

	/**
	 * 
	 * @param time Must be in format HH:mm am/pm
	 * @return
	 */
	private LocalTime parseTime(String time) {
		DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("h:mm a").toFormatter(Locale.ENGLISH);
		return LocalTime.parse(time, formatter);
	}

	@Override
	public String toString() {
		return subject + courseCode + " - " + title;
	}

	@Override
	public int compareTo(Course otherCourse) {
		// TODO Auto-generated method stub
		return 0;
	}
}
