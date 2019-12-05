package logic;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import collections.BiPredicateMultiMap;
import collections.MultiMap;

/**
 * This class is used to store course information retrieved from the Scraper so
 * that it can later be manipulated and scheduled
 */
public class Course implements Serializable, Comparable<Course>, Iterable<List<LocalTime[]>> {

	/**
	 * This is used for Serializable compatibility
	 */
	private static final long serialVersionUID = 8126578523819110122L;
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
	private String courseCode;
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
	 * correlates with the day/endTime lists.
	 */
	private ArrayList<LocalTime> startTime;
	/**
	 * An array that corresponds to the time the course ends. This directly
	 * correlates with the day/startTime lists.
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
	private LocalDate startDate;
	/**
	 * The date that the course ends on
	 */
	private LocalDate endDate;
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
	private final TemporalAmount travelTime = Duration.ofMinutes(5);
	/**
	 * Represents the LocalTime object to be used if the class time cannot be parsed
	 * or is TBA
	 */
	public static final LocalTime TBA_TIME = parseTime("1:37 am");
	/**
	 * Represents the Date object to be used if the class date cannot be parsed or
	 * is TBA
	 */
	public static final LocalDate TBA_DATE = LocalDate.MIN;

	/**
	 * Creates a course based on the information provided. Some strings are expected
	 * to be in a certain format, the format will be given
	 * 
	 * @param CRN        The CRN for the course, this is used for signing up for the
	 *                   class
	 * @param subject    The department the class is in
	 * @param courseCode The class number, I.E. 3141 for the course
	 * @param isLab      Is this course a lab course?
	 * @param credits    The number of credits the class is worth
	 * @param title      The name of the class I.E. "Team Software Project"
	 * @param days       The days that the class is held
	 * @param time       The times that the class is held. Must be in format h1:m1
	 *                   a/pm1-h2:m2 a/pm2
	 * @param remaining  The amount of spots remaining within the class
	 * @param instructor The instructor for the class
	 * @param date       The dates that the class is held. Must be in format
	 *                   M1/D1-M2/D2|YEAR
	 * @param fee        The fee to take the class
	 */
	public Course(String CRN, String subject, String courseCode, boolean isLab, List<Double> credits, String title, String days, String time, String remaining, String instructor, String date, double fee) {
		this.days = new ArrayList<>();
		this.startTime = new ArrayList<>();
		this.endTime = new ArrayList<>();

		this.crn = Integer.parseInt(CRN);
		this.subject = subject;
		this.courseCode = courseCode;
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
			startTime.add(TBA_TIME); // This will be the TBA time
			endTime.add(TBA_TIME);
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

	/**
	 * Returns the CRN, as an int
	 * 
	 * @return The CRN
	 */
	public int getCRN() {
		return crn;
	}

	/**
	 * Returns the subject string representation
	 * 
	 * @return The subject, i.e. "MA", "CS", etc.
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Returns the course code
	 * 
	 * @return MA1161 -> 1161
	 */
	public String getCourseCode() {
		return courseCode;
	}

	/**
	 * Returns the amount of credits the class is worth.
	 * 
	 * @return Most times this array is only of size 1, sometimes it is of size 2
	 *         for variable credit classes.
	 */
	public double[] getCredits() {
		return credits;
	}

	/**
	 * Gets the days that the class is held on in full string representation.
	 * 
	 * @return The days the class is held in an unmodifiable list.
	 */
	public List<String> getDays() {
		ArrayList<String> result = new ArrayList<>(6);
		if (!this.days.contains("TBA") || !this.days.contains(" ")) {
			for (String day : days) {
				if (day.contains("M")) {
					result.add("MO");
				}
				if (day.contains("T")) {
					result.add("TU");
				}
				if (day.contains("W")) {
					result.add("WE");
				}
				if (day.contains("R")) {
					result.add("TH");
				}
				if (day.contains("F")) {
					result.add("FR");
				}
			}
		}
		return Collections.unmodifiableList(result.stream().distinct().collect(Collectors.toList())); // Create an immutable list that has duplicates removed
	}

	/**
	 * Returns the first day of the week the class takes place in a DayOfWeek enum
	 * 
	 * @return The DayOfWeek enum for the first day
	 */
	public DayOfWeek firstDay() {
		List<String> days = getDays();
		String day = days.get(0);
		if (day.equals("MO")) {
			return DayOfWeek.MONDAY;
		} else if (day.equals("TU")) {
			return DayOfWeek.TUESDAY;
		} else if (day.equals("WE")) {
			return DayOfWeek.WEDNESDAY;
		} else if (day.equals("TH")) {
			return DayOfWeek.THURSDAY;
		} else {
			return DayOfWeek.FRIDAY;
		}
	}

	/**
	 * Returns the start time corresponding the the i'th index
	 * 
	 * @param i The index
	 * @return Returns a LocalTime object for that class start time
	 */
	public LocalTime getStartTime(int i) {
		return startTime.get(i);
	}

	/**
	 * Returns the end time corresponding the the i'th index
	 * 
	 * @param i The index
	 * @return Returns a LocalTime object for that class end time
	 */
	public LocalTime getEndTime(int i) {
		return endTime.get(i);
	}

	/**
	 * Returns the amount of spots left in the class
	 * 
	 * @return An int of the amount of spots
	 */
	public int getRemaining() {
		return remaining;
	}

	/**
	 * Returns the instructor for the course
	 * 
	 * @return The instructor, as a String
	 */
	public String getInstructor() {
		return instructor;
	}

	/**
	 * This is the date that the class starts on, most (if not all, I'm not sure)
	 * start on the first day of the semester
	 * 
	 * @return A date representing the start date of class
	 */
	public LocalDate getStartDate() {
		return startDate;
	}

	/**
	 * This is the date that the class ends, classes can be half or full semesters
	 * (and maybe more, not sure).
	 * 
	 * @return A date representing the end date of class
	 */
	public LocalDate getEndDate() {
		return endDate;
	}

	/**
	 * The fee that it costs to take this class, typically applies to lab courses
	 * 
	 * @return The fee as an int
	 */
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
			startTime.add(TBA_TIME);
			endTime.add(TBA_TIME);
		}
	}

	/**
	 * Returns the times that the class takes place on that day (start and end).
	 * 
	 * @param day The day, this is the single character string representation
	 * @return A list of LocalTime[]'s in which arr[0] = start time and arr[1] = end
	 *         time
	 */
	public List<LocalTime[]> getTimes(String day) {
		ArrayList<LocalTime[]> result = new ArrayList<>();
		for (int i = 0; i < days.size(); i++) {
			if (days.get(i).contains(day)) {
				result.add(new LocalTime[] { startTime.get(i), endTime.get(i) });
			}
		}
		return Collections.unmodifiableList(result);
	}

	/**
	 * The method that front facing users call to test if courses conflict. This
	 * method is to make sure that x.conflicts(y) is equal to y.conflicts(x) because
	 * of the implementation of the conflicts method is is not reflexive.
	 * 
	 * @param other The other course to check conflicts with
	 * @return Returns a boolean based on if a course conflicts with the other
	 */
	public boolean conflicts(Course other) {
		return this.conflictsHelper(other) || other.conflictsHelper(this);
	}

	/**
	 * This tests to see if two courses conflict with each other. A conflict is
	 * defined as a course and other that has overlapping dates, overlapping days,
	 * as well as overlapping times. A conflict also occurs if there is not at least
	 * 5 minutes in between classes, but this is configurable.
	 * 
	 * @param other The other course to check conflicts with
	 * @return Returns a boolean based on if a course conflicts with the other
	 */
	private boolean conflictsHelper(Course other) {
		if (datesConflict(this.startDate, this.endDate, other.startDate, other.endDate)) {
			for (Course.CourseTimeIterator thisTimes = (Course.CourseTimeIterator) this.iterator(); thisTimes.hasNext();) {
				for (LocalTime[] thisTime : thisTimes.next()) {
					for (LocalTime[] otherTime : other.getTimes(thisTimes.getDay())) {
						if (timesConflict(thisTime[0], thisTime[1], otherTime[0], otherTime[1]) || travelTimeConflict(thisTime[0], thisTime[1], otherTime[0], otherTime[1])) {
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
	private boolean datesConflict(LocalDate startDate1, LocalDate endDate1, LocalDate startDate2, LocalDate endDate2) {
		if (startDate1.isAfter(startDate2) && startDate1.isBefore(endDate2)) { // First class starts in middle of second class
			return true;
		} else if (endDate1.isAfter(startDate2) && endDate1.isBefore(endDate2)) { // Second class starts in middle of first class
			return true;
		} else if (endDate1.equals(startDate2)) { // First class ends when second class starts
			return true;
		} else if (startDate1.equals(endDate2)) { // First class starts when second class ends
			return true;
		} else if (startDate1.isBefore(startDate2) && endDate1.isAfter(endDate2)) { // First class starts before and ends after the second class
			return true;
		} else if (startDate1.isAfter(startDate2) && endDate1.isBefore(endDate2)) { // First class starts after and ends before the second class
			return true;
		} else { // First class starts and ends at the same date as the second class
			return startDate1.equals(startDate2) && endDate1.equals(endDate2);
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
		if (startTime1.equals(TBA_TIME) || endTime1.equals(TBA_TIME) || startTime2.equals(TBA_TIME) || endTime2.equals(TBA_TIME)) { // Is the time TBA? Just assume it doesn't conflict
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
		} else { // First class starts and ends at the same time as the second class
			return startTime1.equals(startTime2) && endTime1.equals(endTime2);
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
	 * @return Return true if the classes overlap after accounting for travel time.
	 */
	private boolean travelTimeConflict(LocalTime startTime1, LocalTime endTime1, LocalTime startTime2, LocalTime endTime2) {
		return timesConflict(startTime1.plus(travelTime), endTime1.plus(travelTime), startTime2, endTime2) || timesConflict(startTime1, endTime1, startTime2.minus(travelTime), endTime2.minus(travelTime));
	}

	/**
	 * 
	 * @param date Must be in format MM/dd/yyyy
	 * @return Returns a LocalDate object representing the date the class takes
	 *         place. If an exception occurs parsing the date it will instead return
	 *         1/1/1970 00:00:00
	 */
	private LocalDate parseDate(String date) {
		return LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
	}

	/**
	 * 
	 * @param time Must be in format HH:mm am/pm
	 * @return Returns a LocalTime object that signifies that time to the class
	 *         takes place
	 */
	private static LocalTime parseTime(String time) {
		DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("h:mm a").toFormatter(Locale.ENGLISH);
		return LocalTime.parse(time, formatter);
	}

	/**
	 * If the class takes place at different times in the same week
	 * 
	 * @return A boolean if the class doesn't always happen at the same time
	 */
	public boolean isSplitClass() {
		return days.size() > 1;
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

	/**
	 * Compares a class based on the amount of sections that are available for the
	 * class in the Scraper, I.E. the Scraper.getAllCourses must be run first!
	 */
	@Override
	public int compareTo(Course other) {
		return Scraper.getLast().get(this.toString()).size() - Scraper.getLast().get(other.toString()).size();
	}

	/**
	 * Returns an iterator that allows you to iterate through the times that the
	 * class takes place on that day starting from Monday and going through Friday
	 */
	@Override
	public Iterator<List<LocalTime[]>> iterator() {
		return new CourseTimeIterator();
	}

	/**
	 * This iterator goes through each day in the week and will return to you the
	 * times that the class takes place on that day.
	 * 
	 * @author MagneticZero
	 *
	 */
	public class CourseTimeIterator implements Iterator<List<LocalTime[]>> {

		/**
		 * The days the iterator has to go through still, in reverse order to save
		 * computation power of removing an element from an array list
		 */
		private ArrayList<String> days = new ArrayList<>(Arrays.asList("FIL", "F", "R", "W", "T", "M"));
		/**
		 * The current day the iterator is on
		 */
		private String day = "M";

		/**
		 * Returns a boolean based on if there are still days to iterate through
		 * 
		 * @return Is there any days left to iterate through?
		 */
		@Override
		public boolean hasNext() {
			return !days.isEmpty() || day.equals("F");
		}

		/**
		 * Returns the times that the class is held on the next element's day
		 * 
		 * @return
		 */
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

		/**
		 * Returns the current day that the iterator is on
		 * 
		 * @return The day the iterator is on
		 */
		public String getDay() {
			return day;
		}

		/**
		 * Returns the current day in RRule format that the iterator is on
		 * 
		 * @return The day the iterator is on
		 */
		public String getRRuleDay() {
			if (day.equals("M")) {
				return "MO";
			} else if (day.equals("T")) {
				return "TU";
			} else if (day.equals("W")) {
				return "WE";
			} else if (day.equals("R")) {
				return "TH";
			} else {
				return "FR";
			}
		}

		/**
		 * Gets the current day the iterator is on as a DayOfWeek enum
		 * 
		 * @return The DayOfWeek enum representing the day the iterator is on
		 */
		public DayOfWeek getDayEnum() {
			if (day.equals("M")) {
				return DayOfWeek.MONDAY;
			} else if (day.equals("T")) {
				return DayOfWeek.TUESDAY;
			} else if (day.equals("W")) {
				return DayOfWeek.WEDNESDAY;
			} else if (day.equals("R")) {
				return DayOfWeek.THURSDAY;
			} else {
				return DayOfWeek.FRIDAY;
			}
		}
	}

	/**
	 * Returns a BiPredicateMultiMap that maps an array of courses to the other
	 * courses in conflicts with
	 * 
	 * @param <T>     The Course or subclass of Course type
	 * @param courses The array of courses
	 * @return A BiPredicateMultiMap representing the conflicts
	 */
	public static <T extends Course> MultiMap<Course, Course> getConflicts(T[] courses) {
		BiPredicateMultiMap<Course> map = new BiPredicateMultiMap<>((x, y) -> x.conflicts(y));
		for (Course course : courses) {
			map.put(course);
		}
		return map;
	}

	/**
	 * Returns a BiPredicateMultiMap that maps a collection of courses to the other
	 * courses in conflicts with
	 * 
	 * @param <T>     The Course or subclass of Course type
	 * @param courses The collection of courses
	 * @return A BiPredicateMultiMap representing the conflicts
	 */
	public static <T extends Course> MultiMap<Course, Course> getConflicts(Collection<T> courses) {
		return getConflicts((T[]) courses.toArray(new Object[courses.size()]));
	}
}
