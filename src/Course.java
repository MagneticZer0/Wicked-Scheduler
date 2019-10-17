import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class Course implements Serializable, Comparable<Course>, Iterable<List<LocalTime[]>> {

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
	 * An array that corresponds to the days the course is on. This directly
	 * correlates with the start/endTime lists.
	 */
	private ArrayList<String> days;
	/**
	 * An array that corresponds to the time the course starts. This directly
	 * coorelates with the day/endTime lists.
	 */
	private ArrayList<LocalTime> startTime;
	/**
	 * An array that corresponds to the time the course ends. This directly
	 * coorelates with the day/startTime lists.
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
	/**
	 * If the course in question is a lab, used for the toString
	 */
	private boolean isLab;
	/**
	 * This is the minimum amount of time between classes before they're considered
	 * to be overlapping I. E. This is a total of 5 minutes between classes
	 */
	private final TemporalAmount travelTime = Duration.ofMinutes(5).dividedBy(2);

	// Date must be in format M1/D1-M2/D2|YEAR
	// Time must be in format h1:m1 a/pm1-h2:m2 a/pm2
	public Course(String CRN, String subject, String courseCode, boolean isLab, List<Double> credits, String title, String days, String time, String remaining, String instructor, String date, double fee) throws ParseException {
		this.days = new ArrayList<>();
		this.startTime = new ArrayList<>();
		this.endTime = new ArrayList<>();

		this.crn = Integer.parseInt(CRN);
		this.subject = subject;
		this.courseCode = Integer.parseInt(courseCode);
		this.credits = new double[credits.size()];
		for (int i = 0; i < credits.size(); i++) {
			this.credits[i] = credits.get(i);
		}

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
		this.isLab = isLab;
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
			for (String day : result) {
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
	 * Some classes happen at different times on different days Or maybe they have
	 * multiple class sessions in the same day, this is for adding the additional
	 * times for those classes.
	 * 
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

	public List<LocalTime[]> getTimes(String day) {
		// returns the times that the class happens on a certain day or if doesn't it
		// returns 1:37 AM
		ArrayList<LocalTime[]> result = new ArrayList<>();
		for (int i = 0; i < days.size(); i++) {
			if (days.get(i).contains(day)) {
				result.add(new LocalTime[] { startTime.get(i), endTime.get(i) });
			}
		}
		return Collections.unmodifiableList(result);
	}

	public boolean conficts(Course other) {
		if (datesConflict(this.startDate, this.endDate, other.startDate, other.endDate)) {
			for (Course.CourseTimeIterator thisTimes = (Course.CourseTimeIterator) this.iterator(); thisTimes.hasNext();) {
				for (LocalTime[] thisTime : thisTimes.next()) {
					for (LocalTime[] otherTime : other.getTimes(thisTimes.getDay())) {
						if (timesConflict(thisTime[0], thisTime[1], otherTime[0], otherTime[1])) {
							return true;
						}
					}
				}
			}
			return false;
		} else {
			return false; // Can't conflict if classes don't happen during same dates
		}
	}

	/**
	 * I could have made this into 1 big if statement, but I think that this is
	 * better as it's easier to read
	 * 
	 * @param startDate1 The first start date
	 * @param endDate1   The first end date
	 * @param startDate2 The second start date
	 * @param endDate2   The second end date
	 * @return Return true if the classes overlap in any way date wise.
	 */
	private boolean datesConflict(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {
		if (startDate1.after(startDate2) && startDate1.before(endDate2)) { // First class starts in middle of second class
			return true;
		} else if (endDate1.after(startDate2) && endDate1.before(endDate2)) { // Second class starts in middle of first class
			return true;
		} else if (endDate1.equals(startDate2)) { // First class ends when second class starts
			return true;
		} else if (startDate1.equals(endDate2)) { // First class starts when second class ends
			return true;
		} else if (startDate1.before(startDate2) && endDate1.after(endDate2)) { // First class starts before and ends after the second class
			return true;
		} else if (startDate1.after(startDate2) && endDate1.before(endDate2)) { // First class starts after and ends before the second class
			return true;
		} else if (startDate1.equals(startDate2) && endDate1.equals(endDate2)) { // First class starts and ends at the same date as the second class
			return true;
		} else {
			return false;
		}
	}

	/**
	 * I could have made this into 1 big if statement, but I think that this is
	 * better as it's easier to read
	 * 
	 * @param startDate1 The first start time
	 * @param endDate1   The first end time
	 * @param startDate2 The second start time
	 * @param endDate2   The second end time
	 * @return Return true if the classes overlap in any way time wise.
	 */
	private boolean timesConflict(LocalTime startTime1, LocalTime endTime1, LocalTime startTime2, LocalTime endTime2) {
		LocalTime tbaTime = parseTime("1:37 am");
		if (startTime1.equals(tbaTime) || endTime1.equals(tbaTime) || startTime2.equals(tbaTime) || endTime2.equals(tbaTime)) { // Is the time TBA? Just assume it doesn't conflict
			return false;
		} else if (startTime1.isAfter(startTime2) && startTime1.isBefore(endTime2)) { // First class starts in middle of second class
			return true;
		} else if (endTime1.isAfter(startTime2) && endTime1.isBefore(endTime2)) { // Second class starts in middle of first class
			return true;
		} else if (endTime1.equals(startTime2)) { // First class ends when second class starts
			return true;
		} else if (startTime1.equals(endTime2)) { // First class starts when second class ends
			return true;
		} else if (startTime1.isBefore(startTime2) && endTime1.isAfter(endTime2)) { // First class starts before and ends after the second class
			return true;
		} else if (startTime1.isAfter(startTime2) && endTime1.isBefore(endTime2)) { // First class starts after and ends before the second class
			return true;
		} else if (startTime1.equals(startTime2) && endTime1.equals(endTime2)) { // First class starts and ends at the same time as the second class
			return true;
		} else {
			return false;
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

	/**
	 * Returns a string representation for a course object. This will be the
	 * department followed by the course number followed by the course name, I.E.
	 * CS3141 - Team Software Project, and if the class is a lab it will have "Lab"
	 * appended to the end of it.
	 * 
	 * @return Returns the string representation of the class
	 */
	@Override
	public String toString() {
		return subject + courseCode + " - " + title + (isLab ? " Lab" : ""); // Appends lab to the end if the course is a lab
	}

	@Override
	public int compareTo(Course other) {
		return Scraper.courses.get(this.toString()).size() - Scraper.courses.get(other.toString()).size();
	}

	@Override
	public Iterator<List<LocalTime[]>> iterator() {
		return new CourseTimeIterator();
	}

	public class CourseTimeIterator implements Iterator<List<LocalTime[]>> {

		private ArrayList<String> days = new ArrayList<>(Arrays.asList("F", "R", "W", "T", "M"));
		private String day = "M";

		@Override
		public boolean hasNext() {
			return !days.isEmpty();
		}

		@Override
		public List<LocalTime[]> next() {
			if (hasNext()) {
				List<LocalTime[]> result = getTimes(days.get(days.size() - 1));
				day = days.remove(days.size() - 1);
				return result;
			} else {
				throw new NoSuchElementException();
			}
		}

		public String getDay() {
			return day;
		}
	}
}
