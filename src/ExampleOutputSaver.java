import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class ExampleOutputSaver {
	
	String file = System.getProperty("user.home") + "\\Wicked-Scheduler\\" + "courses.ser";

	public void setup() throws IOException {
		File directory = new File(System.getProperty("user.home") + "\\Wicked-Scheduler\\");
		if (!directory.exists()) {
			directory.mkdir();
		}

		File courses = new File(System.getProperty("user.home") + "\\Wicked-Scheduler\\" + "courses.ser");
		if (courses.exists()) {
			courses.delete(); // Delete only if old later
			courses.createNewFile();
		}
	}

	public void saveCourses(List<Course> courses) throws FileNotFoundException, IOException {
		setup();

		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));

		out.writeObject(courses);

		out.close();
	}

	public List<Course> loadCourses() throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));

		List<Course> courses = (List<Course>) in.readObject();

		in.close();
		return courses;
	}
}
