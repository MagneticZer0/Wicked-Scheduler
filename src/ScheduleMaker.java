import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

import collections.MultiMap;

import collections.MultiMap;

public class ScheduleMaker {
    static ArrayList<Course> currentCourse = new ArrayList<>();
    private static MultiMap<String, Course> allCourses;
	 
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
    		if( allCourses == null ) {
    			allCourses = Scraper.getAllClasses(semesterID);
    			//Collections.sort(allCourses);
    		}    		
    		currentCourse.addAll(allCourses.get(courseName));
    		System.out.println("Add course " + allCourses.get(courseName));
    	} catch ( IOException ex ) {
    		System.out.println("Error PaseException, IOException");
    		System.exit(0);
    	}
    }
    
    public static void main(String[] args) {
    	ArrayList<String> courses = new ArrayList<>(); // Store the courses from the GUI
    	
    	// Create the arraylist of selected courses
    	//courses = getCC();
    	
    	// Testing
    	
    	courses.add("EE3131 - Electronics");
    	courses.add("CS3411 - Systems Programming");
    	
    	for(int j = 0; j < courses.size(); j++) {
    		try {
    			//System.out.println(Scraper.getAllSemesters().toString());
    			findCC( courses.get(j), Scraper.getAllSemesters().get("Fall 2019"));
    		} catch (IOException ex) {
    			
    		}
    	}
    	
    	Collections.sort(currentCourse);
    	
    	
    	// Compare each element in list for a conflict
    	for(int i = 0; i < currentCourse.size(); i++) {
    		for(int j = 0; j < currentCourse.size(); j++) {
    			if(i == j) {
    				// Skip
    				continue;
    			}
    			if(currentCourse.get(i).conflicts(currentCourse.get(j))) {
    				// Error, classes conflict notify user
    				System.out.println("Conflict Occured " + currentCourse.get(i) + " " + currentCourse.get(j));
    				return;
    			}
    		}
    	}
    	
    	System.out.println(currentCourse.size());
    	for(int i = 0; i < currentCourse.size(); i++) {
    		System.out.println(currentCourse.get(i));
    	}
    	
    	// Build schedule to GUI?   	
    	
    }
}
