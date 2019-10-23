import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import collections.MultiMap;

import collections.MultiMap;

public class ScheduleMaker {
    static ArrayList<Course> currentCourse = new ArrayList<>();
	 
    /**
     * Get Course code from GUI
     * @return An ArrayList of the courses from the GUI
     */
	private static ArrayList<String> getCC() {
		ArrayList<String> courses = new ArrayList<>();
    	return courses;
    }
    
    /**
     * This method will retrieve the Course from the given name and semester and add it to the 
     * global ArrayList
     * @author Alex Hare
     * @param courseName - The name of the course to be searched for
     * @param semesterID - Semester to search in
     */
    public static void findCC( String courseName, String semesterID ) {
    	try {
    		MultiMap<String, Course> allCourses = Scraper.getAllClasses(semesterID);
    		currentCourse.addAll(allCourses.get(courseName));
    		System.out.println("Add course " + allCourses.get(courseName));
    	} catch ( ParseException | IOException ex ) {
    		System.out.println("Error PaseException, IOException");
    		System.exit(0);
    	}
    }
    
    public static void main(String[] args) {
    	ArrayList<String> courses = new ArrayList<>(); // Store the courses from the GUI
    	// Create the arraylist of selected courses
    	//courses = getCC();
    	
    	// Testing
    	courses.add("EE 3131");
    	courses.add("EE 2174");
    	
    	for(int j = 0; j < courses.size(); j++) {
    		try {
    			findCC( courses.get(j), Scraper.getAllSemesters().get("Spring 2019 (View only)"));
    		} catch (IOException ex) {
    			
    		}
    	}
    	
    	// Compare each element in list for a conflict
    	for(int i = 0; i < currentCourse.size(); i++) {
    		for(int j = 0; j < currentCourse.size(); j++) {
    			if(currentCourse.get(i).conflicts(currentCourse.get(j))) {
    				// Error, classes conflict notify user
    				System.out.println("Conflict Occured");
    				return;
    			}
    		}
    	}
    	
    	System.out.println(currentCourse.size());
    	//System.out.println(currentCourse.get(0).getCRN());
    	//System.out.println(currentCourse.get(1).getCRN());
    	
    	// Build schedule to GUI?   	
    	
    }
}
