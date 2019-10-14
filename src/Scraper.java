import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Pattern;

/**
 * 
 * @author Harley Merkaj
 *
 */
public class Scraper {

	private static final String COURSE_SELECT_URL = "https://www.banweb.mtu.edu/pls/owa/bzskfcls.p_sel_crse_search";
	private static final String CATEGORY_SELECT_URL = "https://www.banweb.mtu.edu/owassb/bwckgens.p_proc_term_date";
	private static final String CLASS_LIST_URL = "https://www.banweb.mtu.edu/owassb/bzckschd.p_get_crse_unsec";
	private static final String DUMMY_VALUE = "dummy";
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36";
	private static ArrayList<Class> classes = new ArrayList<>();

	public static void main(String[] args) throws IOException, ParseException {
		//System.out.println(getAllSemesters());
		//System.out.println(getEditableSemesters());
		//System.out.println(getCategories("201905"));
		System.out.println(getAllClasses("201905").toString());
	}

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

	public static Map<String, String> getEditableSemesters() throws IOException {
		Map<String, String> output = getAllSemesters();
		output.keySet().removeIf(e -> e.contains("View only"));
		return output;
	}

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

	public static List<Class> getAllClasses(String semesterID) throws IOException, ParseException {
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
		Class previousClass = null;
		while ((inputLine = in.readLine()) != null) {
			String inputLineLower = inputLine.toLowerCase();
			if (!searching) {
				searching = inputLineLower.contains("summary=\"this layout table is used to present the sections found\"");
			} else {
				if (inputLineLower.contains("summary=\"this is for formatting of the bottom links.\"")) {
					break;
				} else if (!inputLineLower.contains("</th>")){
					if (!inRow) {
						inRow = inputLineLower.contains("<tr>");
					} else {
						if (inputLineLower.contains("</tr>")) {
							inRow = false;
							String[] classInfo = input.split("\\|");
							if (classInfo.length > 10) { // Because there's some classes that happen multiple times a day, or at different times on different days.
								if (classInfo[0].trim().isEmpty()) {
									previousClass.addDayandTime(classInfo[7] + "|" + classInfo[8]);
								} else {
									previousClass = new Class(classInfo[0], classInfo[1], classInfo[2], null, classInfo[6], classInfo[7], classInfo[8], classInfo[11], classInfo[12], classInfo[13] + "|" + year, 0);
									classes.add(previousClass);
								}
							}
							input = "";
						} else {
							String value = getInternalValue(inputLine).replaceAll("\n", "").replaceAll("&nbsp;", "").trim() + "|";
							if (inputLine.contains("colspan")) { // Because for some reason they just colspan TBA for days/time
								int loops = Integer.parseInt(inputLine.split("colspan=\"")[1].split("\"")[0]);
								for (int i=0; i<loops-1; i++) {
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
		return classes;
	}

	private static BufferedReader getWebPage(String url) throws IOException {
		// Add caching?
		URL link = new URL(url);
		URLConnection connection = link.openConnection();
		connection.setRequestProperty("User-Agent", USER_AGENT); // Make the website think that you're using Chrome
		// Maybe add a timeout somewhere

		return new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)); // Reading the source
	}

	private static BufferedReader sendWebForm(String url, Map<String, String> arguments) throws IOException {
		StringJoiner argJoiner = new StringJoiner("&");
		for (Map.Entry<String, String> argument : arguments.entrySet()) {
			joinerHelper(argJoiner, argument.getKey(), argument.getValue());
		}
		return sendWebForm(url, argJoiner);
	}

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

	private static StringJoiner joinerHelper(StringJoiner joiner, String key, String value) throws UnsupportedEncodingException {
		return joiner.add(URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8"));
	}

	private static String getInternalValue(String str) {
		String[] split = str.split(">");
		return split[split.length/2].split("<")[0];
	}
}
