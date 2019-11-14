import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import collections.MultiMap;

public class ScheduleMaker {
	
    private static MultiMap<String, Course> allCourses;
    private static String semesterID;
	     
    /**
     * This method will retrieve the Course from the given name and semester and add it to the 
     * global ArrayList
     * @author Alex Hare
     * @param courseName - The name of the course to be searched for
     * @param semesterID - Semester to search in
     */
    public static Set<Course> findCourses( String courseName ) {
    	
    	Set<Course> possibleCourses = new HashSet<>(); // Holds all courses at various times
    	
    	try {
    		// load all courses if needed
    		if( allCourses == null ) {
    			Scraper.loadCourses();
    			allCourses = Scraper.getAllClasses(semesterID);
    		}    		
    		
    		// add all the inputed course to possibleCourses
    		possibleCourses.addAll(allCourses.get(courseName));
    		
    	} catch ( IOException ex ) {
    		System.out.println("Error PaseException, IOException");
    		System.exit(0);
    	}
    	
    	return possibleCourses;
    }
    
    public static Set<Set<Course>> build(Set<String> desiredCourses, String semester) {
    	    	
    	System.out.println("BUILD CALLED!!!");
    	
    	semesterID = semester;
    	
    	/*
    	 * a list of schedules ( list of lists ) 
    	 * 
    	 * for each desired course:
    	 *   for each section in the desired course:
    	 *     for each schedule in the list:
    	 *       for each course in the schedule:
    	 *         if there is a conflict
    	 *           remove the schedule from the list of schedules
    	 *           break break;
    	 *       add the section to the current schedule
    	 *     
    	 */
    	
    	Set<Course> possibleCourses = new HashSet<>();
    	Set<Set<Course>> validSchedules = new HashSet<>(); // contains a list of schedules ( each "schedule" is a list of courses that do not conflict )
    	Set<Course> tempSchedule = new HashSet<>();
    	Set<Set<Course>> tempValids;
    	
    	for ( String courseCode : desiredCourses ) {
    		System.out.println("  HANDLING " + courseCode );
    		possibleCourses = findCourses(courseCode);
    		
    		// if there are no pre-existing schedules
    		if ( validSchedules.isEmpty() ) {
    			
    			System.out.println("  THERE ARE NO PRE-EXISTING SCHEDULES");
    			for ( Course possibleCourse : possibleCourses ) {
    				System.out.println("    ADDED FIRST CLASS " + possibleCourse + " AT TIME " + possibleCourse.getTimes("M").toString() + possibleCourse.getTimes("T").toString() );
    				tempSchedule = new HashSet<>();
    				tempSchedule.add(possibleCourse);
    				validSchedules.add(tempSchedule);
    			}
    		} else {
    			
    			tempValids = new HashSet<>();
    			System.out.println("  THERE ARE PRE-EXISTING SCHEDULES");
    			for ( Course possibleCourse : possibleCourses ) {
    				System.out.println("      possibleCourse = " + possibleCourse + " AT TIME " + possibleCourse.getTimes("M").toString() + possibleCourse.getTimes("T").toString());
    				forEachSchedule:
    				for ( Set<Course> schedule : validSchedules ) {
    					tempSchedule = new HashSet<>();
    					for ( Course existingCourse : schedule ) {
    						if ( possibleCourse.conflicts(existingCourse) ) {
    							System.out.println("    CONFLICT!!!");
    							//validSchedules.remove(schedule);
    							break forEachSchedule;
    						}
    						//System.out.println(possibleCourse.toString());
    						//System.out.println(existingCourse.toString());
    						if ( possibleCourse.toString().equals(existingCourse.toString()) ) {
    							System.out.println("    DUPLICATE!!!");
    							break forEachSchedule;
    						}
    					}
    					tempSchedule.addAll(schedule);
    					tempSchedule.add(possibleCourse);
    					tempValids.add(tempSchedule);
    					System.out.println("      ADDED " + possibleCourse + " AT TIME " + possibleCourse.getTimes("M").toString() + possibleCourse.getTimes("T").toString() );
    				}  
    			}
    			
    			validSchedules.addAll(tempValids);
    		}
    	}
    	
    	Set<Set<Course>> finalSchedules = new HashSet<>();
    	
    	System.out.println("SIZE BEFORE PURGE: " + validSchedules.size() );
    	System.out.println("Schedules:");
    	for ( Set<Course> schedule : validSchedules ) {
    		if ( schedule.size() == desiredCourses.size() ) {
    			finalSchedules.add(schedule);
    			System.out.println(schedule.toString());    			
    		}
    		
    	}
    	
    	System.out.println("SIZE AFTER PURGE: " + finalSchedules.size() );
    	
		return finalSchedules;
    }
    
    public static void main(String[] args) {
    	Set<String> desiredCourses = new HashSet<>();
    	
    	// first testing
    	//desiredCourses.add("ACC2000 - Accounting Principles I");
    	//desiredCourses.add("ACC2100 - Accounting Principles II");
    	
    	// these courses produce inconsistent behavior
    	// when debugging, size 6 before purge, size 0 after purge
    	// when running, size 10 before purge, size 1 after purge
    	desiredCourses.add("CS3000 - Ethical/Social Aspects of Comp");    	
    	desiredCourses.add("CS3331 - Concurrent Computing");
    	desiredCourses.add("CS3411 - Systems Programming");
    	desiredCourses.add("CS3712 - Software Quality Assurance");
    	
    	ScheduleMaker.build(desiredCourses, Scraper.getAllSemesters().get("Spring 2020"));
    	return;
    }
}
