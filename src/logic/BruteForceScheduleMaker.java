package logic;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import collections.MultiMap;

/**
 * This scheduling algorithm is a smart type of brute-force algorithm that will
 * generate all possible schedules based on the courses given.
 */
public final class BruteForceScheduleMaker {

	/**
	 * Since the class is static for all intents and purposes there is no need to
	 * allow instantiation
	 */
	private BruteForceScheduleMaker() {
		throw new UnsupportedOperationException("This is a static class and cannot be instantiated!");
	}

	/**
	 * This method will retrieve the Course from the given name and semester and add
	 * it to the global ArrayList
	 * 
	 * @param courseName - The name of the course to be searched for
	 * @param semesterID - Semester to search in
	 */
	public static Set<Course> findCourses(String courseName, String semesterID) {
		Set<Course> possibleCourses = new HashSet<>(); // Holds all courses at various times
		try {
			// add all the inputed course to possibleCourses
			possibleCourses.addAll(Scraper.getAllClasses(semesterID).get(courseName));

		} catch (IOException e) {
			Globals.popupException().writeError(e);
		}

		return possibleCourses;
	}

	/**
	 * Returns a set of sets of courses that are valid possible schedules
	 * 
	 * @param desiredCourses The courses that the user desires
	 * @param semesterID     The semester ID to make a schedule of
	 * @return Returns all possible sets of schedules
	 */
	public static Set<Set<Course>> build(Set<String> desiredCourses, String semesterID) {
		/*
		 * a list of schedules ( list of lists )
		 * 
		 * for each desired course: for each section in the desired course: for each
		 * schedule in the list: for each course in the schedule: if there is a
		 * conflict, remove the schedule from the list of schedules, and break twice add
		 * the section to the current schedule
		 * 
		 */

		Set<Course> possibleCourses = new HashSet<>();
		Set<Set<Course>> validSchedules = new HashSet<>(); // contains a list of schedules ( each "schedule" is a list of courses that do not conflict )
		Set<Course> tempSchedule = new HashSet<>();
		Set<Set<Course>> toAdd;
		Set<Set<Course>> toRemove;
		boolean conflict;
		int courseAdditionCount = 0;

		for (String courseCode : desiredCourses) {
			possibleCourses = findCourses(courseCode, semesterID);

			// if there are no pre-existing schedules
			if (validSchedules.isEmpty()) {

				for (Course possibleCourse : possibleCourses) {
					tempSchedule = new HashSet<>();
					tempSchedule.add(possibleCourse);
					validSchedules.add(tempSchedule);
				}
			} else {

				toAdd = new HashSet<>();
				toRemove = new HashSet<>();
				for (Course possibleCourse : possibleCourses) {
					for (Set<Course> schedule : validSchedules) {
						if (schedule.size() != courseAdditionCount) {
							toRemove.add(schedule);
						}

						tempSchedule = new HashSet<>();
						conflict = false;
						for (Course existingCourse : schedule) {
							if (possibleCourse.conflicts(existingCourse)) {
								conflict = true;
								break;
							}
						}

						if (conflict) {
							continue;
						}

						tempSchedule.addAll(schedule);
						tempSchedule.add(possibleCourse);
						toAdd.add(tempSchedule);
					}
				}

				validSchedules.removeAll(toRemove);
				validSchedules.addAll(toAdd);
			}
			courseAdditionCount++;
		}

		Set<Set<Course>> finalSchedules = new HashSet<>();

		for (Set<Course> schedule : validSchedules) {
			if (schedule.size() == desiredCourses.size()) {
				finalSchedules.add(schedule);
			}

		}
		return finalSchedules;
	}
}
