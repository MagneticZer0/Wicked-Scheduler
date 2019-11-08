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
    				continue;
    			}
    		}
    	}
    	
    	// This array will store the number of course repeats in order of sorted appearance
    	int[] arr = new int[numCourses];
    	int ind = 0;
    	for(int i = 0; i < numCourses; i++) {
    		arr[i] = 1; // At least one

    		while(currentCourse.get(ind).toString().equals(currentCourse.get(ind + 1).toString())) {
    			arr[i]++;
    			ind++;
    			if(ind < currentCourse.size()) {
    				break;
    			}
    		}
    		
    		ind++;
    	}    	
    	
    	// Build schedule
    	// Go through the number of courses to have
    	for(int i = 0; i < numCourses; i++) {
    		// Go through the multiple times of that class
    		for(int j = 0; j < arr[i]; j++) {
    			// Add the first class
    			if(i == 0) {
    				firstCourseList.add(currentCourse.get(i+j));
    			} else {
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
    	
    	// Make a second schedule
    	if( numCourses < currentCourse.size() ) {
    		// Multiple courses
    		for(int i = 0; i < numCourses; i++) {
        		// Go through the multiple times of that class
        		for(int j = (arr[i] - 1); j >= 0; j--) {
        			//System.out.println(currentCourse.get(i + j));
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
    	
    	courses.add("CS3411 - Systems Programming");
    	courses.add("EE3131 - Electronics");
    	courses.add("ACC2000 - Accounting Principles I");
    	
    	for(int j = 0; j < courses.size(); j++) {
    		//System.out.println(Scraper.getAllSemesters().toString());
			findCC( courses.get(j), Scraper.getAllSemesters().get("Fall 2019"));
    	}
    	// S
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
    	
    	// This array will store the number of course repeats in order of sorted appearance
    	int[] arr = new int[numCourses];
    	int ind = 0;
    	for(int i = 0; i < numCourses; i++) {
    		arr[i] = 1; // At least one

    		while(currentCourse.get(ind).toString().equals(currentCourse.get(ind + 1).toString())) {
    			arr[i]++;
    			ind++;
    			if(ind < currentCourse.size()) {
    				break;
    			}
    		}
    		
    		ind++;
    	}    	
    	
    	// Build schedule
    	// Go through the number of courses to have
    	for(int i = 0; i < numCourses; i++) {
    		// Go through the multiple times of that class
    		for(int j = 0; j < arr[i]; j++) {
    			//System.out.println(currentCourse.get(i + j));
    			// Add the first class
    			if(i == 0) {
    				finalCourseList.add(currentCourse.get(i+j));
    			} else {
    				// Add other classes
    				
    				if(finalCourseList.get( i - 1 ).conflicts(currentCourse.get(i + j + arr[i - 1]))) {
    					// Conflict go to next option
    					if(j == arr[i] - 1) {
    						// If on the last option of a class and can't add it ERROR
    						System.out.println("Error incombatable course: " + currentCourse.get(i + j));
    					}
    					continue;
    				} else {
    					
    					//System.out.println(i);
    					finalCourseList.add(currentCourse.get(i + j + arr[i - 1]));
    					break;
    				}
    			}
    		}
    	}
    	out.add(finalCourseList);
    	
    	//**
    	System.out.println("Second");
    	if( numCourses < currentCourse.size() ) {
    		// Multiple courses
    		for(int i = 0; i < numCourses; i++) {
        		// Go through the multiple times of that class
        		for(int j = (arr[i] - 1); j >= 0; j--) {
        			//System.out.println(currentCourse.get(i + j));
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
    	}//**/
    	    	    	
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
