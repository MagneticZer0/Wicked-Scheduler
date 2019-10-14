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

public class Class implements Serializable {

	private static final long serialVersionUID = -8811383389305817678L; // This is for serializable object compatability

	private int crn;
	private String subject;
	private int courseNumber;
	private double[] credits;
	private String title;
	private ArrayList<String> days;
	private ArrayList<LocalTime> startTime;
	private ArrayList<LocalTime> endTime;
	private int remaining;
	private String instructor;
	private Date startDate;
	private Date endDate;
	private int fee;

	// Date must be in format M1/D1-M2/D2|YEAR
	// Time must be in format h1:m1 a/pm1-h2:m2 a/pm2
	public Class(String CRN, String subject, String courseNumber, double[] credits, String title, String days, String time, String remaining, String instructor, String date, int fee) throws ParseException {
		this.days = new ArrayList<>();
		this.startTime = new ArrayList<>();
		this.endTime = new ArrayList<>();

		this.crn = Integer.parseInt(CRN);
		this.subject = subject;
		this.courseNumber = Integer.parseInt(courseNumber);
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
		ArrayList<String> days = new ArrayList<>(6);
		if (!this.days.contains("TBA") || !this.days.contains(" ")) {
			if (this.days.contains("M")) {
				days.add("Monday");
			}
			if (this.days.contains("T")) {
				days.add("Tuesday");
			}
			if (this.days.contains("W")) {
				days.add("Wednesday");
			}
			if (this.days.contains("R")) {
				days.add("Thursday");
			}
			if (this.days.contains("F")) {
				days.add("Friday");
			}
		}
		return Collections.unmodifiableList(days); // Create an immutable list
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

	public int getFee() {
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
		return subject + courseNumber + " - " + title;
	}

}
