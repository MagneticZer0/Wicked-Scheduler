import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Pattern;

import collections.MultiMap;

/**
 * The Scraper is what does all the data scraping it will access multiple links
 * through the use of web forms and take the information needed from each
 * website and output it into various Java collection classes.
 * 
 * @author Harley Merkaj
 *
 */
public class Scraper {

	/**
	 * The initial URL, this asks you to choose a term
	 */
	private static final String COURSE_SELECT_URL = "https://www.banweb.mtu.edu/pls/owa/bzskfcls.p_sel_crse_search";
	/**
	 * THe secondary page, this asks you to choose a category
	 */
	private static final String CATEGORY_SELECT_URL = "https://www.banweb.mtu.edu/owassb/bwckgens.p_proc_term_date";
	/**
	 * The tertiary page, this display all classes available for the category choosen.
	 */
	private static final String CLASS_LIST_URL = "https://www.banweb.mtu.edu/owassb/bzckschd.p_get_crse_unsec";
	/**
	 * Dummy value used for the web form
	 */
	private static final String DUMMY_VALUE = "dummy";
	/**
	 * Use agent used when visiting the website, this makes it think we're using Chrome.
	 */
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36";
	/**
	 * The output of the getAllClasses method
	 */
	public static MultiMap<String, Course> courses = new MultiMap<>();

//	public static void main(String[] args) throws IOException, ParseException {
//		System.out.println(getAllSemesters().toString() + "\n");
//		getAllClasses(getAllSemesters().get("Fall 2001 (View only)")); // Returns all classes for the Fall 2001 semester
//		for(Map.Entry<String, List<Course>> entry : courses.entrySet()) {
//			System.out.println(entry.toString());
//		}
//		new ExampleOutputSaver().saveCourses(courses); // Save output
//	}

	/**
	 * Accesses the COURSE_SELECT_URL link and takes all the semesters that Michigan Tech offers
	 * @return A Hashmap of semester names to semester IDs.
	 * @throws IOException If something goes wrong accessing the website.
	 */
	public static Map<String, String> getAllSemesters() throws IOException {
		BufferedReader in = getWebPage(COURSE_SELECT_URL); // Reading the source

		boolean searching = false;
		HashMap<String, String> output = new HashMap<>();

		String regex = "<OPTION VALUE=\".*?\">";
		Pattern regexPattern = Pattern.compile(regex);

		String inputLine = null;
		while ((inputLine = in.readLine()) != null) {
			if (inputLine.contains("term_input_id") && inputLine.contains("p_term")) {
				searching = true;
			} else if (searching) {
				if (regexPattern.matcher(inputLine).lookingAt()) {
					output.put(inputLine.split("<OPTION VALUE=\".*?>")[1].split("</OPTION>")[0], inputLine.split("<OPTION VALUE=\"")[1].split("\">")[0]);
				} else {
					break;
				}
			}
		}
		in.close();
		return output;
	}

	/**
	 * Does what getAllSemesters does but only returns the ones that don't contain (View only)
	 * @return A hashmap of semester names to semester IDs.
	 * @throws IOException If something goes wrong accessing the website.
	 */
	public static Map<String, String> getEditableSemesters() throws IOException {
		Map<String, String> output = getAllSemesters();
		output.keySet().removeIf(e -> e.contains("View only"));
		return output;
	}

	/**
	 * Gets all categories within a given semster by searching it by the semester ID.
	 * @param semesterID The semester ID for the semester, this is given by the map given by {@link getAllSemesters()}
	 * @return Returns a list of available categories.
	 * @throws IOException If something goes wrong accessing the website.
	 */
	public static List<String> getCategories(String semesterID) throws IOException {
		HashMap<String, String> arguments = new HashMap<>();
		arguments.put("p_calling_proc", "bzskfcls.P_CrseSearch");
		arguments.put("p_term", semesterID);

		BufferedReader in = sendWebForm(CATEGORY_SELECT_URL, arguments);

		boolean searching = false;
		ArrayList<String> output = new ArrayList<>(100);

		String regex = "<OPTION VALUE=\".*?\">";
		Pattern regexPattern = Pattern.compile(regex);

		String inputLine = null;
		while ((inputLine = in.readLine()) != null) {
			if (inputLine.contains("sel_subj") && inputLine.contains("subj_id")) {
				searching = true;
			} else if (searching) {
				if (regexPattern.matcher(inputLine).lookingAt()) {
					output.add(inputLine.split("<OPTION VALUE=\"")[1].split("\">")[0]);
				} else {
					break;
				}
			}
		}
		in.close();
		return output;
	}

	/**
	 * Returns all classes of all categories within a given semester by the semester ID.
	 * @param semesterID The semester Id for the semester, this is given by the map given by {@link getAllSemesters()} 
	 * @return Returns a list of Class objects for the given semester
	 * @throws IOException If something goes wrong accessing the website.
	 * @throws ParseException If something goes wrong parsing the website.
	 */
	public static MultiMap<String, Course> getAllClasses(String semesterID) throws IOException, ParseException {
		List<String> categories = getCategories(semesterID);

		String year = semesterID.substring(0, 4);

		StringJoiner argJoiner = new StringJoiner("&");
		joinerHelper(argJoiner, "term_in", semesterID);
		joinerHelper(argJoiner, "sel_subj", DUMMY_VALUE);
		joinerHelper(argJoiner, "sel_day", DUMMY_VALUE);
		joinerHelper(argJoiner, "sel_schd", DUMMY_VALUE);
		joinerHelper(argJoiner, "sel_insm", DUMMY_VALUE);
		joinerHelper(argJoiner, "sel_camp", DUMMY_VALUE);
		joinerHelper(argJoiner, "sel_levl", DUMMY_VALUE);
		joinerHelper(argJoiner, "sel_sess", DUMMY_VALUE);
		joinerHelper(argJoiner, "sel_instr", DUMMY_VALUE);
		joinerHelper(argJoiner, "sel_ptrm", DUMMY_VALUE);
		joinerHelper(argJoiner, "sel_attr", DUMMY_VALUE);
		for (String category : categories) {
			joinerHelper(argJoiner, "sel_subj", category);
		}
		joinerHelper(argJoiner, "sel_crse", "");
		joinerHelper(argJoiner, "sel_title", "");
		joinerHelper(argJoiner, "sel_schd", "%");
		joinerHelper(argJoiner, "sel_from_cred", "");
		joinerHelper(argJoiner, "sel_to_cred", "");
		joinerHelper(argJoiner, "sel_camp", "%");
		joinerHelper(argJoiner, "sel_levl", "%");
		joinerHelper(argJoiner, "sel_ptrm", "%");
		joinerHelper(argJoiner, "sel_instr", "%");
		joinerHelper(argJoiner, "sel_attr", "%");
		joinerHelper(argJoiner, "begin_hh", "0");
		joinerHelper(argJoiner, "begin_mi", "0");
		joinerHelper(argJoiner, "begin_ap", "a");
		joinerHelper(argJoiner, "end_hh", "0");
		joinerHelper(argJoiner, "end_mi", "0");
		joinerHelper(argJoiner, "end_ap", "a");

		BufferedReader in = sendWebForm(CLASS_LIST_URL, argJoiner);

		String input = "";
		String inputLine = null;
		boolean searching = false;
		boolean inRow = false;
		Course previousClass = null;
		while ((inputLine = in.readLine()) != null) {
			String inputLineLower = inputLine.toLowerCase();
			if (!searching) {
				searching = inputLineLower.contains("summary=\"this layout table is used to present the sections found\"");
			} else {
				if (inputLineLower.contains("summary=\"this is for formatting of the bottom links.\"")) {
					break;
				} else if (!inputLineLower.contains("</th>")) {
					if (!inRow) {
						inRow = inputLineLower.contains("<tr>");
					} else {
						if (inputLineLower.contains("</tr>")) {
							inRow = false;
							String[] classInfo = input.split("\\|");
							if (classInfo.length > 10) { // Because there's some classes that happen multiple times a day, or at
														 // different times on different days.
								if (classInfo[0].trim().isEmpty() && previousClass != null) {
									previousClass.addDayandTime(classInfo[7] + "|" + classInfo[8]);
								} else {
									double fee = 0;
									if (classInfo.length > 15) {
										for(int i=classInfo.length-1; i>15; i--) {
											String[] dollarSplit = classInfo[i].split("\\$");
											fee += NumberFormat.getInstance().parse(dollarSplit[1]).doubleValue();
										}
									}
									ArrayList<Double> credits = new ArrayList<>();
									String[] credSplit = classInfo[5].split("-");
									for(String s : credSplit) {
										credits.add(Double.parseDouble(s));
									}
									previousClass = new Course(classInfo[0], classInfo[1], classInfo[2], classInfo[3].contains("L"), credits, classInfo[6], classInfo[7], classInfo[8], classInfo[11], classInfo[12], classInfo[13] + "|" + year, fee);
									courses.put(previousClass.toString(), previousClass);
								}
							}
							input = "";
						} else {
							String value = getInternalValue(inputLine).replaceAll("\n", "").replaceAll("&nbsp;", "").trim() + "|";
							if (inputLine.contains("colspan")) { // Because for some reason they just colspan TBA for days/time
								int loops = Integer.parseInt(inputLine.split("colspan=\"")[1].split("\"")[0]);
								for (int i = 0; i < loops - 1; i++) {
									input += value;
								}
							}
							input += value;
						}
					}

				}
			}
		}
		in.close();
		return courses;
	}

	/**
	 * Returns a {@link java.io.BufferedReader} for a given webpage, can be used for reading the source of the page
	 * @param url The URL to get the BufferedReader for.
	 * @return Returns the BufferedReader for that website.
	 * @throws IOException If something geso wrong accessing the website.
	 */
	private static BufferedReader getWebPage(String url) throws IOException {
		// Add caching?
		URL link = new URL(url);
		URLConnection connection = link.openConnection();
		connection.setRequestProperty("User-Agent", USER_AGENT); // Make the website think that you're using Chrome
		// Maybe add a timeout somewhere

		return new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)); // Reading the source
	}

	/**
	 * Sends a web form to the website with a given map of arguments
	 * @param url The URL to send the web form to
	 * @param arguments The map of arguments to send within the web form
	 * @return Returns a BufferedReader, tpyically for reading the source of the page
	 * @throws IOException If something geos wrong accessing the website.
	 */
	private static BufferedReader sendWebForm(String url, Map<String, String> arguments) throws IOException {
		StringJoiner argJoiner = new StringJoiner("&");
		for (Map.Entry<String, String> argument : arguments.entrySet()) {
			joinerHelper(argJoiner, argument.getKey(), argument.getValue());
		}
		return sendWebForm(url, argJoiner);
	}

	/**
	 * Sends a web form to the website with a given StringJoiner
	 * @param url The URL to send the web form to
	 * @param arguments The StringJoiner of arguments to send within the web form
	 * @return Returns a BufferedReader, tpyically for reading the source of the page
	 * @throws IOException If something geos wrong accessing the website.
	 */
	private static BufferedReader sendWebForm(String url, StringJoiner arguments) throws IOException {
		byte[] form = arguments.toString().getBytes(StandardCharsets.UTF_8);

		URL link = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) link.openConnection();

		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setFixedLengthStreamingMode(form.length);
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=" + StandardCharsets.UTF_8);
		connection.connect();
		connection.getOutputStream().write(form);

		return new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)); // Reading the source
	}

	/**
	 * A helper function used to create the web form for getting all classes within a semester
	 * @param joiner The joiner to use
	 * @param key The key for the argument
	 * @param value The value for the argument
	 * @return Returns that StringJoiner
	 * @throws UnsupportedEncodingException If something goes wrong when encoding the strings
	 */
	private static StringJoiner joinerHelper(StringJoiner joiner, String key, String value) throws UnsupportedEncodingException {
		return joiner.add(URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8"));
	}

	/**
	 * Gets the values within tags.
	 * @param str The line to get the tag for
	 * @return Returns the most inner tag
	 */
	private static String getInternalValue(String str) {
		String[] split = str.split(">");
		return split[split.length / 2].split("<")[0];
	}
}
