import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import collections.MultiMap;

public class ScheduleMaker {
	
    static ArrayList<Course> currentCourse = new ArrayList<>(); // Holds all courses at various times
    private static MultiMap<String, Course> allCourses;
    private static int numCourses;
	     
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
    			Scraper.loadCourses();
    			allCourses = Scraper.getAllClasses(semesterID);
    			//Collections.sort(allCourses);
    		}    		
    		currentCourse.addAll(allCourses.get(courseName));
    		numCourses++;
    		System.out.println("Add course " + allCourses.get(courseName));
    	} catch ( IOException ex ) {
    		System.out.println("Error PaseException, IOException");
    		System.exit(0);
    	}
    }
    
    public static ArrayList<ArrayList<Course>> build(ArrayList<String> courses, String Semester) {
    	ArrayList<Course> firstCourseList = new ArrayList<>();
    	ArrayList<Course> secondCourseList = new ArrayList<>();
    	ArrayList<ArrayList<Course>> out = new ArrayList<>();
    	
    	// Create the arraylist of selected courses
    	//courses = getCC();
    	
    	/** Testing
    	
    	courses.add("CS3411 - Systems Programming");
    	courses.add("EE3131 - Electronics");
    	courses.add("CS4321 - Introduction to Algorithms"); **/
    	
    	for(int j = 0; j < courses.size(); j++) {
			findCC( courses.get(j), Scraper.getAllSemesters().get(Semester));
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
    				currentCourse.remove(j);
    				continue;
    			}
    		}
    	}
    	
    	// This array will store the number of course repeats in order of sorted appearance
    	// i.e. Systems has two offerings 
    	int[] arr = new int[numCourses];
    	int ind = 0;
    	for(int i = 0; i < numCourses; i++) {
    		arr[i] = 1; // At least one

    		// Check for repeats and increase count accordingly
    		if( ind + 1 < currentCourse.size() && ind < currentCourse.size() ) {
    			while(currentCourse.get(ind).toString().equals(currentCourse.get(ind + 1).toString())) {
        			arr[i]++;
        			ind++;
        			if(ind < currentCourse.size()) {
        				break;
        			}
        		}    		
    		}    		
    		ind++;
    	}    	
    	
    	// Build schedule
    	// Go through the number of courses to have
    	for(int i = 0; i < numCourses; i++) {
    		// Go through the multiple times of that class
    		for(int j = 0; j < arr[i]; j++) {
    			// Skip if the class is already in the schedule
    			if(firstCourseList.contains(currentCourse.get(i + j))) {
    				// Class exists skip
    				break;    				
    			}
    			
    			// Add the first class
    			if(i == 0) {
    				firstCourseList.add(currentCourse.get(i+j));
    			} else if(arr[i] == 1){
    				firstCourseList.add(currentCourse.get(i));
    			}else {
    				// Add other classes
    				if(firstCourseList.get( i - 1 ).conflicts(currentCourse.get(i + j + arr[i - 1]))) {
    					// Conflict go to next option
    					if(j == arr[i] - 1) {
    						// If on the last option of a class and can't add it ERROR
    						System.out.println("Error incombatable course: " + currentCourse.get(i + j));
    					}
    					continue;
    				} else {
    					firstCourseList.add(currentCourse.get(i + j + arr[i - 1]));
    					break;
    				}
    			}
    		}
    	}
    	
    	out.add(firstCourseList);
    	
    	// Make a second schedule if there are enough courses
    	if( numCourses < currentCourse.size() ) {
    		// Multiple courses, go through each course. Single courses first
    		for(int i = 0; i < numCourses; i++) {
        		// Go through the multiple times of that course started from the latest courses
        		for(int j = (arr[i] - 1); j >= 0; j--) {
        			// Skip if the class is already in the schedule
        			if(firstCourseList.contains(currentCourse.get(i + j))) {
        				// Class exists skip
        				break;    				
        			}
        			
        			// Add the first class
        			if(i == 0) {
        				secondCourseList.add(currentCourse.get(i+j));
        			} else {
        				// Add other classes
        				
        				if(secondCourseList.get( i - 1 ).conflicts(currentCourse.get(i + j + arr[i - 1]))) {
        					// Conflict go to next option
        					if(j == arr[i] - 1) {
        						// If on the last option of a class and can't add it ERROR
        						System.out.println("Error incombatable course: " + currentCourse.get(i + j));
        					}
        					continue;
        				} else {        					
        					secondCourseList.add(currentCourse.get(i + j));
        					break;
        				}
        			}
        		}
        	}
    		out.add(secondCourseList);
    	}
    	System.out.println("ArrayList arraylist size: " + out.size());
    	return out; 	
    }
    
    public static void main(String[] args) {
    	ArrayList<String> courses = new ArrayList<>(); // Store the courses from the GUI
    	ArrayList<Course> finalCourseList = new ArrayList<>();
    	ArrayList<Course> secondCourseList = new ArrayList<>();
    	ArrayList<ArrayList<Course>> out = new ArrayList<>();
    	
    	// Create the arraylist of selected courses
    	//courses = getCC();
    	
    	// Testing
    	
    	courses.add("ACC3100 - Intermediate Accounting II");
    	courses.add("ACC2100 - Accounting Principles II");
    	courses.add("ACC2000 - Accounting Principles I");
    	courses.add("ACC3500 - Managerial/Cost Accounting I");
    	courses.add("ACC4600 - Advanced Tax Topics");
    	
    	out = build(courses, "Spring 2020");
    	finalCourseList = out.get(0);
    	//secondCourseList = out.get(1);
    	
    	System.out.println("Current Course Size: " + currentCourse.size());
    	for(int i = 0; i < currentCourse.size(); i++) {
    		System.out.println(currentCourse.get(i));
    	}
    	    	    	
    	System.out.println(finalCourseList.size());
    	for(int i = 0; i < finalCourseList.size(); i++) {
    		System.out.println(finalCourseList.get(i));
    	}
    	
    	System.out.println(secondCourseList.size());
    	for(int i = 0; i < secondCourseList.size(); i++) {
    		System.out.println(secondCourseList.get(i));
    	}
    	
    	// Build schedule to GUI?   	
    	return;
    }
}
