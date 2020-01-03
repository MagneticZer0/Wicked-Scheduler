
package logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This scheduling algorithm is a greedy algorithm that will greedily produce
 * schedules. Due to the nature of this algorithm it can produce schedules that
 * don't have all classes, but it will still return them.
 */
public class GreedyQuickScheduleMaker {

	/**
	 * Stores all course object that the wants
	 */
	private static ArrayList<Course> currentCourse = new ArrayList<>();

	/**
	 * Since the class is static for all intents and purposes there is no need to
	 * allow instantiation
	 */
	private GreedyQuickScheduleMaker() {
		throw new UnsupportedOperationException("This is a static class and cannot be instantiated!");
	}

	/**
	 * This method will retrieve the course from the given name and semester and add
	 * it to the global ArrayList
	 * 
	 * @param courseName - The name of the course to be searched for
	 * @param semesterID - Semester to search in
	 */
	public static void findCC(String courseName, String semesterID) {
		try {
			currentCourse.addAll(Scraper.getAllClasses(semesterID).get(courseName));
		} catch (IOException e) {
			Globals.popupException().writeError(e);
		}
	}

	/**
	 * Returns a list of lists of courses that are valid possible schedules
	 * 
	 * @param desiredCourses The courses that the user desires
	 * @param semesterID     The semester ID to make a schedule of
	 * @return Returns all possible sets of schedules
	 */
	public static ArrayList<ArrayList<Course>> build(List<String> desiredCourses, String semesterID) {
		currentCourse.clear();
		ArrayList<Course> firstCourseList = new ArrayList<>();
		ArrayList<Course> secondCourseList = new ArrayList<>();
		ArrayList<ArrayList<Course>> out = new ArrayList<>();
		ArrayList<Course> copyList = new ArrayList<>();

		// Create the arraylist of selected courses
		for (int j = 0; j < desiredCourses.size(); j++) {
			findCC(desiredCourses.get(j), Scraper.getAllSemesters().get(semesterID));
		}

		Collections.sort(currentCourse);

		for (int i = 0; i < currentCourse.size(); i++) {
			copyList.add(currentCourse.get(i));
		}

		int numberOfCourses = 0;
		for (int i = 0; i < currentCourse.size(); i++) {
			if (i + 1 < currentCourse.size()) {
				if (!(currentCourse.get(i).toString().equals(currentCourse.get(i + 1).toString()))) {
					// If the next course doesn't equal the current course
					numberOfCourses++;
				}
			} else {
				numberOfCourses++;
			}
		}

		// This array will store the number of course repeats in order of sorted appearance
		// i.e. Systems has two offerings
		int[] arr = new int[numberOfCourses];
		int ind = 0;
		for (int i = 0; i < numberOfCourses; i++) {
			arr[i] = 1; // At least one

			// Check for repeats and increase count accordingly
			if (ind + 1 < currentCourse.size() && ind < currentCourse.size()) {
				while (currentCourse.get(ind).toString().equals(currentCourse.get(ind + 1).toString())) {
					arr[i]++;
					ind++;
					if (ind + 1 >= currentCourse.size()) {
						break;
					}
				}
			}
			ind++;
		}

		int courseIndex = 0;
		// Build schedule
		// Go through the number of courses to have
		for (int i = 0; i < numberOfCourses; i++) {
			// Go through the multiple times of that class
			for (int j = 0; j < arr[i]; j++) {
				// Skip if the class is already in the schedule
				boolean skip = false;
				boolean skipConflict = false;
				for (int k = 0; k < firstCourseList.size(); k++) {
					if (firstCourseList.get(k).toString().equals(currentCourse.get(courseIndex).toString())) {

						// Class exists skip
						skip = true;
					}
				}
				if (skip) {
					break;
				}

				// Add the first class
				if (i == 0) {
					firstCourseList.add(currentCourse.get(courseIndex));
					courseIndex++;
				} else if (arr[i] == 1) {
					for (int k = 0; k < firstCourseList.size(); k++) {
						if (firstCourseList.get(k).conflicts(currentCourse.get(courseIndex))) {
							skipConflict = true;
						}
					}
					if (skipConflict) {
						courseIndex++;
						continue;
					}
					firstCourseList.add(currentCourse.get(courseIndex));
					courseIndex++;
				} else {
					// Add other classes
					for (int k = 0; k < firstCourseList.size(); k++) {
						if (firstCourseList.get(k).conflicts(currentCourse.get(courseIndex))) {
							skipConflict = true;
						}
					}
					if (skipConflict) {
						courseIndex++;
						continue;
					}
					try {
						if (!firstCourseList.get(i - 1).conflicts(currentCourse.get(courseIndex))) {
							firstCourseList.add(currentCourse.get(courseIndex));
							courseIndex += arr[i];
							break;
						}
					} catch (IndexOutOfBoundsException e) {
						// If we go out of bounds we skip this course
						numberOfCourses--;
					}

				}
			}

		}

		out.add(firstCourseList);
		courseIndex = 0;

		// Make a second schedule if there are enough courses
		if (numberOfCourses < currentCourse.size()) {

			// Multiple courses, go through each course. Single courses first
			for (int i = 0; i < numberOfCourses; i++) {
				// Go through the multiple times of that course started from the latest courses
				for (int j = (arr[i] - 1); j >= 0; j--) {
					// Skip if the class is already in the schedule
					boolean skip = false;
					boolean skipConflict = false;
					for (int k = 0; k < secondCourseList.size(); k++) {
						if (secondCourseList.get(k).toString().equals(currentCourse.get(courseIndex).toString())) {
							// Class exists skip
							skip = true;
						}
					}
					if (skip) {
						break;
					}

					// Add the first class
					if (i == 0) {
						secondCourseList.add(currentCourse.get(courseIndex));
						courseIndex++;
					} else if (arr[i] == 1) {
						for (int k = 0; k < secondCourseList.size(); k++) {
							if (secondCourseList.get(k).conflicts(currentCourse.get(courseIndex))) {
								skipConflict = true;
							}
						}
						if (skipConflict) {
							courseIndex++;
							continue;
						}
						secondCourseList.add(currentCourse.get(courseIndex));
						courseIndex++;
					} else {
						// Add other classes
						for (int k = 0; k < secondCourseList.size(); k++) {
							if (secondCourseList.get(k).conflicts(currentCourse.get(courseIndex + j))) {
								skipConflict = true;
							}
						}
						if (skipConflict) {
							if (j == 0) {
								courseIndex += arr[i];
							}
							continue;
						}
						if (!secondCourseList.get(i - 1).conflicts(currentCourse.get(courseIndex + j))) {
							secondCourseList.add(currentCourse.get(courseIndex + j));
							courseIndex += arr[i];
							break;
						}
					}
				}
			}
			out.add(secondCourseList);
		}
		return out;
	}
}
