import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Class implements Serializable {

	private static final long serialVersionUID = -8811383389305817678L; // This is for serializable object compatability

	private int crn;
	private String subject;
	private int courseNumber;
	private double[] credits;
	private String title;
	private String days;
	private LocalTime startTime;
	private LocalTime endTime;
	private int remaining;
	private String instructor;
	private Date startDate;
	private Date endDate;
	private int fee;

	// Date must be in format M1/D1-M2/D2|YEAR
	// Time must be in format h1:m1 a/pm1-h2:m2 a/pm2
	public Class(int CRN, String subject, int courseNumber, double[] credits, String title, String days, String time, int remaining, String instructor, String date, int fee) throws ParseException {
		this.crn = CRN;
		this.subject = subject;
		this.courseNumber = courseNumber;
		this.credits = credits;
		this.title = title;
		this.days = days;

		String[] timeSplit = time.split("-");
		startTime = parseTime(timeSplit[0]);
		endTime = parseTime(timeSplit[1]);

		this.remaining = remaining;
		this.instructor = instructor;

		String[] dateSplit1 = date.split("-");
		String[] dateSpllt2 = date.split("|");
		startDate = parseDate(dateSplit1[0] + "/" + dateSpllt2[1]);
		endDate = parseDate(dateSplit1[1].split("|")[0] + "/" + dateSpllt2[1]);

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
		if (this.days != "TBA" || this.days != " ") {
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

	public LocalTime getStartTime() {
		return startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
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
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
		return LocalTime.parse(time, formatter);
	}

	@Override
	public String toString() {
		return subject + courseNumber + " - " + title;
	}

}
