package view.utils;

/**
 * This class is used to format subject titles or question paper titles etc.
 *
 * @author Sam Barba
 */
public class StringFormatter {

	/**
	 * Capitalise each word in a title (e.g. for a subject or question paper) and trim whitespace.
	 * 
	 * @param title - the title to format
	 * @return formatted title
	 */
	public static String formatTitle(String title) {
		if (title.trim().isEmpty()) {
			return title;
		}

		// remove characters that could potentially harm read/write functionality
		title = title.replace("\n", Constants.EMPTY).replace(Constants.QUOT_MARK, "'");

		String[] words = title.trim().split(" ");
		StringBuilder result = new StringBuilder();
		for (String word : words) {
			result.append(capitalise(word) + " ");
		}
		return result.toString().trim(); // remove last space
	}

	/**
	 * Capitalise a string.
	 * 
	 * @param s - the string to capitalise
	 * @return the capitalised string
	 */
	public static String capitalise(String s) {
		return s.length() == 1
			? s.toUpperCase()
			: Character.toString(s.charAt(0)).toUpperCase() + s.substring(1);
	}

	private StringFormatter() {
	}
}
