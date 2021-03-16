package rawquestiontransformation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import model.persisted.Question;

import view.Constants;

/**
 * This class is used to perform the preliminary transformation of online-sourced multiple-choice questions, in order to
 * make them usable by this system.
 *
 * An example of a raw question:
 * 
 * The first milk produced by a woman in the first few days after giving birth is called:
 * 
 * 0 formula
 * 
 * 0 enrichment
 * 
 * 1 colostrum
 * 
 * 0 sputum
 * 
 * 0 amniocentesis
 * 
 * The answers must be turned into A-D (deleting one of the incorrect ones if > 4) and recording the correct one,
 * represented by '1' in this case.
 *
 * @author Sam Barba
 */
public class RawQuestionTransformer {

	private static final String INPUT_FILE_PATH = "C:\\Users\\Sam Barba\\Desktop\\EuropeHistory.txt";

	private static Map<String, List<String>> questionsAndAnswers = new HashMap<>();

	private List<Question> questions = new ArrayList<>();

	public static void main(String[] args) throws FileNotFoundException {
		File inputFile = new File(INPUT_FILE_PATH);
		Scanner input = new Scanner(inputFile);

		List<String> lines = new ArrayList<>();

		while (input.hasNextLine()) {
			String line = input.nextLine()
				.replace(Constants.QUOT_MARK, "'")
				.replace(Constants.NEWLINE, Constants.EMPTY)
				.trim();

			if (line.length() > 0) {
				lines.add(line);
			}
		}
		input.close();

		populateQuestions(lines);
	}

	private static void populateQuestions(List<String> rawLines) {
		String question = "";

		for (String line : rawLines) {
			char firstChar = line.charAt(0);

			if (firstChar != '0' && firstChar != '1') { // if line is a question
				// add new question entry to map
				question = line;
				questionsAndAnswers.put(question, new ArrayList<String>());
			} else {
				// add possible answer to correct question in map
				questionsAndAnswers.get(question).add(line);
			}
		}

		// find shortest question and longest question in order to determine difficulty level and time required later
		int minLength = Integer.MAX_VALUE, maxLength = 0;
		for (String questionKey : questionsAndAnswers.keySet()) {
			int len = questionKey.length();
			if (len < minLength) {
				minLength = len;
			}
			if (len > maxLength) {
				maxLength = len;
			}
		}

		int id = 1;
		for (Entry<String, List<String>> entry : questionsAndAnswers.entrySet()) {

		}
	}

	private static double map(double x, double r1start, double r1end, double r2start, double r2end) {
		return (x - r1start) * (r2end - r2start) / (r1end - r1start) + r2start;
	}
}
