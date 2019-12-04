import java.io.IOException;
import java.util.HashSet;
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
    	    	
    	final long startTime = System.currentTimeMillis();
    	// System.out.println("BUILD CALLED!!!"); // FOR TESTING
    	
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
    	Set<Set<Course>> toAdd;
    	Set<Set<Course>> toRemove;
    	boolean conflict;
    	int courseAdditionCount = 0;
    	
    	for ( String courseCode : desiredCourses ) {
    		// System.out.println("  HANDLING " + courseCode ); // FOR TESTING
    		possibleCourses = findCourses(courseCode);
    		
    		// if there are no pre-existing schedules
    		if ( validSchedules.isEmpty() ) {
    			
    			// System.out.println("  THERE ARE NO PRE-EXISTING SCHEDULES"); // FOR TESTING
    			// System.out.println(possibleCourses); // FOR TESTING
    			for ( Course possibleCourse : possibleCourses ) {
    				// System.out.println("    ADDED FIRST CLASS " + possibleCourse + " AT TIME " + possibleCourse.getTimes("M").toString() + possibleCourse.getTimes("T").toString() ); // FOR TESTING
    				tempSchedule = new HashSet<>();
    				tempSchedule.add(possibleCourse);
    				validSchedules.add(tempSchedule);
    			}
    		} else {
    			
    			toAdd = new HashSet<>();
    			toRemove = new HashSet<>();
    			// System.out.println("  THERE ARE PRE-EXISTING SCHEDULES"); // FOR TESTING
    			for ( Course possibleCourse : possibleCourses ) {
    				// System.out.println("      possibleCourse = " + possibleCourse + " AT TIME " + possibleCourse.getTimes("M").toString() + possibleCourse.getTimes("T").toString()); // FOR TESTING
    				for ( Set<Course> schedule : validSchedules ) {
    					
    					if ( schedule.size() != courseAdditionCount ) {
    						toRemove.add(schedule);
    					}
    					
    					tempSchedule = new HashSet<>();
    					conflict = false;
    					for ( Course existingCourse : schedule ) {
    						if ( possibleCourse.conflicts(existingCourse) ) {
    							// System.out.println("    CONFLICT WITH SCHEDULE " + schedule.toString()); // FOR TESTING
    							conflict = true;
    							break;
    						}
    						//System.out.println(possibleCourse.toString());
    						//System.out.println(existingCourse.toString());
    						//if ( possibleCourse.toString().equals(existingCourse.toString()) ) {
    						//	  System.out.println("    DUPLICATE!!!");
    						//	  break;
    						//}
    					}
    					
    					if (conflict) {
    						continue;
    					}
    					
    					tempSchedule.addAll(schedule);
    					tempSchedule.add(possibleCourse);
    					toAdd.add(tempSchedule);
    					// System.out.println("      ADDED " + possibleCourse + " AT TIME " + possibleCourse.getTimes("M").toString() + possibleCourse.getTimes("T").toString() ); // FOR TESTING
    				}  
    			}
    			
    			validSchedules.removeAll(toRemove);
    			validSchedules.addAll(toAdd);
    		}
    		courseAdditionCount++;
    	}
    	
    	Set<Set<Course>> finalSchedules = new HashSet<>();
    	
    	// System.out.println("SIZE BEFORE PURGE: " + validSchedules.size() ); // FOR TESTING
    	// System.out.println("Schedules:"); // FOR TESTING
    	for ( Set<Course> schedule : validSchedules ) {
    		// System.out.println(schedule.toString()); // FOR TESTING
    		if ( schedule.size() == desiredCourses.size() ) {
    			finalSchedules.add(schedule);
    			// System.out.println("     FINAL:" + schedule.toString()); // FOR TESTING		
    		}
    		
    	}
    	
    	// System.out.println("SIZE AFTER PURGE: " + finalSchedules.size() ); // FOR TESTING
    	
    	final long endTime = System.currentTimeMillis();
    	System.out.println("Total execution time: " + (endTime-startTime) + "ms");
    	
		return finalSchedules;
    }
    
    public static void main(String[] args) {

    }
}
