import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

public class ScheduleMaker {
    ArrayList<Course> currentCourse = new ArrayList<>();
	
	// Get Course code from GUI
	private String getCC() {
    	return null;
    }
    
    // Find Course code in CouseList
    public String findCC( String courseName, String semesterID ) {
    	try {
    		MultiMap<String, Course> allCourses = Scraper.getAllClasses(semesterID);
    		currentCourse.addAll(allCourses.get(courseName));
    	} catch ( ParseException | IOException ex ) {
    		System.out.println("Error PaseException, IOException");
    		System.exit(0);
    	}
    	
    	
    	return null;
    }
    
    public static void main(String[] args) {
    	
    }
}
